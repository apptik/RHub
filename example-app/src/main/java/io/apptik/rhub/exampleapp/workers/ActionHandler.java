package io.apptik.rhub.exampleapp.workers;

import android.content.Context;
import android.support.v4.util.Pair;

import io.apptik.rhub.exampleapp.DataShield;
import io.apptik.rhub.exampleapp.publishers.Sensors;
import io.apptik.roxy.Removable;
import rx.functions.Action1;


public class ActionHandler implements Action1<Pair<DataShield.Action, Context>> {

    Removable lightRemovable;
    Removable accRemovable;

    public ActionHandler() {
    }

    @Override
    public void call(Pair<DataShield.Action, Context> action) {
        if (action.first.equals(DataShield.Action.AccOn) &&  accRemovable==null) {
            accRemovable = DataShield.Inst.get().addSensor(Sensors.startAcc(action.second));
        } else if (action.first.equals(DataShield.Action.AccOff)) {
            if(accRemovable!=null) {
                accRemovable.remove();
                accRemovable = null;
            }
        } else if (action.first.equals(DataShield.Action.LightOn) && lightRemovable==null) {
            lightRemovable = DataShield.Inst.get().addSensor(Sensors.startLight(action.second));
        } else if (action.first.equals(DataShield.Action.LightOff)) {
            if(lightRemovable!=null) {
                lightRemovable.remove();
                lightRemovable = null;
            }
        }
    }
}
