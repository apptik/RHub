package io.apptik.rxhub;


import rx.Observable;


/**
 * RxJava based Hub connecting Observables and Observers so that Observers can receive events
 * without knowledge of which Observables, if any, there are,
 * while maintaining clear connection between them.
 *
 * The Hub is fairly simple it just accepts Observables or single events and merges them depending
 * on the type of Proxy then returns the resulting Observable.
 *
 * The Proxy is a Concept responsible for multiplex/multicast the streams of events.
 * Internally they might be implemented by Subjects or simple Observables
 * @see AbstractRxJava1Hub
 */
public interface RxJava1Hub {

    /**
     * Subscribes Proxy to {@link Observable}.
     * If there is no Proxy with the specific tag a new one will be created
     * except if the Proxy is of type {@link RxJava1ProxyType#ObservableRefProxy}
     *
     * @param tag      the ID of the Proxy
     * @param observable the Observable to subscribe to
     */
    void addObservable(Object tag, Observable observable);

    /**
     * Unsubscribe {@link Observable} from a Proxy
     *
     * @param tag      the ID of the Proxy
     * @param observable the Observable to unsubscribe from
     */
    void removeObservable(Object tag, Observable observable);

    /**
     * Clears all subscriptions of all Proxies
     */
    void clearObservables();

    /**
     * Returns the Proxy Observable identified by the tag
     *
     * @param tag the ID of the Proxy
     * @return the Proxy Observable
     */
    Observable getObservable(Object tag);

    /**
     * Type safe variant of {@link #getObservable(Object)}.
     * Returns the Proxy Observable identified by the tag and filtered by the Class provided
     *
     * @param tag the ID of the Proxy
     * @param filterClass the Class to filter the observable by
     * @param <T> the Type of the events the returned Observable will emit
     * @return the Filtered Proxy Observable
     */
    <T> Observable<T> getFilteredObservable(Object tag, Class<T> filterClass);

    /**
     * Manually emit event to a specific Proxy. In order to prohibit this behaviour override this
     *
     * @param tag   the ID of the Proxy
     * @param event the Event to emit
     */
    void emit(Object tag, Object event);

    /**
     * Implement this to return the type of Proxy per tag
     * @param tag the identifier of the Proxy
     * @return the Proxy Type
     */
    RxJava1ProxyType getProxyType(Object tag);

    /**
     * Implement this to return if the Proxy is threadsafe
     * @param tag the identifier of the Proxy
     * @return true if the Proxy is threadsafe, false otherwise
     */
    boolean isProxyThreadsafe(Object tag);

    /**
     * Implement this to return the ability to manually (in non-Rx fashion) emit events
     * @param tag the identifier of the Proxy
     * @return true when manual emit is possible, false otherwise
     */
    boolean canTriggerEmit(Object tag);

    class Source {
        final Observable observable;
        final Object tag;

        Source(Observable observable, Object tag) {
            this.observable = observable;
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Source source = (Source) o;

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

    enum RxJava1ProxyType {
        BehaviorSubjectProxy,
        PublishSubjectProxy,
        ReplaySubjectProxy,
        BehaviorRelayProxy,
        PublishRelayProxy,
        ReplayRelayProxy,
        ObservableRefProxy
    }
}
