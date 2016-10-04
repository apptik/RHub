package io.apptik.rhub.exampleapp;


import android.content.Context;
import android.support.v4.util.Pair;

import io.apptik.rhub.DefaultRxJava1Hub;
import io.apptik.rhub.RxJava1Hub;
import io.apptik.rhub.shield.ProxyTag;
import io.apptik.rhub.shield.ShieldMakerRxJava1;
import io.apptik.roxy.Removable;
import rx.Observable;

public interface DataShield {

    @ProxyTag("Sensor")
    Removable addSensor(Observable<String> observable);

    @ProxyTag("Sensor")
    Observable<String> sensorData();

    @ProxyTag("Action")
    Removable addActionEvent(Observable<Pair<Action, Context>> observable);

    @ProxyTag("Action")
    Observable<Pair<Action, Context>> actionEvents();

    enum Action {
        LightOn,
        LightOff,
        AccOn,
        AccOff
    }

    class Inst {
        private static DataShield inst;
        private static RxJava1Hub hub;
        public static DataShield get() {
            if(inst==null) {
                hub = new DefaultRxJava1Hub() {
                    @Override
                    public RxJava1ProxyType getProxyType(Object tag) {
                        if(tag.equals("Action")) {
                            return RxJava1ProxyType.PublishRelayProxy;
                        }
                        return super.getProxyType(tag);
                    }
                };
                inst = ShieldMakerRxJava1.make(DataShield.class, hub);
            }
            return inst;
        }
        public static RxJava1Hub getHub() {
            return hub;
        }
    }


}
