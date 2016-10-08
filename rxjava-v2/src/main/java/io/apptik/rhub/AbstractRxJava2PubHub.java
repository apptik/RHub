package io.apptik.rhub;


import io.reactivex.Flowable;


/**
 * Base implementation of {@link RxJava2ObsHub}
 * Essentially this is a collection of proxies which can
 * also
 * subscribe to other Observables and pass events to their Subscribers
 * <p/>
 * Proxies are identified by their Tags.
 * Proxies subscribes to Observables however each subscription created is
 * per Source. A Source is identified by Observable and a Tag.
 * For example when Observable A is added with Tag T1 and Tag T2. Two proxies are created receiving
 * the same events. Each of those proxies can be used and unsubscribed from Observable A
 * independently.
 * <p/>
 * Observers subscribe to a RSProcProxy. Observers does not need to know about the source of the
 * Events
 * i.e the Observers that the Proxies is subscribed to.
 * <p/>
 * To fetch the RSProcProxy to subscribe to {@link AbstractRxJava2PubHub#getPub(Object)} must be
 * called.
 * <p/>
 * Non-Rx code can also call {@link AbstractRxJava2PubHub#emit(Object, Object)} to manually emit
 * Events
 * through specific RSProcProxy.
 */
public abstract class AbstractRxJava2PubHub extends AbstractRSHub<Flowable> {

    @Override
    @SuppressWarnings("unchecked")
    public <T> Flowable<T> getPub(Object tag, Class<T> filterClass) {
        return super.getPub(tag, filterClass);
    }

}
