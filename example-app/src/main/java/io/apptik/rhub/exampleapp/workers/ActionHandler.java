package io.apptik.rhub.exampleapp.workers;

import android.content.Context;

import io.apptik.rhub.exampleapp.DataShield;
import io.apptik.rhub.exampleapp.publishers.Sensors;
import rx.Observable;
import rx.functions.Action1;


public class ActionHandler implements Action1<DataShield.Action> {

    Observable<String> lightPublisher;
    Observable<String> accPublisher;
    final Context context;

    public ActionHandler(Context context) {
        this.context = context;
    }

    @Override
    public void call(DataShield.Action action) {
        if (action.equals(DataShield.Action.AccOn)) {
            accPublisher = Sensors.startAcc(context);
            DataShield.Inst.get().addSensor(accPublisher);
        } else if (action.equals(DataShield.Action.AccOff)) {
            if(DataShield.Inst.getHub()!=null && accPublisher!=null) {
                DataShield.Inst.getHub().removeUpstream("Sensor", accPublisher);
            }
        } else if (action.equals(DataShield.Action.LightOn)) {
            lightPublisher = Sensors.startLight(context);
            DataShield.Inst.get().addSensor(lightPublisher);
        } else if (action.equals(DataShield.Action.LightOff)) {
            if(DataShield.Inst.getHub()!=null && lightPublisher!=null) {
                DataShield.Inst.getHub().removeUpstream("Sensor", lightPublisher);
            }
        }
    }
}
