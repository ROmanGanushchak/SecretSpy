package model.Observers;

import model.Observers.ActObservers.MethodToCall;

public interface ActObserversAccess<T> {
    public void subscribe(MethodToCall<T> follower);

    public void subscribe(MethodToCall<T> follower, Integer useCount);

    public void unsubscribe(MethodToCall<T> follower);
}
