package model.Observers;

import java.util.ArrayList;

public class Observers<T> implements ObserversPublicAccess<T> {
    private ArrayList<T> followers;

    public Observers() {
        this.followers = new ArrayList<>();
    }

    public void subscribe(T follower) {
        this.followers.add(follower);
    }

    public void unsubscribe(T follower) {
        this.followers.remove(follower);
    }
    
    public ArrayList<T> getFollowers() {
        return this.followers;
    }
}
