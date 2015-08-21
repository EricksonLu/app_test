package com.android.chapter5.activity;

import android.os.HandlerThread;

/**
 * Created by lushan on 2015/8/18.
 */
public class MHandlerThread extends HandlerThread {


    public MHandlerThread(String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
    }
}
