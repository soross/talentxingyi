package com.koushikdutta.async;

import java.nio.ByteBuffer;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.WritableCallback;
import com.koushikdutta.async.wrapper.AsyncSocketWrapper;

public class DataWrapperSocket extends FilteredDataEmitter implements AsyncSocketWrapper {
    private AsyncSocket mSocket;
    public void setSocket(AsyncSocket socket) {
        mSocket = socket;
        setDataEmitter(mSocket);
    }
    
    @Override
    public AsyncSocket getSocket() {
        return mSocket;
    }

    @Override
    public void write(ByteBuffer bb) {
        mSocket.write(bb);
    }

    @Override
    public void write(ByteBufferList bb) {
        mSocket.write(bb);
    }

    @Override
    public void setWriteableCallback(WritableCallback handler) {
        mSocket.setWriteableCallback(handler);
    }

    @Override
    public WritableCallback getWriteableCallback() {
        return getWriteableCallback();
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
}
