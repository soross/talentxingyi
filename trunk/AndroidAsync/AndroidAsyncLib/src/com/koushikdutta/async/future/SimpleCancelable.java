package com.koushikdutta.async.future;

public class SimpleCancelable implements DependentCancellable {

    boolean complete;

    boolean canceled;

    Cancellable parent;

    @Override
    public Cancellable getParent() {
        return parent;
    }

    @Override
    public void setParent(Cancellable parent) {
        this.parent = parent;
    }

    @Override
    public boolean isDone() {
        return complete;
    }

    @Override
    public boolean isCancelled() {
        // 逻辑：当前是否已经取消掉或者父类是否已经取消掉
        return canceled || (parent != null && parent.isCancelled());
    }

    @Override
    public boolean cancel() {
        // 当已经完成的时候，为false；已经cancel时候，为true。然后把父类的也cancel掉
        synchronized (this) {
            if (complete)
                return false;
            if (canceled)
                return true;
            canceled = true;
        }
        if (parent != null)
            parent.cancel();
        return true;
    }

    public boolean setComplete() {
        synchronized (this) {
            if (canceled)
                return false;
            complete = true;
        }
        return true;
    }

    public static final Cancellable COMPLETED = new SimpleCancelable() {
        {
            setComplete();
        }
    };
}
