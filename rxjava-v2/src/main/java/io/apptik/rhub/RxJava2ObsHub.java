package io.apptik.rhub;


import io.apptik.roxy.RxJava2SubjProxy;
import io.reactivex.Observable;

/**
 * RxJava 2.x extends RSHub so that it supports Observables and Subjects as proxy implementations.
 * Working with Observables is done in a similar manner however there should be a single tag
 * shared between Publisher based proxies and Observable based proxies. Thus
 * {@link #getPub(Object)}, {@link #getPub(Object) and other related methods} may throw an
 * exception in case {@link #getProxyType(Object)} returns incompatible proxy.
 */
public interface RxJava2ObsHub extends RHub<Observable> {

    /**
     * Type safe variant of {@link #getPub(Object)}.
     * Returns the RSProcProxy Observable identified by the tag and filtered by the Class provided
     *
     * @param tag         the ID of the RSProcProxy
     * @param filterClass the Class to filter the observable by
     * @param <T>         the Type of the events the returned Observable will emit
     * @return the Filtered RSProcProxy Observable
     */
    <T> Observable<T> getPub(Object tag, Class<T> filterClass);

    @Override
    ProxyType<RxJava2SubjProxy> getProxyType(Object tag);

}
