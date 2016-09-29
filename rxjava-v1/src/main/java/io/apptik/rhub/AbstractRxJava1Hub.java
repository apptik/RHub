package io.apptik.rhub;


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
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import static io.apptik.rhub.RxJava1Hub.RxJava1ProxyType.BehaviorRelayProxy;
import static io.apptik.rhub.RxJava1Hub.RxJava1ProxyType.BehaviorSubjectProxy;
import static io.apptik.rhub.RxJava1Hub.RxJava1ProxyType.ObservableRefProxy;
import static io.apptik.rhub.RxJava1Hub.RxJava1ProxyType.PublishRelayProxy;
import static io.apptik.rhub.RxJava1Hub.RxJava1ProxyType.PublishSubjectProxy;
import static io.apptik.rhub.RxJava1Hub.RxJava1ProxyType.ReplayRelayProxy;
import static io.apptik.rhub.RxJava1Hub.RxJava1ProxyType.ReplaySubjectProxy;


/**
 * Base implementation of {@link RxJava1Hub}
 * Essentially this is a collection of {@link Observable} proxies which can also subscribe to other
 * Observables and pass events to their Subscribers
 * <p/>
 * Proxies can be either {@link Subject} or {@link Relay}. Proxies are identified by their Tags.
 * Proxies subscribes to Observables however each subscription created is
 * per {@link Source}. A Source is identified by Observable and a Tag.
 * For example when Observable A is added with Tag T1 and Tag T2. Two proxies are created receiving
 * the same events. Each of those proxies can be used and un-subscribed from Observable A
 * independently.
 * <p/>
 * Observers subscribe to a Proxy. Observers does not need to know about the source of the Events
 * i.e the Observers that the Proxies is subscribed to.
 * <p/>
 * To fetch the Proxy to subscribe to {@link AbstractRxJava1Hub#getPub(Object)} must be
 * called.
 * <p/>
 * Non-Rx code can also call {@link AbstractRxJava1Hub#emit(Object, Object)} to manually emit Events
 * through specific Proxy.
 */
public abstract class AbstractRxJava1Hub implements RxJava1Hub {

    private final Map<Object, RelayProxy> proxyRelayMap = new ConcurrentHashMap<>();
    private final Map<Object, SubjV1Proxy> proxySubjMap = new ConcurrentHashMap<>();
    private final Map<Object, Observable> directObsMap = new ConcurrentHashMap<>();

    @Override
    public final Removable addUpstream(final Object tag, final Observable observable) {
        ProxyType proxyType = getProxyType(tag);
        if (proxyType == ObservableRefProxy) {
            directObsMap.put(tag, observable);
        } else {
            getObservableProxyInternal(tag);
            if (proxyType == PublishSubjectProxy
                    || proxyType == BehaviorSubjectProxy
                    || proxyType == ReplaySubjectProxy) {
                SubjV1Proxy proxy = proxySubjMap.get(tag);
                if (proxy != null) {
                    proxy.addObs(tag, observable);
                }
            }

            if (proxyType == PublishRelayProxy
                    || proxyType == BehaviorRelayProxy
                    || proxyType == ReplayRelayProxy) {
                RelayProxy proxy = proxyRelayMap.get(tag);
                if (proxy != null) {
                    proxy.addObs(tag, observable);
                }
            }
        }
        return new Removable() {
            @Override
            public void remove() {
                AbstractRxJava1Hub.this.removeUpstream(tag, observable);
            }
        };
    }

    @Override
    public final void removeUpstream(Object tag, Observable observable) {
        ProxyType proxyType = getProxyType(tag);
        if (proxyType == ObservableRefProxy) {
            directObsMap.remove(tag);
        }
        if (proxyType == PublishSubjectProxy
                || proxyType == BehaviorSubjectProxy
                || proxyType == ReplaySubjectProxy) {
            SubjV1Proxy proxy = proxySubjMap.get(tag);
            if (proxy != null) {
                proxy.removeObs(tag, observable);
            }
        }

        if (proxyType == PublishRelayProxy
                || proxyType == BehaviorRelayProxy
                || proxyType == ReplayRelayProxy) {
            RelayProxy proxy = proxyRelayMap.get(tag);
            if (proxy != null) {
                proxy.removeObs(tag, observable);
            }
        }
    }

