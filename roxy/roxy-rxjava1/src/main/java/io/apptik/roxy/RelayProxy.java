package io.apptik.roxy;


import com.jakewharton.rxrelay.Relay;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;


public class RelayProxy implements Roxy<Observable> {

    private final Relay relay;

    private final Map<Observable, Subscription> subscriptions = new ConcurrentHashMap<>();

    public RelayProxy(Relay relay) {
        this.relay = relay;
    }


    /**
     * Subscribes Proxy to {@link Observable}.
     * If there is no Proxy with the specific tag a new one will be created
     *
     * @param observable the Observable to subscribe to
     */
    @Override
    public Removable addUpstream(final Observable observable) {
        Subscription subscription = observable.subscribe(new Subscriber() {
            @Override
            public void onNext(Object value) {
                relay.call(value);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
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
        return relay;
    }

    @Override
    public void emit(Object event) {
        relay.call(event);
    }

    @Override
    public void complete() {
        //na
    }

    @Override
    public TePolicy tePolicy() {
        //no terminal events for Relays
        return TePolicy.SKIP;
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
