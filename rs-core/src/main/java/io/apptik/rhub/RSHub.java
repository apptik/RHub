package io.apptik.rhub;


import org.reactivestreams.Publisher;

import io.apptik.roxy.Removable;

/**
 * Reactive Hub compatible with Reactive Streams {@link Publisher} Interface.
 * This Hub accepts and returns {@link Publisher} as upstream sources and merged outputs.
 * <p>
 * This interface overrides {@link #addUpstream(Object, Publisher)} and
 * {@link #removeUpstream(Object, Publisher)} in order to remove type parameters as the Hub does
 * not care
 * of the input Sources as lon as they are compatible with the Reactive Streams {@link Publisher}
 * interface.
 * <p>
 * Implementations however can override {@link #getPub(Object)} and
 * {@link #getPub(Object, Class)} to return specific implementations of the {@link Publisher}
 * interface depending on the Reactive Framework used.
 */
public interface RSHub<P extends Publisher> extends RHub<P> {

    @Override
    Removable addUpstream(Object tag, Publisher publisher);

    @Override
    void removeUpstream(Object tag, Publisher publisher);

}
