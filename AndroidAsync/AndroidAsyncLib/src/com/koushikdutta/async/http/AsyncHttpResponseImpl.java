package com.koushikdutta.async.http;

import com.koushikdutta.async.*;
import com.koushikdutta.async.LineEmitter.StringCallback;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.WritableCallback;
import com.koushikdutta.async.http.filter.ChunkedOutputFilter;
import com.koushikdutta.async.http.libcore.RawHeaders;
import com.koushikdutta.async.http.libcore.ResponseHeaders;

import java.nio.ByteBuffer;

abstract class AsyncHttpResponseImpl extends FilteredDataEmitter implements AsyncHttpResponse {
    StringCallback mHeaderCallback = new StringCallback() {
        private RawHeaders mRawHeaders = new RawHeaders();

        @Override
        public void onStringAvailable(String s) {
            try {
                if (mRawHeaders.getStatusLine() == null) {
                    mRawHeaders.setStatusLine(s);
                } else if (!"\r".equals(s)) {
                    mRawHeaders.addLine(s);
                } else {
                    mHeaders = new ResponseHeaders(mRequest.getUri(), mRawHeaders);
                    onHeadersReceived();
                    // socket may get detached after headers (websocket)
                    if (mSocket == null)
                        return;
                    DataEmitter emitter = Util.getBodyDecoder(mSocket, mRawHeaders, false, mReporter);
                    setDataEmitter(emitter);
                }
            } catch (Exception ex) {
                report(ex);
            }
        }
    };
    ResponseHeaders mHeaders;
    boolean mCompleted = false;
    DataSink mSink;
    private AsyncHttpRequestBody mWriter;
    private CompletedCallback mReporter = new CompletedCallback() {
        @Override
        public void onCompleted(Exception error) {
            if (error != null && !mCompleted) {
                report(new Exception("connection closed before response completed."));
            } else {
                report(error);
            }
        }
    };
    private AsyncHttpRequest mRequest;
    private AsyncSocket mSocket;
    private boolean mFirstWrite = true;

    public AsyncHttpResponseImpl(AsyncHttpRequest request) {
        mRequest = request;
    }

    public AsyncSocket getSocket() {
        return mSocket;
    }

    void setSocket(AsyncSocket exchange) {
        mSocket = exchange;

        if (mSocket == null)
            return;

        mWriter = mRequest.getBody();
        if (mWriter != null) {
            mRequest.getHeaders().setContentType(mWriter.getContentType());
            if (mWriter.length() != -1) {
                mRequest.getHeaders().setContentLength(mWriter.length());
                mSink = mSocket;
            } else {
                mRequest.getHeaders().getHeaders().set("Transfer-Encoding", "Chunked");
                mSink = new ChunkedOutputFilter(mSocket);
            }
        } else {
            mSink = mSocket;
        }

        String rs = mRequest.getRequestString();
        com.koushikdutta.async.Util.writeAll(exchange, rs.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (mWriter != null)
                    mWriter.write(mRequest, AsyncHttpResponseImpl.this);
            }
        });

        LineEmitter liner = new LineEmitter();
        exchange.setDataCallback(liner);
        liner.setLineCallback(mHeaderCallback);

        mSocket.setEndCallback(mReporter);
        mSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                // TODO: do we care? throw if socket is still writing or something?
            }
        });
    }

    protected abstract void onHeadersReceived();

    @Override
    protected void report(Exception e) {
        super.report(e);

        // DISCONNECT. EVERYTHING.
        // should not get any data after this point...
        // if so, eat it and disconnect.
        mSocket.setDataCallback(new NullDataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                super.onDataAvailable(emitter, bb);
                mSocket.close();
            }
        });
        mSocket.setWriteableCallback(null);
        mSocket.setClosedCallback(null);
        mSocket.setEndCallback(null);
        mCompleted = true;
    }

    @Override
    public ResponseHeaders getHeaders() {
        return mHeaders;
    }

    private void assertContent() {
        if (!mFirstWrite)
            return;
        mFirstWrite = false;
        assert null != mRequest.getHeaders().getHeaders().get("Content-Type");
        assert mRequest.getHeaders().getHeaders().get("Transfer-Encoding") != null || mRequest.getHeaders().getContentLength() != -1;
    }

    @Override
    public void write(ByteBuffer bb) {
        assertContent();
        mSink.write(bb);
    }

    @Override
    public void write(ByteBufferList bb) {
        assertContent();
        mSink.write(bb);
    }

    @Override
    public void end() {
        write(ByteBuffer.wrap(new byte[0]));
    }

    @Override
    public WritableCallback getWriteableCallback() {
        return mSink.getWriteableCallback();
    }

    @Override
    public void setWriteableCallback(WritableCallback handler) {
        mSink.setWriteableCallback(handler);
    }

    @Override
    public boolean isOpen() {
        return mSink.isOpen();
    }

    @Override
    public void close() {
        mSink.close();
    }

    @Override
    public CompletedCallback getClosedCallback() {
        return mSink.getClosedCallback();
    }

    @Override
    public void setClosedCallback(CompletedCallback handler) {
        mSink.setClosedCallback(handler);
    }

    @Override
    public AsyncServer getServer() {
        return mSocket.getServer();
    }
}
