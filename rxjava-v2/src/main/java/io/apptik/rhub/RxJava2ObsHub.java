package io.apptik.rhub;


import io.reactivex.Observable;

/**
 * RxJava 2.x extends RSHub so that it supports Observables and Subjects as proxy implementations.
 * Working with Observables is done in a similar manner however there should be a single tag
 * shared between Publisher based proxies and Observable based proxies. Thus
 * {@link #getPub(Object)}, {@link #getPub(Object) and other related methods} may throw an
 * exception in case {@link #getProxyType(Object)} returns incompatible proxy.
 * 
 */
public interface RxJava2ObsHub extends RHub<Observable> {

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
