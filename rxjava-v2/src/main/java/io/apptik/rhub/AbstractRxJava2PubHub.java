package io.apptik.rhub;


import io.reactivex.Flowable;


/**
 * Base RxJava 2.x implementation of {@link RSHub}
 */
public abstract class AbstractRxJava2PubHub extends AbstractRSHub<Flowable> {

    @Override
    @SuppressWarnings("unchecked")
    public <T> Flowable<T> getPub(Object tag, Class<T> filterClass) {
        return super.getPub(tag, filterClass);
    }

}
