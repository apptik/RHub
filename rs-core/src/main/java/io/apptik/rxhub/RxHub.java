package io.apptik.rxhub;


import org.reactivestreams.Publisher;

/**
 * Reactive Streams based Hub connecting Publishers and Subscribers so that Subscribers can receive
 * events without knowledge of which Publishers, if any, there are,
 * while maintaining clear connection between them.
 *
 * The Hub is fairly simple it just accepts Publishers or single events and merges them depending
 * on the type of Proxy then returns the resulting Publisher.
 *
 * The Proxy is a Concept responsible for multiplex/multicast the streams of events.
 * Internally they might be implemented by Processors or Subjects or simple Observables
 */
public interface RxHub {

    /**
     * Subscribes Proxy to {@link Publisher}.
     * If there is no Proxy with the specific tag a new one will be created
     * except if the Proxy is of type {@link CoreProxyType#PublisherRefProxy}
     *
     * @param tag      the ID of the Proxy
     * @param publisher the Publisher to subscribe to
     */
    Removable addUpstream(Object tag, Publisher publisher);

    /**
     * Unsubscribe upstream {@link Publisher} from a Proxy
     *
     * @param tag      the ID of the Proxy
     * @param publisher the Publisher to unsubscribe from
     */
    void removeUpstream(Object tag, Publisher publisher);

    /**
     * Unsubscribe all upstream {@link Publisher} from a Proxy
     * @param tag      the ID of the Proxy
     */
    void removeUpstream(Object tag);

    /**
     * Clears all upstream subscriptions to all Publishers
     */
    void clearUpstream();

    /**
     * Returns the Proxy Publisher identified by the tag
     *
     * @param tag the ID of the Proxy
     * @return the Proxy Publisher
     */
    Publisher getPub(Object tag);

    /**
     * Type safe variant of {@link #getPub(Object)}.
     * Returns the Proxy Publisher identified by the tag and filtered by the Class provided
     *
     * @param tag the ID of the Proxy
     * @param filterClass the Class to filter the Publisher by
     * @param <T> the Type of the events the returned Publisher will emit
     * @return the Filtered Proxy Publisher
     */
    <T> Publisher<T> getPub(Object tag, Class<T> filterClass);

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
    ProxyType getProxyType(Object tag);

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

    /**
     * removes the Proxy and frees the topic space of {@param tag} and send onComplete
     * (if the proxy allows it) to all its Subscribers
     */
    void resetProxy(Object tag);

    class Source {
        final Publisher publisher;
        final Object tag;

        Source(Publisher publisher, Object tag) {
            this.publisher = publisher;
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Source source = (Source) o;

            if (!publisher.equals(source.publisher)) return false;
            return tag.equals(source.tag);

        }

        @Override
        public int hashCode() {
            int result = publisher.hashCode();
            result = 31 * result + tag.hashCode();
            return result;
        }
    }

    /**
     * In general this can be kind of Processor/Subject or simple Publisher/Observable
     */
    interface ProxyType {
    }

    enum CoreProxyType implements ProxyType {
        PublisherRefProxy
    }
}
