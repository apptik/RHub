package io.apptik.rhub.exampleapp.components;

import android.widget.TextView;

import rx.functions.Action1;

public class Worker2 implements Action1<String> {

    final TextView tv;

    public Worker2(TextView tv) {
        this.tv = tv;
    }

    @Override
    public void call(String s) {
        tv.setText("Worker2: " + s);
    }
}
