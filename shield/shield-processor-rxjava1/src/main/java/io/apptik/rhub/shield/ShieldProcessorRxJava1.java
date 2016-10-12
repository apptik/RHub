package io.apptik.rhub.shield;

import com.google.auto.service.AutoService;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Processor;

import io.apptik.rhub.RxJava1Hub;
import rx.Observable;

/**
 * Annotation Processor that knows about RxJava 1.x types
 */
@AutoService(Processor.class)
public final class ShieldProcessorRxJava1 extends ShieldProcessor<RxJava1Hub, Observable> {

    @Override
    Class<RxJava1Hub> hubClass() {
        return RxJava1Hub.class;
    }

    @Override
    Set<Class<? extends Observable>> pubClass() {
        Set<Class<? extends Observable>> res = new HashSet<>();
        res.add(Observable.class);
        return res;
    }
}
