package io.apptik.rhub;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.apptik.rhub.Proxy.Event.ErrorEvent;
import io.apptik.rhub.RxJava2ObsHub.ObservableSource;
import io.apptik.rhub.RxJava2ObsHub.RxJava2ObsProxyType;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.Subject;

import static io.apptik.rhub.Proxy.Event.COMPLETE;

class SubjProxy {

    final Subject proc;
    private final boolean wrapTE;

    private final Map<ObservableSource, Disposable> subscriptions = new ConcurrentHashMap<>();

    SubjProxy(Subject proc, boolean wrapTE) {
        this.proc = proc;
        this.wrapTE = wrapTE;
    }

    /**
     * Subscribes Proxy to {@link io.reactivex.Observable}.
     * If there is no Proxy with the specific tag a new one will be created
     * except if the Proxy is of type {@link RxJava2ObsProxyType#ObservableRefProxy}
     *
     * @param tag       the ID of the Proxy
     * @param observable the Observable to subscribe to
     */
    void addObs(final Object tag, final Observable observable) {
        observable.subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                subscriptions.put(new ObservableSource(observable, tag), d);
            }

            @Override
            public void onNext(Object value) {
                proc.onNext(value);
            }

            @Override
            public void onError(Throwable e) {
                if (wrapTE) {
                    proc.onNext(new ErrorEvent(e));
                } else {
                    proc.onError(e);
                }
            }

            @Override
            public void onComplete() {
                if (wrapTE) {
                    proc.onNext(COMPLETE);
                } else
                    proc.onComplete();
            }
        });
    }

    /**
     * Unsubscribe {@link Observable} from a Proxy
     *
     * @param tag       the ID of the Proxy
     * @param observable the Observable to unsubscribe from
     */
    void removeObs(Object tag, Observable observable) {
        synchronized (subscriptions) {
            ObservableSource src = new ObservableSource(observable, tag);
            Disposable s = subscriptions.get(src);
            if (s != null) {
                s.dispose();
                subscriptions.remove(src);
            }
        }
    }

    void clear() {
        synchronized (subscriptions) {
            for (Disposable s : subscriptions.values()) {
                s.dispose();
            }
            subscriptions.clear();
        }
    }
}
