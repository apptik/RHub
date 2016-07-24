package io.apptik.rxhub;


import rx.Observable;


/**
 * RxJava based Hub connecting Observables and Observers so that Observers can receive events
 * without knowledge of which Observables, if any, there are,
 * while maintaining clear connection between them.
 * @see AbstractRxHub
 */
public interface RxHub {


    void addProvider(Object tag, Observable provider);

    void removeProvider(Object tag, Observable provider);

    void clearProviders();

    Observable getNode(Object tag);

    void emit(Object tag, Object event);

    NodeType getNodeType(Object tag);

    boolean isNodeThreadsafe(Object tag);


    class Source {
        public final Observable provider;
        public final Object tag;

        public Source(Observable provider, Object tag) {
            this.provider = provider;
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Source source = (Source) o;

            if (!provider.equals(source.provider)) return false;
            return tag.equals(source.tag);

        }

        @Override
        public int hashCode() {
            int result = provider.hashCode();
            result = 31 * result + tag.hashCode();
            return result;
        }
    }

    enum NodeType {
        BehaviorSubject,
        PublishSubject,
        ReplaySubject,
        BehaviorRelay,
        PublishRelay,
        ReplayRelay
    }
}
