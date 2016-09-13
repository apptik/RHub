package io.apptik.rxhub;


import org.reactivestreams.Publisher;

/**
 * Reactive Streams based Hub connecting Publishers and Observers so that Observers can receive
 * events without knowledge of which Publishers, if any, there are,
 * while maintaining clear connection between them.
 *
 */
public interface RxHub {

    /**
     * Subscribes Node to {@link Publisher}.
     * If there is no Node with the specific tag a new one will be created
     * except if the node is of type {@link CoreNodeType#PublisherRef}
     *
     * @param tag      the ID of the Node
     * @param provider the Publisher to subscribe to
     */
    void addProvider(Object tag, Publisher provider);

    /**
     * Unsubscribe {@link Publisher} from a Node
     *
     * @param tag      the ID of the Node
     * @param provider the Publisher to unsubscribe from
     */
    void removeProvider(Object tag, Publisher provider);

    /**
     * Clears all subscriptions of all Nodes
     */
    void clearProviders();

    /**
     * Returns the Node Publisher identified by the tag
     *
     * @param tag the ID of the Node
     * @return the Node Publisher
     */
    Publisher getNode(Object tag);

    /**
     * Type safe variant of {@link #getNode(Object)}.
     * Returns the Node Publisher identified by the tag and filtered by the Class provided
     *
     * @param tag the ID of the Node
     * @param filterClass the Class to filter the Publisher by
     * @param <T> the Type of the events the returned Publisher will emit
     * @return the Filtered Node Publisher
     */
    <T> Publisher<T> getNodeFiltered(Object tag, Class<T> filterClass);

    /**
     * Manually emit event to a specific Node. In order to prohibit this behaviour override this
     *
     * @param tag   the ID of the Node
     * @param event the Event to emit
     */
    void emit(Object tag, Object event);

    /**
     * Implement this to return the type of node per tag
     * @param tag the identifier of the node
     * @return the Node Type
     */
    NodeType getNodeType(Object tag);

    /**
     * Implement this to return if the node is threadsafe
     * @param tag the identifier of the node
     * @return true if the node is threadsafe, false otherwise
     */
    boolean isNodeThreadsafe(Object tag);

    /**
     * Implement this to return the ability to manually (in non-Rx fashion) emit events
     * @param tag the identifier of the node
     * @return true when manual emit is possible, false otherwise
     */
    boolean canTriggerEmit(Object tag);

    class Source {
        final Publisher provider;
        final Object tag;

        Source(Publisher provider, Object tag) {
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

    interface NodeType {
    }

    enum CoreNodeType implements NodeType{
        PublisherRef
    }
}
