package io.apptik.rhub;


import org.reactivestreams.Publisher;

/**
 * Reactive Streams based Hub connecting Publishers and Subscribers so that Subscribers can receive
 * events without knowledge of which Publishers, if any, there are,
 * while maintaining clear connection between them.
 * <p>
 * The Hub is fairly simple it just accepts Publishers or single events and merges them depending
 * on the type of RSProxy then returns the resulting Publisher.
 * <p>
 * The RSProxy is a Concept responsible for multiplex/multicast the streams of events.
 * Internally they might be implemented by Processors or Subjects or simple Observables
 */
public interface RSHub extends RHub<Publisher> {

    /**
     * Type safe variant of {@link #getPub(Object)}.
     * Returns the RSProxy Publisher identified by the tag and filtered by the Class provided
     *
     * @param tag         the ID of the RSProxy
     * @param filterClass the Class to filter the Publisher by
     * @param <T>         the Type of the events the returned Publisher will emit
     * @return the Filtered RSProxy Publisher
     */
    <T> Publisher<T> getPub(Object tag, Class<T> filterClass);


    enum CoreProxyType implements ProxyType {
        PublisherRefProxy
    }
}
