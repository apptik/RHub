package io.apptik.rhub;


import reactor.core.publisher.Flux;

public abstract class AbstractReactorHub extends AbstractRSHub<Flux> {

    @Override
    @SuppressWarnings("unchecked")
    public <T> Flux<T> getPub(Object tag, Class<T> filterClass) {
        return super.getPub(tag, filterClass);
    }
}
