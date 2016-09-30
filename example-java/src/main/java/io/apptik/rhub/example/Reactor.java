package io.apptik.rhub.example;


import org.reactivestreams.Publisher;

import io.apptik.rhub.DefaultReactorHub;
import io.apptik.rhub.RSHub;
import io.reactivex.Flowable;
import io.reactivex.subscribers.DefaultSubscriber;
import reactor.core.publisher.Flux;


public class Reactor {

    public static void main(String[] args) {
        RSHub rxHub = new DefaultReactorHub() {
            @Override
            public ProxyType getProxyType(Object tag) {
                if (tag.equals("src2")) {
                    return CoreProxyType.PublisherRefProxy;
                }
                //return ReactorProxyType.BehaviorProcessorProxy;
                return ReactorProxyType.BehaviorSafeProxy;
            }
        };
        // generalExample(rxHub);
        shieldExample(rxHub);
    }

    private static void generalExample(RSHub rxHub) {
//        Publisher src1 = Flux.fromArray(new Integer[]{1, 3, 5, 7, 11, 13});
//        Publisher src2 = Flux.interval(1, TimeUnit.SECONDS);
//        rxHub.addUpstream("src1", src1);
//
//        rxHub.getPub("src1").subscribe(o -> {
//            System.out.println("consumer1 (src1) got: " + o);
//        });
//
//        rxHub.getPub("src1").subscribe(System.out::println);
//        rxHub.getPub("src1.1").subscribe(o -> {
//            System.out.println("consumer1 (src1.1) got: " + o);
//        });
//
//        rxHub.addUpstream("src1.1", src1.repeat(1));
//        rxHub.addUpstream("src2", src2.buffer(Integer.MAX_VALUE));
//
//        rxHub.getPub("src1").subscribe(o -> {
//            System.out.println("consumer2 (src1) got: " + o);
//        });
//
//        rxHub.getPub("src1.1").subscribe(o -> {
//            System.out.println("consumer2 (src1.1) got: " + o);
//        });
//        rxHub.getPub("src2").subscribe(o -> {
//            System.out.println("consumer2 (src2) got: " + o);
//        });
//        new Thread(() -> {
//            try {
//                Thread.sleep(5000);
//                rxHub.addUpstream("src1.1", Flux.interval(1, TimeUnit.SECONDS));
//                Thread.sleep(5000);
//                rxHub.clearUpstream();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
    }

    /**
     * this example demonstrates the Concept os a "ShieldObs". ShieldObs could be seen and a
     * abstraction layer or contract for the consumers. Its purpose is to filter, secure and/or
     * adapt the data coming from the RxJava2ObsHub and to relieve the consumer from this tasks.
     * <p>
     * It can compared to an Arduino shield :)
     */
    private static void shieldExample(RSHub rxHub) {

        Shield shield = new Shield(rxHub);
        Flowable srcIntPub = Flowable.fromArray(
                new Long[]{1l, 3l, 5l, 7l, 11l, 13l, 101l, 201l, 301l, 401l, 501l})
                //just let it complete later
                //.mergeWith(Flux.interval(Duration.ofMillis(10)))
                ;
        Publisher srcStringPub = Flux.fromArray(new String[]{"a", "b", "c", "d", "f"});
        Flowable srcInt2Pub = Flowable.fromArray(
                new Long[]{2l, 4l, 6l, 8l, 12l, 14l, 102l, 202l, 302l, 402l, 502l});

        shield.getNamesPublisher()
                .subscribe(new DefaultSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        System.out.println(String.format("Subscriber1" +
                                        "(%s) got:%s",
                                Thread.currentThread().getName(), s));
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println(String.format("Subscriber1" +
                                        "(%s) error",
                                Thread.currentThread().getName()));
                    }

                    @Override
                    public void onComplete() {
                        System.out.println(String.format("Subscriber1" +
                                        "(%s) done",
                                Thread.currentThread().getName()));
                    }
                });

        shield.getBigOnesPublisher()
                .subscribe(new DefaultSubscriber<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        System.out.println(String.format("Subscriber2(%s) got:%s",
                                Thread.currentThread().getName(), aLong));
                        if (aLong > 300l) {
                            rxHub.removeUpstream("topic1", srcIntPub);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println(String.format("Subscriber2" +
                                        "(%s) error",
                                Thread.currentThread().getName()));
                    }

                    @Override
                    public void onComplete() {
                        System.out.println(String.format("Subscriber2" +
                                        "(%s) done",
                                Thread.currentThread().getName()));
                    }
                });


        rxHub.addUpstream("topic1", srcIntPub);
        rxHub.addUpstream("topic1", srcStringPub);

        // srcInt.subscribe(System.out::println);

        //wait a little
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                shield.getNamesPublisher()
                        .subscribe(new DefaultSubscriber<String>() {
                            @Override
                            public void onNext(String s) {
                                System.out.println(String.format("Subscriber3" +
                                                "(%s) got:%s",
                                        Thread.currentThread().getName(), s));
                            }

                            @Override
                            public void onError(Throwable t) {
                                System.out.println(String.format("Subscriber3" +
                                                "(%s) error",
                                        Thread.currentThread().getName()));
                            }

                            @Override
                            public void onComplete() {
                                System.out.println(String.format("Subscriber3" +
                                                "(%s) done",
                                        Thread.currentThread().getName()));
                            }
                        });
                rxHub.resetProxy("topic1");
                shield.getBigOnesPublisher().subscribe(new DefaultSubscriber<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        System.out.println(String.format("Subscriber4(%s) got:%s",
                                Thread.currentThread().getName(), aLong));
                        if (aLong > 300l) {
                            rxHub.removeUpstream("topic1", srcInt2Pub);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println(String.format("Subscriber4" +
                                        "(%s) error",
                                Thread.currentThread().getName()));
                    }

                    @Override
                    public void onComplete() {
                        System.out.println(String.format("Subscriber4" +
                                        "(%s) done",
                                Thread.currentThread().getName()));
                    }
                });
                rxHub.addUpstream("topic1", srcInt2Pub);
                rxHub.emit("topic1", 1000l);
                rxHub.resetProxy("topic1");
                //no more
                rxHub.emit("topic1", 1001l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static class Shield {
        final RSHub rxHub;

        Shield(RSHub rxHub) {
            this.rxHub = rxHub;
        }

        Publisher<String> getNamesPublisher() {
            return rxHub.getPub("topic1", String.class);
        }

        Publisher<Long> getBigOnesPublisher() {
            return Flowable.fromPublisher(rxHub.getPub("topic1", Long.class))
                    .filter(o -> o > 100l);
        }

    }
}
