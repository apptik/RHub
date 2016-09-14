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
     * except if the Proxy is of type {@link RxJava2ProxyType#ObservableRef}
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
     * Clears all subscriptions of all Proxys
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
     * Type safe variant of {@link #getPub(Object)}.
     * Returns the Proxy Observable identified by the tag and filtered by the Class provided
     *
     * @param tag         the ID of the Proxy
     * @param filterClass the Class to filter the observable by
     * @param <T>         the Type of the events the returned Observable will emit
     * @return the Filtered Proxy Observable
     */
    <T> Observable<T> getFilteredObservable(Object tag, Class<T> filterClass);


    enum RxJava2ProxyType implements ProxyType {
        BehaviorProcessor,
        PublishProcessor,
        ReplayProcessor,

        BehaviorSubject,
        PublishSubject,
        ReplaySubject,
        //TODO possibly coming up later
//        BehaviorRelay,
//        PublishRelay,
//        ReplayRelay,
        ObservableRef
    }
}
