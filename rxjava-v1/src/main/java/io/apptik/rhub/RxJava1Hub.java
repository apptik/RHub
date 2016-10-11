package io.apptik.rhub;


import rx.Observable;


/**
 * Reactive Hub compatible with RxJava 1.x {@link Observable}
 */
public interface RxJava1Hub extends RHub<Observable> {

    /**
     * Type safe variant of {@link #getPub(Object)}.
     * Returns the Proxy Observable identified by the tag and filtered by the Class provided
     *
     * @param tag         the ID of the Proxy
     * @param filterClass the Class to filter the observable by
     * @param <T>         the Type of the events the returned Observable will emit
     * @return the Filtered Proxy Observable
     */
    <T> Observable<T> getPub(Object tag, Class<T> filterClass);

}
