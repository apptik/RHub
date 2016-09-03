package io.apptik.rxhub;


public interface RxJava2Hub extends RxHub {

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
