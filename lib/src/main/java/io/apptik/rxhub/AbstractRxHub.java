package io.apptik.rxhub;


import com.jakewharton.rxrelay.*;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.*;
import rx.subscriptions.CompositeSubscription;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Base implementation of {@link RxHub}
 * Essentially this is a collection of {@link Observable} nodes which can also subscribe to other
 * Observables and pass events to their Subscribers
 * <p>
 * Nodes can be either {@link Subject} or {@link Relay}. Nodes are identified by their Tags.
 * Nodes subscribes to Observables however each subscription created is
 * per {@link io.apptik.rxhub.RxHub.Source}. A Source is identified by Observable and a Tag.
 * For example when Observable A is added with Tag T1 and Tag T2. Two nodes are created receiving
 * the same events. Each of those nodes can be used and unsubscribed from Observable A
 * independently.
 * <p>
 * Observers subscribe to a Node. Observers does not need to know about the source of the Events
 * i.e the Observers that the Nodes is subscribed to.
 * <p>
 * To fetch the Node to subscribe to {@link AbstractRxHub#getNode(Object)} must be called.
 * <p>
 * Non-Rx code can also call {@link AbstractRxHub#emit(Object, Object)} to manually emit Events
 * through specific Node.
 */
public abstract class AbstractRxHub implements RxHub {

    private Map<Object, Observable> nodeMap = new ConcurrentHashMap<>();
    private Map<Source, Subscription> subscriptionMap = new ConcurrentHashMap<>();

    private CompositeSubscription subscriptions = new CompositeSubscription();

    /**
     * Subscribes Node to {@link Observable}.
     * If there is no Node with the specific tag a new one will be created
     *
     * @param tag the ID of the Node
     * @param provider the Observable to subscribe to
     */
    @Override
    public void addProvider(Object tag, Observable provider) {
        Subscription res;
        Observable node = nodeMap.get(tag);
        if (node == null) {
            node = addNode(tag);
        }
        if (Action1.class.isAssignableFrom(node.getClass())) {
            res = provider.subscribe((Action1) node);
        } else if (Observer.class.isAssignableFrom(node.getClass())) {
            res = provider.subscribe((Observer) node);
        } else {
            //should not happen
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Node(%s) neither " + "Action1 nor " + "Observer", tag));
        }
        subscriptions.add(res);
        subscriptionMap.put(new Source(provider, tag), res);
    }

    /**
     * Unsubscribe {@link Observable} from a Node
     * @param tag the ID of the Node
     * @param provider the Observable to unsubscribe from
     */
    @Override
    public void removeProvider(Object tag, Observable provider) {
        Source s = new Source(provider, tag);
        subscriptions.remove(subscriptionMap.get(s));
        subscriptionMap.remove(s);
    }

    /**
     * Returns the Node Observable identified by the tag
     * @param tag the ID of the Node
     * @return the Node Observable
     */
    @Override
    public Observable getNode(Object tag) {
        Observable res = nodeMap.get(tag);
        if (res == null) {
            res = addNode(tag);
        }
        //make sure we expose it asObservable hide node's identity
        return res.asObservable();
    }

    private Observable addNode(Object tag) {
        Observable res;
        NodeType nt = getNodeType(tag);
        switch (nt) {
            case BehaviorSubject:
                res = BehaviorSubject.create();
                break;
            case PublishSubject:
                res = PublishSubject.create();
                break;
            case ReplaySubject:
                res = ReplaySubject.create();
                break;
            case BehaviorRelay:
                res = BehaviorRelay.create();
                break;
            case PublishRelay:
                res = PublishRelay.create();
                break;
            case ReplayRelay:
                res = ReplayRelay.create();
                break;
            //should not happen;
            default:
                throw new IllegalStateException("Unknown NodeType");
        }

        if (isNodeThreadsafe(tag)) {
            switch (nt) {
                case BehaviorSubject:
                case PublishSubject:
                case ReplaySubject:
                    res = new SerializedSubject((Subject) res);
                    break;
                case BehaviorRelay:
                case PublishRelay:
                case ReplayRelay:
                    res = new SerializedRelay((Relay) res);
                    break;
            }
        }
        nodeMap.put(tag, res);
        return res;
    }

    /**
     * Manually emit event to a specific Node. In order to prohibit this behaviour override this
     * @param tag the ID of the Node
     * @param event the Event to emit
     */
    @Override
    public void emit(Object tag, Object event) {
        Observable node = getNode(tag);
        if (Action1.class.isAssignableFrom(node.getClass())) {
            ((Action1) node).call(event);
        } else if (Observer.class.isAssignableFrom(node.getClass())) {
            ((Observer) node).onNext(event);
        } else {
            //should not happen
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Node(%s) neither " + "Action1 nor " + "Observer", tag));
        }
    }

    /**
     * Clears all subscriptions of all Nodes
     */
    @Override
    public void clearProviders() {
        subscriptions.clear();
    }

}
