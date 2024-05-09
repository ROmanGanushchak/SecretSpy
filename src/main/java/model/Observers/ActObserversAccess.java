package model.Observers;

import model.Observers.ActObservers.MethodToCall;

/** class to provide partial access towords the ActObservers obj */
public interface ActObserversAccess<T> {
    public void subscribe(MethodToCall<T> follower);

    public void subscribe(MethodToCall<T> follower, Integer useCount);

    public void unsubscribe(MethodToCall<T> follower);
}
