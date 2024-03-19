package model.Observers;

public class ActObservers<T> extends Observers<ActionObserver<T>> {
    public void informAll(T data) {
        for (ActionObserver<T> follower : super.getFollowers()) {
            follower.inform(data);
        }
    }
}
