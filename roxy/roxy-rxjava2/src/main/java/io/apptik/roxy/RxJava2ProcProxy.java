package io.apptik.roxy;


import org.reactivestreams.Processor;

import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.FlowableProcessor;

/**
 * Roxy implementation using RxJava 2.x {@link Processor}
 */
public class RxJava2ProcProxy extends RSProcProxy<Flowable> {

    public RxJava2ProcProxy(FlowableProcessor proc, TePolicy tePolicy) {
        super(proc, tePolicy);
    }

    @Override
    protected Flowable hide(Processor processor) {
        return ((Flowable) processor).hide();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> Flowable<T> filter(Processor processor, final Class<T> filterClass) {
        return ((Flowable) processor).filter(new Predicate() {
            @Override
            public boolean test(Object o) {
                return filterClass.isAssignableFrom(o.getClass());
            }
        });
    }

}
