package com.koushikdutta.async;

import android.os.Build;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.WritableCallback;
import com.koushikdutta.async.wrapper.AsyncSocketWrapper;
import org.apache.http.conn.ssl.StrictHostnameVerifier;

import javax.net.ssl.*;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

public class AsyncSSLSocketWrapper implements AsyncSocketWrapper, AsyncSSLSocket {
    AsyncSocket mSocket;
    BufferedDataEmitter mEmitter;
    BufferedDataSink mSink;
    ByteBuffer mReadTmp = ByteBuffer.allocate(8192);
    boolean mUnwrapping = false;

    public AsyncSSLSocketWrapper(AsyncSocket socket, String host, int port) {
        mSocket = socket;

        if (host != null) {
            engine = ctx.createSSLEngine(host, port);
        }
        else {
            engine = ctx.createSSLEngine();
        }
        mHost = host;
        mPort = port;
        engine.setUseClientMode(true);
        mSink = new BufferedDataSink(socket);
        mSink.setMaxBuffer(0);

        // SSL needs buffering of data written during handshake.
        // aka exhcange.setDatacallback
        mEmitter = new BufferedDataEmitter(socket);
//        socket.setDataCallback(mEmitter);

        mEmitter.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                if (mUnwrapping)
                    return;
                try {
                    mUnwrapping = true;
                    
                    ByteBufferList out = new ByteBufferList();

                    mReadTmp.position(0);
                    mReadTmp.limit(mReadTmp.capacity());
                    ByteBuffer b;
                    if (bb.size() > 0) {
                        b = bb.getAll();
                    }
                    else {
                        b = ByteBuffer.allocate(0);
                    }

                    while (true) {
                        int remaining = b.remaining();

                        SSLEngineResult res = engine.unwrap(b, mReadTmp);
                        if (res.getStatus() == Status.BUFFER_OVERFLOW) {
                            addToPending(out);
                            mReadTmp = ByteBuffer.allocate(mReadTmp.remaining() * 2);
                            remaining = -1;
                        }
                        handleResult(res);
                        if (b.remaining() == remaining)
                            break;
                    }

                    if (b.remaining() > 0)
                        bb.add(0, b);

                    addToPending(out);
                    Util.emitAllData(AsyncSSLSocketWrapper.this, out);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    report(ex);
                }
                finally {
                    mUnwrapping = false;
                }
            }
        });
    }
    
    void addToPending(ByteBufferList out) {
        if (mReadTmp.position() > 0) {
            mReadTmp.limit(mReadTmp.position());
            mReadTmp.position(0);
            out.add(mReadTmp);
            mReadTmp = ByteBuffer.allocate(mReadTmp.capacity());
        }
    }

    static SSLContext ctx;
    static {
        // following is the "trust the system" certs setup
        try {
            // critical extension 2.5.29.15 is implemented improperly prior to 4.0.3.
            // https://code.google.com/p/android/issues/detail?id=9307
            // https://groups.google.com/forum/?fromgroups=#!topic/netty/UCfqPPk5O4s
            // certs that use this extension will throw in Cipher.java.
            // fallback is to use a custom SSLContext, and hack around the x509 extension.
            if (Build.VERSION.SDK_INT <= 15)
                throw new Exception();
            ctx = SSLContext.getInstance("Default");
        }
        catch (Exception ex) {
            try {
                ctx = SSLContext.getInstance("TLS");
                TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[] {};
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        for (X509Certificate cert : certs) {
                            cert.getCriticalExtensionOIDs().remove("2.5.29.15");
                        }
                    }
                } };
                ctx.init(null, trustAllCerts, null);
            }
            catch (Exception ex2) {
                ex.printStackTrace();
                ex2.printStackTrace();
            }
        }
    }

    SSLEngine engine;
    boolean finishedHandshake = false;

    private String mHost;
    public String getHost() {
        return mHost;
    }
    
    private int mPort;
    public int getPort() {
        return mPort;
    }
    
    private void handleResult(SSLEngineResult res) {
        if (res.getHandshakeStatus() == HandshakeStatus.NEED_TASK) {
            final Runnable task = engine.getDelegatedTask();
            task.run();
        }
        
        if (res.getHandshakeStatus() == HandshakeStatus.NEED_WRAP) {
            write(ByteBuffer.allocate(0));
        }

        if (res.getHandshakeStatus() == HandshakeStatus.NEED_UNWRAP) {
            mEmitter.onDataAvailable();
        }
        
        try {
            if (!finishedHandshake && (engine.getHandshakeStatus() == HandshakeStatus.NOT_HANDSHAKING || engine.getHandshakeStatus() == HandshakeStatus.FINISHED)) {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init((KeyStore) null);
                boolean trusted = false;
                for (TrustManager tm : tmf.getTrustManagers()) {
                    try {
                        X509TrustManager xtm = (X509TrustManager) tm;
                        peerCertificates = (X509Certificate[]) engine.getSession().getPeerCertificates();
                        xtm.checkServerTrusted(peerCertificates, "SSL");
                        if (mHost != null) {
                            StrictHostnameVerifier verifier = new StrictHostnameVerifier();
                            verifier.verify(mHost, StrictHostnameVerifier.getCNs(peerCertificates[0]), StrictHostnameVerifier.getDNSSubjectAlts(peerCertificates[0]));
                        }
                        trusted = true;
                        break;
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                finishedHandshake = true;
                if (!trusted) {
                    AsyncSSLException e = new AsyncSSLException();
                    report(e);
                    if (!e.getIgnore())
                        throw e;
                }
                assert mWriteableCallback != null;
                mWriteableCallback.onWriteable();
                mEmitter.onDataAvailable();
            }
        }
        catch (Exception ex) {
            report(ex);
        }
    }
    
    private void writeTmp() {
        mWriteTmp.limit(mWriteTmp.position());
        mWriteTmp.position(0);
        if (mWriteTmp.remaining() > 0)
            mSink.write(mWriteTmp);
    }
    
    boolean checkWrapResult(SSLEngineResult res) {
        if (res.getStatus() == Status.BUFFER_OVERFLOW) {
            mWriteTmp = ByteBuffer.allocate(mWriteTmp.remaining() * 2);
            return false;
        }
        return true;
    }

    private boolean mWrapping = false;
    ByteBuffer mWriteTmp = ByteBuffer.allocate(8192);
    @Override
    public void write(ByteBuffer bb) {
        if (mWrapping)
            return;
        if (mSink.remaining() > 0)
            return;
        mWrapping = true;
        int remaining;
        SSLEngineResult res = null;
        do {
            // if the handshake is finished, don't send
            // 0 bytes of data, since that makes the ssl connection die.
            // it wraps a 0 byte package, and craps out.
            if (finishedHandshake && bb.remaining() == 0) {
                mWrapping = false;
                return;
            }
            remaining = bb.remaining();
            mWriteTmp.position(0);
            mWriteTmp.limit(mWriteTmp.capacity());
            try {
                res = engine.wrap(bb, mWriteTmp);
                if (!checkWrapResult(res))
                    remaining = -1;
                writeTmp();
                handleResult(res);
            }
            catch (SSLException e) {
                report(e);
            }
        }
        while ((remaining != bb.remaining() || (res != null && res.getHandshakeStatus() == HandshakeStatus.NEED_WRAP)) && mSink.remaining() == 0);
        mWrapping = false;
    }

    @Override
    public void write(ByteBufferList bb) {
        if (mWrapping)
            return;
        if (mSink.remaining() > 0)
            return;
        mWrapping = true;
        int remaining;
        SSLEngineResult res = null;
        do {
            // if the handshake is finished, don't send
            // 0 bytes of data, since that makes the ssl connection die.
            // it wraps a 0 byte package, and craps out.
            if (finishedHandshake && bb.remaining() == 0) {
                mWrapping = false;
                return;
            }
            remaining = bb.remaining();
            mWriteTmp.position(0);
            mWriteTmp.limit(mWriteTmp.capacity());
            try {
                ByteBuffer[] arr = bb.getAllArray();
                res = engine.wrap(arr, mWriteTmp);
                bb.addAll(arr);
                if (!checkWrapResult(res))
                    remaining = -1;
                writeTmp();
                handleResult(res);
            }
            catch (SSLException e) {
                report(e);
            }
        }
        while ((remaining != bb.remaining() || (res != null && res.getHandshakeStatus() == HandshakeStatus.NEED_WRAP)) && mSink.remaining() == 0);
        mWrapping = false;
    }

    WritableCallback mWriteableCallback;
    @Override
    public void setWriteableCallback(WritableCallback handler) {
        mWriteableCallback = handler;
    }
    
    @Override
    public WritableCallback getWriteableCallback() {
        return mWriteableCallback;
    }

    private void report(Exception e) {
        CompletedCallback cb = getEndCallback();
        if (cb != null)
            cb.onCompleted(e);
    }

    DataCallback mDataCallback;
    @Override
    public void setDataCallback(DataCallback callback) {
        mDataCallback = callback;
    }

    @Override
    public DataCallback getDataCallback() {
        return mDataCallback;
    }

    @Override
    public boolean isChunked() {
        return mSocket.isChunked();
    }

    @Override
    public boolean isOpen() {
        return mSocket.isOpen();
    }

    @Override
    public void close() {
        mSocket.close();
    }

    @Override
    public void setClosedCallback(CompletedCallback handler) {
        mSocket.setClosedCallback(handler);
    }

    @Override
    public CompletedCallback getClosedCallback() {
        return mSocket.getClosedCallback();
    }

    @Override
    public void setEndCallback(CompletedCallback callback) {
        mSocket.setEndCallback(callback);
    }

    @Override
    public CompletedCallback getEndCallback() {
        return mSocket.getEndCallback();
    }

    @Override
    public void pause() {
        mSocket.pause();
    }

    @Override
    public void resume() {
        mSocket.resume();
    }

    @Override
    public boolean isPaused() {
        return mSocket.isPaused();
    }

    @Override
    public AsyncServer getServer() {
        return mSocket.getServer();
    }

    @Override
    public AsyncSocket getSocket() {
        return mSocket;
    }
    
    @Override
    public DataEmitter getDataEmitter() {
        return mSocket;
    }
    
    X509Certificate[] peerCertificates;
    @Override
    public X509Certificate[] getPeerCertificates() {
        return peerCertificates;
    }
}
