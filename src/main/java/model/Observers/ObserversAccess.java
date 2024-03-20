package model.Observers;

public interface ObserversAccess<T> {
    public void subscribe(T follower);
    public void unsubscribe(T follower);
}
