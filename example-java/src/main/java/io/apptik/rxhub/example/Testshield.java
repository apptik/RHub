package io.apptik.rxhub.example;


import io.apptik.rxhub.shield.NodeTag;
import rx.Observable;

public interface TestShield {

    @NodeTag(value = "tag1")
    Observable<String> getTag1String();

    @NodeTag(value = "tag1")
    Observable<Integer> getTag1Int();

    @NodeTag(value = "tag1")
    Observable<?> getTag1Any();

    @NodeTag(value = "tag1")
    Observable getTag1All();

    @NodeTag(value = "tag1")
    void addTag1Provider(Observable p1);

    @NodeTag(value = "tag1")
    void addTag1IntProvider(Observable<Integer> p1);

}
