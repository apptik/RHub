package io.apptik.rxhub;


import io.reactivex.Observable;

/**
 * RxJava 2.x extends RxHub so that it supports Observables and Subjects as proxy implementations.
 * Working with Observables is done in a similar manner however there should be a single tag
 * shared between Publisher based proxies and Observable based proxies. Thus
 * {@link #getPub(Object)}, {@link #getObservable(Object) and other related methods} may throw an
 * exception in case {@link #getProxyType(Object)} returns incompatible proxy.
 * 
 */
public interface RxJava2Hub extends RxHub {

    /**
     * Subscribes Proxy to {@link Observable}.
     * If there is no Proxy with the specific tag a new one will be created
     * except if the Proxy is of type {@link RxJava2ObsProxyType#ObservableRefProxy}
     *
     * @param tag      the ID of the Proxy
     * @param observable the Observable to subscribe to
     */
    void addObsUpstream(Object tag, Observable observable);

    /**
     * Unsubscribe {@link Observable} from a Proxy
     *
     * @param tag      the ID of the Proxy
     * @param observable the Observable to unsubscribe from
     */
    void removeObsUpstream(Object tag, Observable observable);

    /**
     * Clears all subscriptions of all Proxies
     */
    void clearObsUpstream();

    /**
     * Returns the Proxy Observable identified by the tag
     *
     * @param tag the ID of the Proxy
     * @return the Proxy Observable
     */
    Observable getObservable(Object tag);

    /**
     * Type safe variant of {@link #getPub(Object)}.
     * Returns the Proxy Observable identified by the tag and filtered by the Class provided
     *
     * @param tag         the ID of the Proxy
     * @param filterClass the Class to filter the observable by
     * @param <T>         the Type of the events the returned Observable will emit
     * @return the Filtered Proxy Observable
     */
    <T> Observable<T> getObservable(Object tag, Class<T> filterClass);


    /**
     * removes the Proxy and frees the topic space of {@param tag} and send onComplete
     * (if the proxy allows it) to all its Subscribers
     */
    void resetObsProxy(Object tag);

    /**
     * Unsubscribe all upstream {@link Observable} from a Proxy
     * @param tag      the ID of the Proxy
     */
    void removeObsUpstream(Object tag);


    class ObservableSource {
        final Observable observable;
        final Object tag;

        ObservableSource(Observable observable, Object tag) {
            this.observable = observable;
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ObservableSource source = (ObservableSource) o;

            if (!observable.equals(source.observable)) return false;
            return tag.equals(source.tag);

        }

        @Override
        public int hashCode() {
            int result = observable.hashCode();
            result = 31 * result + tag.hashCode();
            return result;
        }
    }

    enum RxJava2PubProxyType implements ProxyType {
        BehaviorProcessorProxy,
        PublishProcessorProxy,
        ReplayProcessorProxy,
        BehaviorSafeProxy,
        PublishSafeProxy,
        ReplaySafeProxy
    }

    enum RxJava2ObsProxyType implements ProxyType {
        BehaviorSubjectProxy,
        PublishSubjectProxy,
        ReplaySubjectProxy,
        BehaviorObsSafeProxy,
        PublishObsSafeProxy,
        ReplayObsSafeProxy,
        ObservableRefProxy
    }
}
