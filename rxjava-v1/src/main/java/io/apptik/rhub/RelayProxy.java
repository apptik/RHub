package io.apptik.rhub;


import com.jakewharton.rxrelay.Relay;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.apptik.rhub.RxJava1Hub.RxJava1ProxyType;
import io.apptik.rhub.RxJava1Hub.Source;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;


class RelayProxy {

    final Relay relay;

    private final Map<Source, Subscription> subscriptions = new ConcurrentHashMap<>();

    RelayProxy(Relay relay) {
        this.relay = relay;
    }

    /**
     * Subscribes Proxy to {@link Observable}.
     * If there is no Proxy with the specific tag a new one will be created
     * except if the Proxy is of type {@link RxJava1ProxyType#ObservableRefProxy}
     *
     * @param tag        the ID of the Proxy
     * @param observable the Observable to subscribe to
     */
    void addObs(final Object tag, final Observable observable) {
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

        subscriptions.put(new Source(observable, tag), subscription);
    }

    /**
     * Unsubscribe {@link Observable} from a Proxy
     *
     * @param tag        the ID of the Proxy
     * @param observable the Observable to unsubscribe from
     */
    void removeObs(Object tag, Observable observable) {
        synchronized (subscriptions) {
            Source src = new Source(observable, tag);
            Subscription s = subscriptions.get(src);
            if (s != null) {
                s.unsubscribe();
                subscriptions.remove(src);
            }
        }
    }

    void clear() {
        synchronized (subscriptions) {
            for (Subscription s : subscriptions.values()) {
                s.unsubscribe();
            }
            subscriptions.clear();
        }
    }
}
