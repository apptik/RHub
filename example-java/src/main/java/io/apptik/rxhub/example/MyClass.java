package io.apptik.rxhub.example;


import java.util.concurrent.TimeUnit;

import io.apptik.rxhub.DefaultRxHub;
import io.apptik.rxhub.RxHub;
import rx.Observable;

public class MyClass {

    public static void main(String[] args) {
        RxHub rxHub = new DefaultRxHub() {
            @Override
            public NodeType getNodeType(Object tag) {
                if(tag.equals("src2")) {
                    return NodeType.ObservableRef;
                }
                return super.getNodeType(tag);
            }
        };
        Observable src1 = Observable.just(5).from(new Integer[] {1,3,5,7,11,13});
        Observable src2 = Observable.interval(1, TimeUnit.SECONDS);
        rxHub.addProvider("src1", src1);

        rxHub.getNode("src1").subscribe(o -> {
            System.out.println("consumer1 (src1) got: " + o);
        });

        rxHub.getNode("src1").subscribe(System.out::println);
        rxHub.getNode("src1.1").subscribe(o -> {
            System.out.println("consumer1 (src1.1) got: " + o);
        });

        rxHub.addProvider("src1.1", src1.repeat(1));
        rxHub.addProvider("src2", src2.onBackpressureBuffer());

        rxHub.getNode("src1").subscribe(o -> {
            System.out.println("consumer2 (src1) got: " + o);
        });

        rxHub.getNode("src1.1").subscribe(o -> {
            System.out.println("consumer2 (src1.1) got: " + o);
        });
        rxHub.getNode("src2").subscribe(o -> {
            System.out.println("consumer2 (src2) got: " + o);
        });
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                rxHub.addProvider("src1.1", Observable.interval(1, TimeUnit.SECONDS));
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

}
