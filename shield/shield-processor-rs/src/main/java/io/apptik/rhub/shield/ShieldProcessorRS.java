package io.apptik.rhub.shield;

import com.google.auto.service.AutoService;

import org.reactivestreams.Publisher;

import javax.annotation.processing.Processor;

import io.apptik.rhub.RSHub;

@AutoService(Processor.class)
public class ShieldProcessorRS extends ShieldProcessor<RSHub, Publisher> {

    @Override
    Class<RSHub> hubClass() {
        return RSHub.class;
    }

    @Override
    Class<Publisher> pubClass() {
        return Publisher.class;
    }
}
