package io.apptik.rhub.exampleapp.workers;

import android.util.Log;

import rx.functions.Action1;

public class Worker1 implements Action1<String> {
    @Override
    public void call(String s) {
        Log.d("Worker1", s);
    }
}
