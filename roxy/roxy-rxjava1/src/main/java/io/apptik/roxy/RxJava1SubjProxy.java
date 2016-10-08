package io.apptik.roxy;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.Subject;


public class RxJava1SubjProxy implements Roxy<Observable> {

    private final Subject subj;

    private final Map<Observable, Subscription> subscriptions = new ConcurrentHashMap<>();

    public RxJava1SubjProxy(Subject subj) {
        this.subj = subj;
    }

    /**
     * Subscribes Proxy to {@link rx.Observable}.
     * If there is no Proxy with the specific tag a new one will be created
     *
     * @param observable the Observable to subscribe to
     */
    @Override
    public Removable addUpstream(final Observable observable) {
        Subscription subscription = observable.subscribe(new Subscriber() {
            @Override
            public void onNext(Object value) {
                subj.onNext(value);
            }

            @Override
            public void onCompleted() {
                subj.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subj.onError(e);
            }

        });

        subscriptions.put(observable, subscription);

        return new Removable() {
            @Override
            public void remove() {
                removeUpstream(observable);
            }
        };
    }

    /**
     * Unsubscribe {@link Observable} from a Proxy
     *
     * @param observable the Observable to unsubscribe from
     */
    @Override
    public void removeUpstream(Observable observable) {
        synchronized (subscriptions) {
            Subscription s = subscriptions.get(observable);
            if (s != null) {
                s.unsubscribe();
                subscriptions.remove(observable);
            }
        }
    }

    @Override
    public Observable pub() {
        return subj;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Observable<T> pub(final Class<T> filterClass) {
        return subj.filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object obj) {
                return filterClass.isAssignableFrom(obj.getClass());
            }
        });
    }

    @Override
    public void emit(Object event) {
        subj.onNext(event);
    }

    @Override
    public void complete() {
        subj.onCompleted();
    }

    @Override
    public TePolicy tePolicy() {
        return TePolicy.PASS;
    }

    @Override
    public void clear() {
        synchronized (subscriptions) {
            for (Subscription s : subscriptions.values()) {
                s.unsubscribe();
            }
            subscriptions.clear();
        }
    }
}
