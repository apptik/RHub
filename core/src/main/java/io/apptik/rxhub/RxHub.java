package io.apptik.rxhub;


import rx.Observable;


/**
 * RxJava based Hub connecting Observables and Observers so that Observers can receive events
 * without knowledge of which Observables, if any, there are,
 * while maintaining clear connection between them.
 *
 * @see AbstractRxHub
 */
public interface RxHub {

    /**
     * Subscribes Node to {@link Observable}.
     * If there is no Node with the specific tag a new one will be created
     * except if the node is of type {@link NodeType#ObservableRef}
     *
     * @param tag      the ID of the Node
     * @param provider the Observable to subscribe to
     */
    void addProvider(Object tag, Observable provider);

    /**
     * Unsubscribe {@link Observable} from a Node
     *
     * @param tag      the ID of the Node
     * @param provider the Observable to unsubscribe from
     */
    void removeProvider(Object tag, Observable provider);

    /**
     * Clears all subscriptions of all Nodes
     */
    void clearProviders();

    /**
     * Returns the Node Observable identified by the tag
     *
     * @param tag the ID of the Node
     * @return the Node Observable
     */
    Observable getNode(Object tag);

    /**
     * Type safe variant of {@link #getNode(Object)}.
     * Returns the Node Observable identified by the tag and filtered by the Class provided
     *
     * @param tag the ID of the Node
     * @param filterClass the Class to filter the observable by
     * @param <T> the Type of the events the returned Observable will emit
     * @return the Filtered Node Observable
     */
    <T> Observable<T> getNodeFiltered(Object tag, Class<T> filterClass);

    /**
     * Manually emit event to a specific Node. In order to prohibit this behaviour override this
     *
     * @param tag   the ID of the Node
     * @param event the Event to emit
     */
    void emit(Object tag, Object event);

    NodeType getNodeType(Object tag);

    boolean isNodeThreadsafe(Object tag);


    class Source {
        final Observable provider;
        final Object tag;

        Source(Observable provider, Object tag) {
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
        ReplayRelay,
        ObservableRef
    }
}
