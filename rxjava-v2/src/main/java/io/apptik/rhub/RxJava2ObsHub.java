package io.apptik.rhub;


import io.apptik.roxy.RxJava2SubjProxy;
import io.reactivex.Observable;

/**
 * Reactive Hub compatible with RxJava 2.x {@link Observable}
 */
public interface RxJava2ObsHub extends RHub<Observable> {

    /**
     * Type safe variant of {@link #getPub(Object)}.
     * Returns the Proxy Observable identified by the tag and filtered by the Class provided
     *
     * @param tag         the ID of the Proxy
     * @param filterClass the Class to filter the observable by
     * @param <T>         the Type of the events the returned Observable will emit
     * @return the Filtered RSProcProxy Observable
     */
    <T> Observable<T> getPub(Object tag, Class<T> filterClass);

    @Override
    ProxyType<RxJava2SubjProxy> getProxyType(Object tag);

}
