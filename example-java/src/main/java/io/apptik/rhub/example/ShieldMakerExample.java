package io.apptik.rhub.example;


import org.reactivestreams.Publisher;

import io.apptik.rhub.DefaultReactorHub;
import io.apptik.rhub.DefaultRxJava1Hub;
import io.apptik.rhub.DefaultRxJava2ObsHub;
import io.apptik.rhub.DefaultRxJava2PubHub;
import io.apptik.rhub.RSHub;
import io.apptik.rhub.RxJava1Hub;
import io.apptik.rhub.RxJava2ObsHub;
import io.apptik.rhub.shield.ShieldMakerRS;
import io.apptik.rhub.shield.ShieldMakerRxJava1;
import io.apptik.rhub.shield.ShieldMakerRxJava2;
import io.reactivex.Flowable;
import io.reactivex.subscribers.DefaultSubscriber;
import rx.Observable;

public class ShieldMakerExample {
    public static void main(String[] args) {
        exampleRxJava1();
        exampleRxJava2();
        exampleRxJava2RS();
        exampleReactorRS();
    }

    private static void exampleRxJava1() {
        RxJava1Hub rxJava1Hub = new DefaultRxJava1Hub();
        TestShieldRxJava1 testShieldRxJava1 = ShieldMakerRxJava1.make(TestShieldRxJava1.class,rxJava1Hub);

        Observable<String> tag1String =  testShieldRxJava1.getTag1String();
        tag1String.subscribe(System.out::println);
        rxJava1Hub.emit("tag1", "Here we go :)");
        rxJava1Hub.emit("tag1",777);
        Observable<Integer> tag1Int =  testShieldRxJava1.getTag1Int();
        tag1Int.subscribe(System.out::println);
        rxJava1Hub.emit("tag1",999);
        testShieldRxJava1.addTag1Provider(Observable.just(1,2,3,4,"a","b","c","d"));
        testShieldRxJava1.addTag1IntProvider(Observable.just(1));
    }

    private static void exampleRxJava2() {
        RxJava2ObsHub rxJava2Hub = new DefaultRxJava2ObsHub();
        TestShieldRxJava2 testShieldRxJava2 = ShieldMakerRxJava2.make(TestShieldRxJava2.class,
                rxJava2Hub);

        io.reactivex.Observable<String> tag1String =  testShieldRxJava2.getTag1String();
        tag1String.subscribe(System.out::println);
        rxJava2Hub.emit("tag1", "Here we go :)");
        rxJava2Hub.emit("tag1",777);
        io.reactivex.Observable<Integer> tag1Int =  testShieldRxJava2.getTag1Int();
        tag1Int.subscribe(System.out::println);
        rxJava2Hub.emit("tag1",999);
        testShieldRxJava2.addTag1Provider(io.reactivex.Observable.just(1,2,3,4,"a","b","c","d"));
        testShieldRxJava2.addTag1IntProvider(io.reactivex.Observable.just(1));
    }

    private static void exampleRxJava2RS() {
        RSHub rxJava2Hub = new DefaultRxJava2PubHub();
        TestShieldRS testShieldRS = ShieldMakerRS.make(TestShieldRS.class,
                rxJava2Hub);

        Publisher<String> tag1String =  testShieldRS.getTag1String();
        tag1String.subscribe(new SimpleOne());
        rxJava2Hub.emit("tag1", "Here we go :)");
        rxJava2Hub.emit("tag1",777);
        Publisher<Integer> tag1Int =  testShieldRS.getTag1Int();
        tag1Int.subscribe(new SimpleOne());
        rxJava2Hub.emit("tag1",999);
        testShieldRS.addTag1Provider(Flowable.just(1,2,3,4,"a","b","c","d"));
        testShieldRS.addTag1IntProvider(Flowable.just(1));
    }

    private static void exampleReactorRS() {
        RSHub reactorHub = new DefaultReactorHub();
        TestShieldRS testShieldRS = ShieldMakerRS.make(TestShieldRS.class,
                reactorHub);

        Publisher<String> tag1String =  testShieldRS.getTag1String();
        tag1String.subscribe(new SimpleOne());
        reactorHub.emit("tag1", "Here we go :)");
        reactorHub.emit("tag1",777);
        Publisher<Integer> tag1Int =  testShieldRS.getTag1Int();
        tag1Int.subscribe(new SimpleOne());
        reactorHub.emit("tag1",999);
        testShieldRS.addTag1Provider(Flowable.just(1,2,3,4,"a","b","c","d"));
        testShieldRS.addTag1IntProvider(Flowable.just(1));
    }


    private static class SimpleOne extends DefaultSubscriber {

        @Override
        public void onNext(Object o) {
            System.out.println(o);
        }

        @Override
        public void onError(Throwable t) {

        }

        @Override
        public void onComplete() {

        }
    }
}
