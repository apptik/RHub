package io.apptik.rhub.shield;

import com.google.auto.service.AutoService;

import javax.annotation.processing.Processor;

import io.apptik.rhub.RxJava1Hub;
import rx.Observable;

@AutoService(Processor.class)
public class ShieldProcessorRxJava1 extends ShieldProcessor<RxJava1Hub, Observable> {

    @Override
    Class<RxJava1Hub> hubClass() {
        return RxJava1Hub.class;
    }

    @Override
    Class<Observable> pubClass() {
        return Observable.class;
    }
}
