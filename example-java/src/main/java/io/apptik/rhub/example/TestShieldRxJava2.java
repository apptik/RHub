package io.apptik.rhub.example;


import io.apptik.rhub.shield.ProxyTag;
import io.reactivex.Observable;

public interface TestShieldRxJava2 {

    @ProxyTag(value = "tag1")
    Observable<String> getTag1String();

    @ProxyTag(value = "tag1")
    Observable<Integer> getTag1Int();

    @ProxyTag(value = "tag1")
    Observable<?> getTag1Any();

    @ProxyTag(value = "tag1")
    Observable getTag1All();

    @ProxyTag(value = "tag1")
    void addTag1Provider(Observable p1);

    @ProxyTag(value = "tag1")
    void addTag1IntProvider(Observable<Integer> p1);

}
