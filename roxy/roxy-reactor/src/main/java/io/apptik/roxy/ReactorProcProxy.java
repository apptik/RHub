package io.apptik.roxy;


import org.reactivestreams.Processor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;

/**
 * Roxy implementation using Reactor-Core
 */
public class ReactorProcProxy extends RSProcProxy<Flux> {

    public ReactorProcProxy(FluxProcessor proc, TePolicy tePolicy) {
        super(proc, tePolicy);
    }

    @Override
    protected Flux hide(Processor processor) {
        return ((Flux) processor).hide();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> Flux<T> filter(Processor processor, final Class<T> filterClass) {
        return ((Flux) processor).filter(o -> filterClass.isAssignableFrom(o.getClass()));
    }

}
