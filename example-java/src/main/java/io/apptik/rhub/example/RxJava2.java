package io.apptik.rhub.example;


import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.apptik.rhub.DefaultRxJava2ObsHub;
import io.apptik.rhub.DefaultRxJava2PubHub;
import io.apptik.rhub.RSHub;
import io.apptik.rhub.RxJava2ObsHub;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subscribers.DefaultSubscriber;

import static io.apptik.rhub.RxJava2ObsHub.RxJava2PubProxyType.BehaviorProcessorProxy;

public class RxJava2 {

    public static void main(String[] args) {
        RxJava2ObsHub rxJava2ObsHub = new DefaultRxJava2ObsHub() {
            @Override
            public ProxyType getProxyType(Object tag) {
                if (tag.equals("src2")) {
                    return RxJava2ObsProxyType.ObservableRefProxy;
                } else if (tag.equals("topic1Obs")) {
                    return RxJava2ObsProxyType.BehaviorSubjectProxy;
                }
                return RxJava2ObsProxyType.BehaviorSubjectProxy;
            }
        };
        RSHub rxJava2PubHub = new DefaultRxJava2PubHub() {
            @Override
            public ProxyType getProxyType(Object tag) {
                if (tag.equals("src2")) {
                    return CoreProxyType.PublisherRefProxy;
                } else if (tag.equals("topic1Obs")) {
                    return BehaviorProcessorProxy;
                }
                return BehaviorProcessorProxy;
            }
        };
        // generalExample(rxHub);
        shieldExample(rxJava2ObsHub, rxJava2PubHub);
    }

    private static void generalExample(RxJava2ObsHub rxJava2Hub) {
        Observable src1 = Observable.fromArray(new Integer[]{1, 3, 5, 7, 11, 13});
        Observable src2 = Observable.interval(1, TimeUnit.SECONDS);
        rxJava2Hub.addUpstream("src1", src1);

        rxJava2Hub.getPub("src1").subscribe(o -> {
            System.out.println("consumer1 (src1) got: " + o);
        });

        rxJava2Hub.getPub("src1").subscribe(System.out::println);
        rxJava2Hub.getPub("src1.1").subscribe(o -> {
            System.out.println("consumer1 (src1.1) got: " + o);
        });

        rxJava2Hub.addUpstream("src1.1", src1.repeat(1));
        rxJava2Hub.addUpstream("src2", src2.buffer(Integer.MAX_VALUE));

        rxJava2Hub.getPub("src1").subscribe(o -> {
            System.out.println("consumer2 (src1) got: " + o);
        });

        rxJava2Hub.getPub("src1.1").subscribe(o -> {
            System.out.println("consumer2 (src1.1) got: " + o);
        });
        rxJava2Hub.getPub("src2").subscribe(o -> {
            System.out.println("consumer2 (src2) got: " + o);
        });
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                rxJava2Hub.addUpstream("src1.1", Observable.interval(1, TimeUnit.SECONDS));
                Thread.sleep(5000);
                rxJava2Hub.clearUpstream();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * this example demonstrates the Concept os a "ShieldObs". ShieldObs could be seen and a
     * abstraction layer or contract for the consumers. Its purpose is to filter, secure and/or
     * adapt the data coming from the RxJava2ObsHub and to relieve the consumer from this tasks.
     * <p>
     * It can compared to an Arduino shield :)
     */
    private static void shieldExample(RxJava2ObsHub rxJava2ObsHub, RSHub rxJava2PubHub) {

        ShieldObs shieldObs = new ShieldObs(rxJava2ObsHub);
        ShieldPub shieldPub = new ShieldPub(rxJava2PubHub);
        Observable srcIntObs = Observable.fromArray(
                new Long[]{1l, 3l, 5l, 7l, 11l, 13l, 101l, 201l, 301l, 401l, 501l})
                //just let it complete later
                .mergeWith(Observable.interval(1l, TimeUnit.MILLISECONDS));
        Flowable srcIntPub = Flowable.fromArray(
                new Long[]{1l, 3l, 5l, 7l, 11l, 13l, 101l, 201l, 301l, 401l, 501l})
                //just let it complete later
                .mergeWith(Flowable.interval(1l, TimeUnit.MILLISECONDS));
        Observable srcStringObs = Observable.fromArray(new String[]{"a", "b", "c", "d", "f"});
        Flowable srcStringPub = Flowable.fromArray(new String[]{"a", "b", "c", "d", "f"});

        shieldObs.getNamesObservable().subscribe(o -> {
            System.out.println(String.format("consumer1(%s) got:%s",
                    Thread.currentThread().getName(), o));

        });

        shieldObs.getBigOnesObservable().subscribe(o -> {
            System.out.println(String.format("consumer2(%s) got:%s",
                    Thread.currentThread().getName(), o));
            if (o.equals(301l)) {
                rxJava2ObsHub.removeUpstream("topic1Obs", srcIntObs);
            }
        });

        shieldPub.getNamesPublisher()
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

        shieldPub.getBigOnesPublisher()
                .subscribe(new DefaultSubscriber<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        System.out.println(String.format("Subscriber2(%s) got:%s",
                                Thread.currentThread().getName(), aLong));
                        if (aLong.equals(301l)) {
                            rxJava2PubHub.removeUpstream("topic1", srcIntPub);
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

        rxJava2ObsHub.addUpstream("topic1Obs", srcIntObs);
        rxJava2ObsHub.addUpstream("topic1Obs", srcStringObs);
        rxJava2PubHub.addUpstream("topic1", srcIntPub);
        rxJava2PubHub.addUpstream("topic1", srcStringPub);
        // srcInt.subscribe(System.out::println);

        //wait a little
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static class ShieldObs {
        final RxJava2ObsHub rxJava2Hub;

        ShieldObs(RxJava2ObsHub rxJava2Hub) {
            this.rxJava2Hub = rxJava2Hub;
        }

        Observable<String> getNamesObservable() {
            return rxJava2Hub.getPub("topic1Obs", String.class);
        }

        Observable<Long> getBigOnesObservable() {
            return rxJava2Hub.getPub("topic1Obs", Long.class)
                    .filter(o -> o > 100);
        }
    }

    private static class ShieldPub {
        final RSHub rxJava2Hub;

        ShieldPub(RSHub rxJava2Hub) {
            this.rxJava2Hub = rxJava2Hub;
        }

        Publisher<String> getNamesPublisher() {
            return rxJava2Hub.getPub("topic1", String.class);
        }

        Publisher<Long> getBigOnesPublisher() {
            return ((Flowable<Long>) rxJava2Hub.getPub("topic1", Long.class))
                    .filter(o -> o > 100);
        }
    }
}
