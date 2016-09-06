package io.apptik.rxhub.example;


import io.apptik.rxhub.shield.NodeTag;
import rx.Observable;

public interface Testshield {

    @NodeTag(value = "tag1")
    Observable<String> getTag1();

}
