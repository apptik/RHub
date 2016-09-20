package io.apptik.rxhub;


import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.apptik.rxhub.RxHub.Source;

class Proxy {

    final Processor proc;
    private final boolean wrapTE;

    private final Map<Source, Subscription> subscriptions = new ConcurrentHashMap<>();

    Proxy(Processor proc, boolean wrapTE) {
        this.proc = proc;
        this.wrapTE = wrapTE;
    }

    /**
     * Subscribes Proxy to {@link Publisher}.
     * If there is no Proxy with the specific tag a new one will be created
     * except if the Proxy is of type {@link RxHub.CoreProxyType#PublisherRefProxy}
     *
     * @param tag       the ID of the Proxy
     * @param publisher the Publisher to subscribe to
     */
    void addPub(final Object tag, final Publisher publisher) {

        publisher.subscribe(new Subscriber() {
            @Override
            public void onSubscribe(final Subscription s) {
                subscriptions.put(new Source(publisher, tag), s);
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Object o) {
                proc.onNext(o);
            }

            @Override
            public void onError(Throwable t) {
                if (wrapTE) {
                    proc.onNext(new Event.ErrorEvent(t));
                } else {
                    proc.onError(t);
                }
            }

            @Override
            public void onComplete() {
                if (wrapTE) {
                    proc.onNext(Event.COMPLETE);
                } else
                    proc.onComplete();
            }
        });
    }

    /**
     * Unsubscribe {@link Publisher} from a Proxy
     *
     * @param tag       the ID of the Proxy
     * @param publisher the Publisher to unsubscribe from
     */
    void removePub(Object tag, Publisher publisher) {
        synchronized (subscriptions) {
            Source src = new Source(publisher, tag);
            Subscription s = subscriptions.get(src);
            if (s != null) {
                s.cancel();
                subscriptions.remove(src);
            }
        }
    }

    void clear() {
        synchronized (subscriptions) {
            for (Subscription s : subscriptions.values()) {
                s.cancel();
            }
            subscriptions.clear();
        }
    }

    public enum Event {
        COMPLETE;

        /**
         * Wraps a Throwable form onError.
         */
        public static final class ErrorEvent implements Serializable {

            private static final long serialVersionUID = -2443670933703181488L;

            final Throwable e;

            ErrorEvent(Throwable e) {
                this.e = e;
            }

            @Override
            public boolean equals(Object o) {

                if (this == o) return true;
                if (!(o instanceof ErrorEvent)) return false;

                ErrorEvent that = (ErrorEvent) o;

                return e != null ? e.equals(that.e) : that.e == null;

            }

            @Override
            public int hashCode() {
                return e != null ? e.hashCode() : 0;
            }

            @Override

            public String toString() {
                return "onError[" + e + "]";
            }
        }
    }
}
