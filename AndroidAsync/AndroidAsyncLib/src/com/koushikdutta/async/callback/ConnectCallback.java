package com.koushikdutta.async.callback;

import com.koushikdutta.async.AsyncSocket;

public interface ConnectCallback {

    /**
     * 建立连接完毕
     *
     * @param ex     异常信息
     * @param socket Socket实例
     */
    public void onConnectCompleted(Exception ex, AsyncSocket socket);
}