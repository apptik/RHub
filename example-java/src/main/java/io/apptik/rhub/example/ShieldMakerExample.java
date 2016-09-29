package io.apptik.rhub.example;


import io.apptik.rhub.DefaultRxJava1Hub;
import io.apptik.rhub.RxJava1Hub;
import io.apptik.rhub.shield.ShieldMakerRxJava1;
import rx.Observable;

public class ShieldMakerExample {
    public static void main(String[] args) {
        example1();
    }

    private static void example1() {
        RxJava1Hub rxJava1Hub = new DefaultRxJava1Hub();
        TestShield testShield = ShieldMakerRxJava1.make(TestShield.class,rxJava1Hub);

        Observable<String> tag1String =  testShield.getTag1String();
        tag1String.subscribe(System.out::println);
        rxJava1Hub.emit("tag1", "Here we go :)");
        rxJava1Hub.emit("tag1",777);
        Observable<Integer> tag1Int =  testShield.getTag1Int();
        tag1Int.subscribe(System.out::println);
        rxJava1Hub.emit("tag1",999);
        testShield.addTag1Provider(Observable.just(1,2,3,4,"a","b","c","d"));
        testShield.addTag1IntProvider(Observable.just(1));
    }
}
