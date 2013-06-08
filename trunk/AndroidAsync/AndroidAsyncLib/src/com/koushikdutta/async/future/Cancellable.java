package com.koushikdutta.async.future;

public interface Cancellable {

    /**
     * 任务是否已经完成
     *
     * @return true:任务已经完成,false:任务未完成
     */
    boolean isDone();

    /**
     * 任务是否已经被取消
     *
     * @return true:任务已经被取消,false:任务未被取消
     */
    boolean isCancelled();

    /**
     * 取消任务
     * @return true:任务取消成功,false:任务取消失败
     */
    boolean cancel();
}