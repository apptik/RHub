package io.apptik.rhub.exampleapp.components;

import android.widget.TextView;

import rx.functions.Action1;

public class Comp2 implements Action1<String> {

    final TextView tv;

    public Comp2(TextView tv) {
        this.tv = tv;
    }

    @Override
    public void call(String s) {
        tv.setText("Comp2: " + s);
    }
}
