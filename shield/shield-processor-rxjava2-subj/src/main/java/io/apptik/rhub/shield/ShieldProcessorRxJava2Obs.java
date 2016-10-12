package io.apptik.rhub.shield;

import com.google.auto.service.AutoService;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Processor;

import io.apptik.rhub.RxJava2ObsHub;
import io.reactivex.Observable;


/**
 * Annotation Processor that knows about RxJava 2.x {@link Observable} types
 */
@AutoService(Processor.class)
public final class ShieldProcessorRxJava2Obs extends ShieldProcessor<RxJava2ObsHub, Observable> {

    @Override
    Class<RxJava2ObsHub> hubClass() {
        return RxJava2ObsHub.class;
    }

    @Override
    Set<Class<? extends Observable>> pubClass() {
        Set<Class<? extends Observable>> res = new HashSet<>();
        res.add(Observable.class);
        return res;
    }


}
