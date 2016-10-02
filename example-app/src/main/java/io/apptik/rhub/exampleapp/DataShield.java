package io.apptik.rhub.exampleapp;


import io.apptik.rhub.DefaultRxJava1Hub;
import io.apptik.rhub.RxJava1Hub;
import io.apptik.rhub.shield.ProxyTag;
import io.apptik.rhub.shield.ShieldMakerRxJava1;
import rx.Observable;

public interface DataShield {

    @ProxyTag(value = "Sensor")
    void addSensor(Observable<String> observable);

    @ProxyTag(value = "Sensor")
    Observable<String> sensorData();

    @ProxyTag(value = "Action")
    void addActionEvent(Observable<Action> observable);

    @ProxyTag(value = "Action")
    Observable<Action> actionEvents();


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
