package io.apptik.rhub.example;


import org.reactivestreams.Publisher;

import io.apptik.rhub.shield.ProxyTag;
import io.apptik.roxy.Removable;

public interface TestShieldRS {

    @ProxyTag("tag1")
    Publisher<String> getTag1String();

    @ProxyTag("tag1")
    Publisher<Integer> getTag1Int();

    @ProxyTag("tag1")
    Publisher<?> getTag1Any();

    @ProxyTag("tag1")
    Publisher getTag1All();

    @ProxyTag("tag1")
    void addTag1Provider(Publisher p1);

    @ProxyTag("tag1")
    void addTag1IntProvider(Publisher<Integer> p1);

    @ProxyTag("tag1")
    Removable addTag1IntProvider2(Publisher<Integer> p1);


}
