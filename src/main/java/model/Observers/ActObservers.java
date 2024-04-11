package model.Observers;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;

public class ActObservers<T> implements ActObserversAccess<T> {
    @FunctionalInterface
    public static interface MethodToCall<T> {
        void execute(T t);
    }

    private ArrayList<AbstractMap.SimpleEntry<MethodToCall<T>, Integer>> followers;

    public ActObservers() {
        this.followers = new ArrayList<>();
    }

    public void subscribe(MethodToCall<T> follower) {
        this.followers.add(new AbstractMap.SimpleEntry<MethodToCall<T>,Integer>(follower, 0));
    }

    public void subscribe(MethodToCall<T> follower, Integer useCount) {
        this.followers.add(new AbstractMap.SimpleEntry<MethodToCall<T>, Integer>(follower, useCount));
    }

    public void unsubscribe(MethodToCall<T> follower) {
        for (int i=0; i<followers.size(); i++) {
            if (followers.get(i).getKey() == follower) {
                followers.remove(i);
                break;
            }
        }
    }
    
    public void informAll(T data) {
        for (int i=0; i<followers.size(); i++) {
            followers.get(i).getKey().execute(data);
            if (followers.get(i).getValue() != 0) {
                if (followers.get(i).getValue() == 1)
                    followers.remove(i--);
                else 
                    followers.get(i).setValue(followers.get(i).getValue()-1);
            }
        }
    }
}
