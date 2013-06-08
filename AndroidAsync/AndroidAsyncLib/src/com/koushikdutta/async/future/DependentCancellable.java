package com.koushikdutta.async.future;

/**
 * 依赖的Cancellable
 */
public interface DependentCancellable extends Cancellable {

    /**
     * 获取父Cancellable实例
     *
     * @return Cancellable实例
     */
    public Cancellable getParent();

    /**
     * 设置父Cancellable实例
     */
    public void setParent(Cancellable parent);
}
