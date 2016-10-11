package io.apptik.roxy;


import java.io.Serializable;

/**
 * Reactive Proxy - a dynamic multi-receiver and multi-producer.
 *
 * @param <P> the type of Upstream source that the Proxy accepts and merged output the Proxy returns
 */
public interface Roxy<P> {

    /**
     * Subscribes Proxy to event provider.
     * @param publisher the Publisher to subscribe to
     */
    Removable addUpstream(P publisher);

    /**
     * Unsubscribe upstream publisher from a Proxy
     * @param publisher the Publisher to unsubscribe from
     */
    void removeUpstream(P publisher);

    /**
     * Returns the resulting merged Publisher
     * @return resulting Publisher
     */
    P pub();

    /**
     * Returns the resulting merged Publisher filtered by the filterClass provided
     * @param filterClass the Class to filter the Publisher by
     * @param <T> the Type of the events the returned Publisher will emit
     * @return resulting Publisher
     */
    <T> P pub(Class<T> filterClass);

    /**
     * Manually emit an event.
     * @param event the Event to emit
     */
    void emit(Object event);

    /**
     * Trigger onComplete event on the Proxy and thus to all its subscribers
     */
    void complete();

    /**
     * Clears the proxy from upstreams
     */
    void clear();

    /**
     * Returns the Terminal Event Policy
     * @return the Terminal Event Policy
     */
    TePolicy tePolicy();

    /**
     *  Defines the Terminal Event Policy, that is how to handle onError and onCompplete events
     *  form the Upstream
     */
    enum TePolicy {
        /**
         * Pass terminal events
         */
        PASS,
        /**
         * Wrap terminal events in {@link Event}
         */
        WRAP,
        /**
         * Ignore/Skip terminal events sent by the upstream
         */
        SKIP
    }

    enum Event {
        /**
         * Wraps onComplete
         */
        COMPLETE;

        /**
         * Wraps a Throwable form onError.
         */
        public static final class ErrorEvent implements Serializable {

            private static final long serialVersionUID = -2443670933703181488L;

            final Throwable e;

            public ErrorEvent(Throwable e) {
                this.e = e;
            }

            @Override
            public boolean equals(Object o) {

                if (this == o) return true;
                if (!(o instanceof ErrorEvent)) return false;

                ErrorEvent that = (ErrorEvent) o;

                return e != null ? e.equals(that.e) : that.e == null;

            }

            @Override
            public int hashCode() {
                return e != null ? e.hashCode() : 0;
            }

            @Override

            public String toString() {
                return "onError[" + e + "]";
            }
        }
    }
}
