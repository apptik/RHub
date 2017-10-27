package io.apptik.rhub.exampleapp.publishers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter;
import com.github.pwittchen.reactivesensors.library.ReactiveSensors;

import rx.Observable;
import rx.schedulers.Schedulers;

public class Sensors {


    public static Observable<String> startAcc(Context context) {
        return startSensor(context, Sensor.TYPE_ACCELEROMETER, "ACCELEROMETER");
    }

    public static Observable<String> startLight(Context context) {
        return startSensor(context, Sensor.TYPE_GRAVITY, "GRAVITY");
    }

     private static Observable<String> startSensor(Context context, int sensorType, String type) {
       return new ReactiveSensors(context).observeSensor(sensorType)
                .subscribeOn(Schedulers.computation())
                .filter(ReactiveSensorFilter.filterSensorChanged())
                .map(reactiveSensorEvent -> {
                    SensorEvent event = reactiveSensorEvent.getSensorEvent();
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];
                    return String.format("%s: x = %f, y = %f, z = %f",type, x, y, z);
                })
                .doOnNext(sensorReading -> {
                    Log.d("sensor readings", sensorReading);

                });
    }

}
