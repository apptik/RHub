package io.apptik.rhub.example;


import org.reactivestreams.Publisher;

import io.apptik.rhub.shield.ProxyTag;

public interface TestShieldRS {

    @ProxyTag(value = "tag1")
    Publisher<String> getTag1String();

    @ProxyTag(value = "tag1")
    Publisher<Integer> getTag1Int();

    @ProxyTag(value = "tag1")
    Publisher<?> getTag1Any();

    @ProxyTag(value = "tag1")
    Publisher getTag1All();

    @ProxyTag(value = "tag1")
    void addTag1Provider(Publisher p1);

    @ProxyTag(value = "tag1")
    void addTag1IntProvider(Publisher<Integer> p1);

}
