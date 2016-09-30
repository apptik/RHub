package io.apptik.rhub.shield;

import com.google.auto.service.AutoService;

import javax.annotation.processing.Processor;

import io.apptik.rhub.RxJava2ObsHub;
import io.reactivex.Observable;


@AutoService(Processor.class)
public class ShieldProcessorRxJava2 extends ShieldProcessor<RxJava2ObsHub, Observable> {

    @Override
    Class<RxJava2ObsHub> hubClass() {
        return RxJava2ObsHub.class;
    }

    @Override
    Class<Observable> pubClass() {
        return Observable.class;
    }
}
