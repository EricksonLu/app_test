package com.android.chapter5.functions;

import android.app.Activity;
import android.util.Log;
import android.view.WindowManager;

import com.android.chapter5.activity.OnYourBike;
import com.android.chapter5.activity.TimerActivity;
import com.android.chapter5.model.Settings;

/**
 * Created by lushan on 2015/8/19.
 */
public class Timer extends TimerActivity{

    Activity activity = new TimerActivity();
    public void setTimeDisplay() {
//        Log.i(CLASS_NAME, "setTimeDisplay");

        counter.setText(timer.display());

    }
    /**
     * Keep the screen on depending on the stay awake setting
     */
    public  void stayAwakeOrNot() {
//        Log.d(CLASS_NAME, "stayAwakeOrNot");

        Settings settings = ((OnYourBike) getApplication()).getSettings();

        if (settings.isCaffeinated(activity)) {
            // Log.i(CLASS_NAME, "Staying awake");
            getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            // Log.i(CLASS_NAME, "Not staying awake");
            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
