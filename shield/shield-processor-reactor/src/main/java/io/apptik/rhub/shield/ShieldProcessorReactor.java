package io.apptik.rhub.shield;

import com.google.auto.service.AutoService;

import org.reactivestreams.Publisher;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Processor;

import io.apptik.rhub.AbstractReactorHub;
import reactor.core.publisher.Flux;

@AutoService(Processor.class)
public class ShieldProcessorReactor extends ShieldProcessor<AbstractReactorHub, Publisher> {

    @Override
    Class<AbstractReactorHub> hubClass() {
        return AbstractReactorHub.class;
    }

    @Override
    Set<Class<? extends Publisher>> pubClass() {
        Set<Class<? extends Publisher>> res = new HashSet<>();
        res.add(Publisher.class);
        res.add(Flux.class);
        return res;
    }


}
