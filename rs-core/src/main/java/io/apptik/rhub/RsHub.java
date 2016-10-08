package io.apptik.rhub;


import org.reactivestreams.Publisher;

import io.apptik.roxy.Removable;

/**
 * Reactive Streams based Hub connecting Publishers and Subscribers so that Subscribers can receive
 * events without knowledge of which Publishers, if any, there are,
 * while maintaining clear connection between them.
 * <p>
 * The Hub is fairly simple it just accepts Publishers or single events and merges them depending
 * on the type of RSProcProxy then returns the resulting Publisher.
 * <p>
 * The RSProcProxy is a Concept responsible for multiplex/multicast the streams of events.
 * Internally they might be implemented by Processors or Subjects or simple Observables
 */
public interface RSHub<P extends Publisher> extends RHub<P> {

    @Override
    Removable addUpstream(Object tag, Publisher publisher);

    @Override
    void removeUpstream(Object tag, Publisher publisher);

}
