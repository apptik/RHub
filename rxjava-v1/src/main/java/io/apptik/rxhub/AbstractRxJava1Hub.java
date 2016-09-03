package io.apptik.rxhub;


import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;
import com.jakewharton.rxrelay.Relay;
import com.jakewharton.rxrelay.ReplayRelay;
import com.jakewharton.rxrelay.SerializedRelay;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;


/**
 * Base implementation of {@link RxJava1Hub}
 * Essentially this is a collection of {@link Observable} nodes which can also subscribe to other
 * Observables and pass events to their Subscribers
 * <p/>
 * Nodes can be either {@link Subject} or {@link Relay}. Nodes are identified by their Tags.
 * Nodes subscribes to Observables however each subscription created is
 * per {@link Source}. A Source is identified by Observable and a Tag.
 * For example when Observable A is added with Tag T1 and Tag T2. Two nodes are created receiving
 * the same events. Each of those nodes can be used and unsubscribed from Observable A
 * independently.
 * <p/>
 * Observers subscribe to a Node. Observers does not need to know about the source of the Events
 * i.e the Observers that the Nodes is subscribed to.
 * <p/>
 * To fetch the Node to subscribe to {@link AbstractRxJava1Hub#getNode(Object)} must be called.
 * <p/>
 * Non-Rx code can also call {@link AbstractRxJava1Hub#emit(Object, Object)} to manually emit Events
 * through specific Node.
 */
public abstract class AbstractRxJava1Hub implements RxJava1Hub {

    private final Map<Object, Observable> nodeMap = new ConcurrentHashMap<>();
    private final Map<Source, Subscription> subscriptionMap = new ConcurrentHashMap<>();

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    public final void addProvider(Object tag, Observable provider) {
        if (getNodeType(tag) == NodeType.ObservableRef) {
            nodeMap.put(tag, provider);
        } else {
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
                        "Node(%s) type(%s) is not supported! Do we have an alien injection?",
                        tag, provider.getClass()));
            }
            subscriptions.add(res);
            subscriptionMap.put(new Source(provider, tag), res);
        }
    }

    @Override
    public final void removeProvider(Object tag, Observable provider) {
        if (getNodeType(tag) == NodeType.ObservableRef) {
            nodeMap.remove(tag);
        } else {
            Source s = new Source(provider, tag);
            subscriptions.remove(subscriptionMap.get(s));
            subscriptionMap.remove(s);
        }
    }

    @Override
    public final Observable getNode(Object tag) {
        //make sure we expose it asObservable hide node's identity
        return getNodeInternal(tag).asObservable();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Observable<T> getNodeFiltered(Object tag, final Class<T> filterClass) {
        return getNode(tag).filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object obj) {
                return filterClass.isAssignableFrom(obj.getClass());
            }
        });
    }


    private Observable getNodeInternal(Object tag) {
        Observable res = nodeMap.get(tag);
        if (res == null) {
            res = addNode(tag);
        }
        return res;
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
            case ObservableRef:
                res = null;
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

    @Override
    public final void emit(Object tag, Object event) {
        if(!canTriggerEmit(tag)) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting events on Node(%s) not allowed.", tag));
        }
        if (getNodeType(tag)==NodeType.ObservableRef) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting event not possible. Node(%s) represents immutable stream.", tag));
        }
        Observable node = getNodeInternal(tag);
        if (Action1.class.isAssignableFrom(node.getClass())) {
            ((Action1) node).call(event);
        } else if (Observer.class.isAssignableFrom(node.getClass())) {
            ((Observer) node).onNext(event);
        } else {
            //should not happen
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Node(%s) type(%s) is not supported! Do we have an alien injection?",
                    tag, node.getClass()));
        }
    }

    @Override
    public final void clearProviders() {
        subscriptions.clear();
    }

}
