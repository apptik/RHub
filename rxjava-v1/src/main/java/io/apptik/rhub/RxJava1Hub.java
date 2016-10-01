package io.apptik.rhub;


import rx.Observable;


/**
 * RxJava based Hub connecting Observables and Observers so that Observers can receive events
 * without knowledge of which Observables, if any, there are,
 * while maintaining clear connection between them.
 * <p>
 * The Hub is fairly simple it just accepts Observables or single events and merges them depending
 * on the type of Proxy then returns the resulting Observable.
 * <p>
 * The Proxy is a Concept responsible for multiplex/multicast the streams of events.
 * Internally they might be implemented by Subjects or simple Observables
 *
 * @see AbstractRxJava1Hub
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

    enum RxJava1ProxyType implements ProxyType {
        BehaviorSubjectProxy,
        PublishSubjectProxy,
        ReplaySubjectProxy,
        BehaviorRelayProxy,
        PublishRelayProxy,
        ReplayRelayProxy,
        ObservableRefProxy
    }
}
