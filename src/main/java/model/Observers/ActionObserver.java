package model.Observers;

public class ActionObserver<T> {
    @FunctionalInterface
    public static interface MethodToCall<T> {
        void execute(T t);
    }

    private MethodToCall<T> method;
    private int useCount;

    public ActionObserver(MethodToCall<T> method) {
        this.method = method;
        this.useCount = -1;
    }

    public ActionObserver() {
        this.useCount = -1;
    }

    public void setMethodToCall(MethodToCall<T> method) {
        this.method = method;
    }

    public void setUseCount(int count) {
        this.useCount = count;
    }

    public void inform(T parametr) {
        method.execute(parametr);
        if (useCount != -1)
            useCount--;
    }

    public int getUseCount() {
        return this.useCount;
    }
}
