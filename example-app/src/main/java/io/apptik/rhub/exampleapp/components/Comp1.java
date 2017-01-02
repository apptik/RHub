package io.apptik.rhub.exampleapp.components;

import android.util.Log;

import rx.functions.Action1;

public class Comp1 implements Action1<String> {
    @Override
    public void call(String s) {
        Log.d("Comp1", s);
    }
}
