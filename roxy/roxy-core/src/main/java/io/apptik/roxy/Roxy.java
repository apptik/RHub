package io.apptik.roxy;


import java.io.Serializable;

public interface Roxy<P> {

    /**
     * Subscribes Proxy to event provider.
     * If there is no Proxy with the specific tag a new one will be created
     *
     * @param publisher the Publisher to subscribe to
     */
    Removable addUpstream(P publisher);

    void removeUpstream(P publisher);
    P pub();
    void emit(Object event);
    void complete();
    void clear();
    TePolicy tePolicy();

    /**
     *
     */
    interface ProxyType {
    }

    enum TePolicy {
        PASS,WRAP,SKIP
    }

    enum Event {
        COMPLETE;

        /**
         * Wraps a Throwable form onError.
         */
        public static final class ErrorEvent implements Serializable {

            private static final long serialVersionUID = -2443670933703181488L;

            final Throwable e;

            public ErrorEvent(Throwable e) {
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
