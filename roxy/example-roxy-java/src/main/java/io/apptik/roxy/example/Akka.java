package io.apptik.roxy.example;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import akka.stream.javadsl.AsPublisher;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import io.apptik.roxy.AkkaHubProxy;
import io.apptik.roxy.AkkaProcProxy;
import io.apptik.roxy.Removable;
import io.apptik.roxy.Roxy;
import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;

public class Akka {
    static ActorSystem system;
    static ActorMaterializer materializer;

    public static void main(String... args) {
        //genericProcessorPrxoy();
        genericAkaHubProxy();
    }

    private static void genericAkaHubProxy() {
            final int BUFF = 64;
            system = ActorSystem.create("Example");
            materializer = ActorMaterializer.create(
                    ActorMaterializerSettings.create(system)
                            .withDebugLogging(true)
                            .withAutoFusing(true)
                            .withInputBuffer(BUFF, BUFF)
                    ,
                    system);

        AkkaHubProxy demo = new AkkaHubProxy(materializer);

        demo.asPublisher().subscribe(new Subscriber<Object>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Object o) {
                System.out.println("onNext: " + o);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError");
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
        demo.emit("000");
        demo.pub().runWith(Sink.foreach(System.out::println),materializer);
        demo.emit("AAA");
        demo.addUpstream((Source.range(1,30).map(o -> o.toString())));
        demo.pub().runWith(Sink.foreach(System.out::println),materializer);
        demo.emit("BBB");

        Removable ks = demo.addUpstream((Source.range(201,3000).map(o -> o.toString())));
        new Thread(() -> {
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ks.remove();
            demo.addUpstream((Source.range(101,130).map(o -> o.toString())));
        }).start();


        new Thread(() -> {
            try {
                Thread.sleep(3333);
                demo.complete();
                //demo.addUpstream(Source.range(33, 67).map(o -> o.toString()));
                Thread.sleep(333);
                system.terminate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void genericProcessorPrxoy() {
        final int BUFF = 64;
        system = ActorSystem.create("Example");
        materializer = ActorMaterializer.create(
                ActorMaterializerSettings.create(system)
                        .withDebugLogging(true)
                        .withAutoFusing(true)
                        .withInputBuffer(BUFF, BUFF)
                ,
                system);

        AkkaProcProxy demo = new AkkaProcProxy(PublishProcessor.create(),
                Roxy.TePolicy.WRAP, materializer);

        demo.pub().subscribe(new Subscriber() {

            Subscription s;

            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                s = subscription;
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Object o) {
                System.out.println(o);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError: ");
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });

        demo.addUpstream(Source.range(201, 2000)
                .runWith(Sink.asPublisher(AsPublisher.WITHOUT_FANOUT), materializer));
        new Thread(() -> {
            try {
                Thread.sleep(333);
                demo.emit("AAA");
                demo.emit(5);
                demo.emit(5);
                demo.addUpstream(Flowable.range(1, 100));
                demo.emit(5);
                demo.emit(5);
                demo.emit(5);
                demo.emit(5);
                Thread.sleep(3333);
                demo.complete();
                system.terminate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


        //system.terminate();
    }

}
