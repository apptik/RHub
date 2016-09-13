package io.apptik.rxhub;


import io.reactivex.Observable;

/**
 *
 */
public interface RxJava2Hub extends RxHub {

    /**
     * Subscribes Node to {@link Observable}.
     * If there is no Node with the specific tag a new one will be created
     * except if the node is of type {@link RxJava2NodeType#ObservableRef}
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
    Observable getNodeObservable(Object tag);

    /**
     * Type safe variant of {@link #getNode(Object)}.
     * Returns the Node Observable identified by the tag and filtered by the Class provided
     *
     * @param tag         the ID of the Node
     * @param filterClass the Class to filter the observable by
     * @param <T>         the Type of the events the returned Observable will emit
     * @return the Filtered Node Observable
     */
    <T> Observable<T> getNodeFilteredObservable(Object tag, Class<T> filterClass);


    enum RxJava2NodeType implements NodeType {
        BehaviorProcessor,
        PublishProcessor,
        ReplayProcessor,

        BehaviorSubject,
        PublishSubject,
        ReplaySubject,
        //TODO possibly coming up later
//        BehaviorRelay,
//        PublishRelay,
//        ReplayRelay,
        ObservableRef
    }
}
