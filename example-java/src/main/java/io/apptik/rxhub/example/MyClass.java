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
       // generalExample(rxHub);
        shieldExample(rxHub);
    }

    private static void generalExample(RxHub rxHub) {
        Observable src1 = Observable.from(new Integer[] {1,3,5,7,11,13});
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
                rxHub.clearProviders();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * this example demonstrates the Concept os a "Shield". Shield could be seen and a
     * abstraction layer or contract for the consumers. Its purpose is to filter, secure and/or
     * adapt the data coming from the RxHub and to relieve the consumer from this tasks.
     *
     * It can compared to an Arduino shield :)
     */
    private static void shieldExample(RxHub rxHub) {

        Shield shield = new Shield(rxHub);
        Observable srcInt = Observable.from(new Integer[] {1,3,5,7,11,13,101,201,301,401,501});
        Observable srcString = Observable.from(new String[] {"a","b","c","d","f"});

        shield.getNames().subscribe(o -> {
            System.out.println("consumer1 got: " + o);
        });

        shield.getBigOnes().subscribe(o -> {
            System.out.println("consumer2 got: " + o);
        });

        rxHub.addProvider("topic1", srcInt);
        rxHub.addProvider("topic1", srcString);

    }

    private static class Shield {
        final RxHub rxHub;
        Shield(RxHub rxHub) {
           this.rxHub = rxHub;
        }

        Observable<String> getNames() {
            return rxHub.getNodeFiltered("topic1", String.class);
        }

        Observable<Integer> getBigOnes() {
            return rxHub.getNodeFiltered("topic1", Integer.class)
                    .filter(o -> o > 100);
        }
    }
}
