package io.apptik.rhub.exampleapp.components;

import android.content.Context;
import android.support.v4.util.Pair;

import io.apptik.rhub.exampleapp.Shield;
import io.apptik.rhub.exampleapp.publishers.Sensors;
import io.apptik.roxy.Removable;
import rx.functions.Action1;


public class ActionHandler implements Action1<Pair<Shield.Action, Context>> {

    private Removable lightRemovable;
    private Removable accRemovable;

    @Override
    public void call(Pair<Shield.Action, Context> action) {
        if (action.first.equals(Shield.Action.AccOn) &&  accRemovable==null) {
            accRemovable = Shield.Inst.get().addSensor(Sensors.startAcc(action.second));
        } else if (action.first.equals(Shield.Action.AccOff)) {
            if(accRemovable!=null) {
                accRemovable.remove();
                accRemovable = null;
            }
        } else if (action.first.equals(Shield.Action.LightOn) && lightRemovable==null) {
            lightRemovable = Shield.Inst.get().addSensor(Sensors.startLight(action.second));
        } else if (action.first.equals(Shield.Action.LightOff)) {
            if(lightRemovable!=null) {
                lightRemovable.remove();
                lightRemovable = null;
            }
        }
    }
}
