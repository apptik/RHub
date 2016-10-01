package io.apptik.rhub;


import org.reactivestreams.Publisher;

/**
 * Reactive Streams based Hub connecting Publishers and Subscribers so that Subscribers can receive
 * events without knowledge of which Publishers, if any, there are,
 * while maintaining clear connection between them.
 * <p>
 * The Hub is fairly simple it just accepts Publishers or single events and merges them depending
 * on the type of Proxy then returns the resulting Publisher.
 * <p>
 * The Proxy is a Concept responsible for multiplex/multicast the streams of events.
 * Internally they might be implemented by Processors or Subjects or simple Observables
 */
public interface RSHub extends RHub<Publisher> {

    /**
     * Type safe variant of {@link #getPub(Object)}.
     * Returns the Proxy Publisher identified by the tag and filtered by the Class provided
     *
     * @param tag         the ID of the Proxy
     * @param filterClass the Class to filter the Publisher by
     * @param <T>         the Type of the events the returned Publisher will emit
     * @return the Filtered Proxy Publisher
     */
    <T> Publisher<T> getPub(Object tag, Class<T> filterClass);

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


    enum CoreProxyType implements ProxyType {
        PublisherRefProxy
    }
}
