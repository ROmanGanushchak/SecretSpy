package model.Observers;

public class ActionObserver<T> {
    @FunctionalInterface
    public static interface MethodToCall<T> {
        void execute(T t);
    }

    private MethodToCall<T> method;

    public ActionObserver(MethodToCall<T> method) {
        this.method = method;
    }

    public void inform(T parametr) {
        method.execute(parametr);
    }
}
