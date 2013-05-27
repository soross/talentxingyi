package com.koushikdutta.async.future;

public interface Future<T> extends Cancellable, java.util.concurrent.Future<T> {

    /**
     * 获取返回结果的回调实例
     *
     * @return 返回结果的回调实例
     */
    public FutureCallback<T> getResultCallback();

    /**
     * 设置返回结果的回调实例
     *
     * @param callback 返回结果的回调实例
     * @return Future实例
     */
    public Future<T> setResultCallback(FutureCallback<T> callback);
}
