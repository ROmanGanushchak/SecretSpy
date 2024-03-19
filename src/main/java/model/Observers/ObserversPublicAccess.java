package model.Observers;

public interface ObserversPublicAccess<T> {
    public void subscribe(T follower);
    public void unsubscribe(T follower);
}