    @Override
    public final Observable getPub(Object tag) {
        Observable res = getObservableProxyInternal(tag);
        if (getProxyType(tag) == ObservableRefProxy) {
            return res;
        } else {
            //make sure we expose it as Observable hide proxy's identity
            return res.asObservable();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Observable<T> getPub(Object tag, final Class<T> filterClass) {
        return getObservableProxyInternal(tag).filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object obj) {
                return filterClass.isAssignableFrom(obj.getClass());
            }
        });
    }

    private Observable getObservableProxyInternal(Object tag) {
        Observable res = null;
        ProxyType proxyType = getProxyType(tag);
        if (proxyType == ObservableRefProxy) {
            res = directObsMap.get(tag);
        }
        if (proxyType == PublishSubjectProxy
                || proxyType == BehaviorSubjectProxy
                || proxyType == ReplaySubjectProxy) {
            SubjV1Proxy proxy = proxySubjMap.get(tag);
            if (proxy != null) {
                res = proxy.subj;
            }

        }

        if (proxyType == PublishRelayProxy
                || proxyType == BehaviorRelayProxy
                || proxyType == ReplayRelayProxy) {
            RelayProxy proxy = proxyRelayMap.get(tag);
            if (proxy != null) {
                res = proxy.relay;
            }
        }

        if (res == null) {
            res = createProxy(tag);
        }
        return res;
    }

    private Observable createProxy(Object tag) {
        Observable res;
        ProxyType nt = getProxyType(tag);
        if (nt.equals(RxJava1ProxyType.BehaviorSubjectProxy)) {
            res = BehaviorSubject.create();
        } else if (nt.equals(RxJava1ProxyType.PublishSubjectProxy)) {
            res = PublishSubject.create();
        } else if (nt.equals(RxJava1ProxyType.ReplaySubjectProxy)) {
            res = ReplaySubject.create();
        } else if (nt.equals(RxJava1ProxyType.BehaviorRelayProxy)) {
            res = BehaviorRelay.create();
        } else if (nt.equals(RxJava1ProxyType.PublishRelayProxy)) {
            res = PublishRelay.create();
        } else if (nt.equals(RxJava1ProxyType.ReplayRelayProxy)) {
            res = ReplayRelay.create();
        } else if (nt.equals(RxJava1ProxyType.ObservableRefProxy)) {
            throw new IllegalStateException("Cannot create ObservableRefProxy, " +
                    "it must be added before.");
            //should not happen;
        } else {
            throw new IllegalStateException("Unknown ProxyType");
        }

        if (nt.equals(RxJava1ProxyType.BehaviorSubjectProxy)
                || nt.equals(RxJava1ProxyType.PublishSubjectProxy)
                || nt.equals(RxJava1ProxyType.ReplaySubjectProxy)) {
            if (isProxyThreadsafe(tag)) {
                res = new SerializedSubject((Subject) res);
            }
            proxySubjMap.put(tag, new SubjV1Proxy((Subject) res));

        } else if (nt.equals(RxJava1ProxyType.BehaviorRelayProxy)
                || nt.equals(RxJava1ProxyType.PublishRelayProxy)
                || nt.equals(RxJava1ProxyType.ReplayRelayProxy)) {
            if (isProxyThreadsafe(tag)) {
                res = new SerializedRelay((Relay) res);
            }
            proxyRelayMap.put(tag, new RelayProxy((Relay) res));

        }

        return res;
    }

    @Override
    public final void emit(Object tag, Object event) {
        if (!canTriggerEmit(tag)) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting events on Tag(%s) not allowed.", tag));
        }
        ProxyType proxyType = getProxyType(tag);
        if (proxyType == ObservableRefProxy) {
            throw new IllegalStateException(String.format(Locale.ENGLISH,
                    "Emitting event not possible. Tag(%s) represents immutable stream.", tag));
        } else {
            Observable proxy = getObservableProxyInternal(tag);
            if (proxyType == BehaviorRelayProxy ||
                    proxyType == PublishRelayProxy ||
                    proxyType == ReplayRelayProxy) {
                ((Action1) proxy).call(event);
            } else if (proxyType == BehaviorSubjectProxy ||
                    proxyType == PublishSubjectProxy ||
                    proxyType == ReplaySubjectProxy) {
                ((Observer) proxy).onNext(event);
            } else {
                //should not happen
                throw new IllegalStateException(String.format(Locale.ENGLISH,
                        "Proxy(%s) type(%s) is not supported! Do we have an alien injection?",
                        tag, proxy.getClass()));
            }
        }
    }


    @Override
    public final void clearUpstream() {
        for (Map.Entry<Object, SubjV1Proxy> entries : proxySubjMap.entrySet()) {
            SubjV1Proxy proxy = entries.getValue();
            proxy.clear();
        }
        for (Map.Entry<Object, RelayProxy> entries : proxyRelayMap.entrySet()) {
            RelayProxy proxy = entries.getValue();
            proxy.clear();
        }
        directObsMap.clear();
    }

    @Override
    public void resetProxy(Object tag) {
        ProxyType proxyType = getProxyType(tag);
        if (getProxyType(tag) == ObservableRefProxy) {
            directObsMap.remove(tag);
        } else {
            if (proxyType == BehaviorRelayProxy ||
                    proxyType == PublishRelayProxy ||
                    proxyType == ReplayRelayProxy) {
                RelayProxy proxy = proxyRelayMap.get(tag);
                if (proxy != null) {
                    proxy.clear();
                    proxyRelayMap.remove(tag);
                }
            } else if (proxyType == BehaviorSubjectProxy ||
                    proxyType == PublishSubjectProxy ||
                    proxyType == ReplaySubjectProxy) {
                SubjV1Proxy proxy = proxySubjMap.get(tag);
                if (proxy != null) {
                    proxy.clear();
                    proxy.subj.onCompleted();
                    proxySubjMap.remove(tag);
                }
            }
        }
    }

    @Override
    public void removeUpstream(Object tag) {
        ProxyType proxyType = getProxyType(tag);
        if (getProxyType(tag) == ObservableRefProxy) {
            directObsMap.remove(tag);
        } else {
            if (proxyType == BehaviorRelayProxy ||
                    proxyType == PublishRelayProxy ||
                    proxyType == ReplayRelayProxy) {
                RelayProxy proxy = proxyRelayMap.get(tag);
                if (proxy != null) {
                    proxy.clear();
                    proxyRelayMap.remove(tag);
                }
            } else if (proxyType == BehaviorSubjectProxy ||
                    proxyType == PublishSubjectProxy ||
                    proxyType == ReplaySubjectProxy) {
                SubjV1Proxy proxy = proxySubjMap.get(tag);
                if (proxy != null) {
                    proxy.clear();
                    proxySubjMap.remove(tag);
                }
            }
        }
    }

}
