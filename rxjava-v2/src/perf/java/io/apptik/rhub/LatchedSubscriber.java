/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.apptik.rhub;

import org.openjdk.jmh.infra.Blackhole;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.CountDownLatch;

public class LatchedSubscriber<T> implements Subscriber<T> {

    public final CountDownLatch latch;
    private final Blackhole bh;

    public LatchedSubscriber(Blackhole bh, CountDownLatch latch) {
        this.bh = bh;
        this.latch = latch;
    }

    @Override
    public void onComplete() {
        latch.countDown();
    }

    @Override
    public void onError(Throwable e) {
        latch.countDown();
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T t) {
        //System.out.println(t);
        bh.consume(t);
    }

}
