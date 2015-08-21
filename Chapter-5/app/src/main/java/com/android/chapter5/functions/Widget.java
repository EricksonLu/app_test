package com.android.chapter5.functions;

import android.app.Activity;
import android.widget.Button;

import com.android.chapter5.activity.TimerActivity;
import com.android.chapter5.model.TimerState;

/**
 * Created by lushan on 2015/8/19.
 */
public class Widget extends TimerActivity{




//private static Button start = TimerActivity.start;




    public  void enableButtons() {
//        Log.i(CLASS_NAME, "enableButtons");
        start.setEnabled(!TimerState.timerRunning);
        stop.setEnabled(TimerState.timerRunning);
    }

}
