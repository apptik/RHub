package io.apptik.rhub.example;


import io.apptik.rhub.shield.ProxyTag;
import io.reactivex.Observable;

public interface TestShieldRxJava2 {

    @ProxyTag("tag1")
    Observable<String> getTag1String();

    @ProxyTag("tag1")
    Observable<Integer> getTag1Int();

    @ProxyTag("tag1")
    Observable<?> getTag1Any();

    @ProxyTag("tag1")
    Observable getTag1All();

    @ProxyTag("tag1")
    void addTag1Provider(Observable p1);

    @ProxyTag("tag1")
    void addTag1IntProvider(Observable<Integer> p1);

}
