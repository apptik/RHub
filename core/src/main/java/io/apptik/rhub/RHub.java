package io.apptik.rhub;

import io.apptik.roxy.Removable;
import io.apptik.roxy.Roxy;

/**
 * Reactive Hub is a collection of multi-receiver and multi-producer Proxies connecting Publishers
 * and Subscribers so that Subscribers can receive events without knowledge of which Publishers, if
 * any, there are, while maintaining easily identifiable connection between them.
 * <p>
 * The Hub is fairly simple, it just accepts Publishers or single events and merges them depending
 * on the type of Proxy then returns the resulting Publisher identified by a Tag(topic).
 * <p>
 * The Proxy is a single unit, implementation of {@link Roxy} (Reactive Proxy) responsible for
 * merge and multicast and possibly buffer the streams of events. The Proxy in a Hub should be kept
 * as simple as possible and free from other processing logic.
 * <p>
 * A Hub can have many different proxies identified by a Tag (topic). Every proxy can work
 * independently and merge, buffer and emit events in a different manner.
 * @param <P> the type of Upstream source that the Hub accepts and merged output the Hub returns
 */
public interface RHub<P> {

    /**
     * Subscribes Proxy to publisher.
     * If there is no Proxy with the specific tag a new one will be created
     *
     * @param tag       the ID of the Proxy
     * @param publisher the Publisher to subscribe to
     */
    Removable addUpstream(Object tag, P publisher);

    /**
     * Unsubscribe upstream publisher from a Proxy
     *
     * @param tag       the ID of the Proxy
     * @param publisher the Publisher to unsubscribe from
     */
    void removeUpstream(Object tag, P publisher);

    /**
     * Unsubscribe all upstream subscriptions from a Proxy
     *
     * @param tag the ID of the Proxy
     */
    void removeUpstream(Object tag);

    /**
     * Unsubscribe all upstream subscriptions from all Proxies
     */
    void clearUpstream();

    /**
     * Returns the Proxy Publisher identified by the tag
     *
     * @param tag the ID of the Proxy
     * @return the Proxy Publisher
     */
    P getPub(Object tag);

    /**
     * Type safe variant of {@link #getPub(Object)}.
     * Returns the Proxy Publisher identified by the tag and filtered by the Class provided
     *
     * @param tag         the ID of the Proxy
     * @param filterClass the Class to filter the Publisher by
     * @param <T>         the Type of the events the returned Publisher will emit
     * @return the Filtered Proxy Publisher
     */
    <T> P getPub(Object tag, Class<T> filterClass);

    /**
     * Manually emit an event to a specific Proxy. In order to prohibit this behaviour override
     * this to ignore or throw an exception or {@link RHub#canTriggerEmit(Object)} to return false.
     *
     * @param tag   the ID of the Proxy
     * @param event the Event to emit
     */
    void emit(Object tag, Object event);

    /**
     * Implement this to return the type of Proxy per tag
     *
     * @param tag the identifier of the Proxy
     * @return the Proxy Type
     */
    <R extends Roxy<P>> ProxyType<R> getProxyType(Object tag);

    /**
     * Implement this to return the if ability to manually (in non-Rx fashion) emit events is
     * enabled
     *
     * @param tag the identifier of the Proxy
     * @return true when manual emit is possible, false otherwise
     */
    boolean canTriggerEmit(Object tag);

    /**
     * removes the Proxy and frees the topic space and send onComplete
     * (if the proxy allows it) to all its Subscribers
     * @param tag the ID of the Proxy
     */
    void resetProxy(Object tag);

    /**
     * Proxy Type identifies the proxy behaviour.
     * @param <R> Implementation of {@link Roxy}
     */
    interface ProxyType<R extends Roxy> {
        /**
         * Returns the Proxy type. This will be called when a new Proxy needs to be created.
         * Proxies returned must be different.
         * @return the Proxy Instance
         */
        R getRoxy();
    }
}
