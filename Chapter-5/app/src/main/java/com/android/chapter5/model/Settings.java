package com.android.chapter5.model;

import android.app.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.os.Handler;

import com.android.chapter5.R;
import com.android.chapter5.activity.OnYourBike;
import com.android.chapter5.activity.TimerActivity;


/**
 * Created by lushan on 2015/8/11.
 */
public class Settings {
    private static String CLASS_NAME;

    private static String VIBRATE = "vibrate";
    private static String STAYAWAKE = "stayawake";
    protected Handler handler;
    protected boolean vibrateOn;
    protected boolean stayAwake;
    protected CheckVibrate checkVibrate;
    public Settings() {
        CLASS_NAME = getClass().getName();
    }


    public boolean isVibrateOn(Activity activity) {
        Log.d(CLASS_NAME, "isVibrateOn");

        SharedPreferences preferences = activity
                .getPreferences(Activity.MODE_PRIVATE);

        if (preferences.contains(VIBRATE)) {
            vibrateOn = preferences.getBoolean(VIBRATE, false);
        }

        // Log.i(CLASS_NAME, "Vibrate is " + vibrateOn);

        return vibrateOn;
    }


//
//    class Runner  implements Runnable{
//
//        @Override
//        public void run() {
//            Settings settings;
//            settings = ((OnYourBike) ).getSettings();
//            isVibrateOn(SettingsActivity);
//        }
//    }






    public void setVibrate(Activity activity, boolean vibrate) {

//        Log.i(CLASS_NAME, "=====Setting vibrate to===== " + vibrate);
        vibrateOn = vibrate;

        SharedPreferences preferences = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(VIBRATE, vibrate);
        editor.apply(); // rather than commit()


    }

    public Boolean isCaffeinated(Activity activity) {
//        Log.d(CLASS_NAME, "isCaffeinated");

        handler = new Handler();
        CheckCaffeinated checkcaffeinated = new CheckCaffeinated(activity);
        handler.postDelayed(checkcaffeinated, 1);
//        Log.i(CLASS_NAME, "=====checkcaffeinated is =====" + vibrateOn);
//        Log.i(CLASS_NAME, "isCaffeinated ---> " + Thread.currentThread().getId());

//        Log.i(CLASS_NAME, "Stay awake is " + stayAwake);

        return stayAwake;
    }

    public void setCaffeinated(Activity activity, boolean stayawake) {
//        Log.d(CLASS_NAME, "setCaffeinated");

//        Log.i(CLASS_NAME, "Setting stay awake to " + stayawake);

        stayAwake = stayawake;

        SharedPreferences preferences = activity
                .getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(STAYAWAKE, stayAwake);
        editor.apply(); // rather than commit()
    }

    private class CheckVibrate implements Runnable {
        Activity activity;

        public CheckVibrate(Activity activity) {

            this.activity = activity;
        }

        @Override
        public void run() {
            SharedPreferences preferences = activity.getPreferences(Activity.MODE_PRIVATE);
            if (preferences.contains(VIBRATE)) {
                vibrateOn = preferences.getBoolean(VIBRATE, false);
                Log.i(CLASS_NAME, "run() ---> " + Thread.currentThread().getId());
            }
        }
    }

    private class CheckCaffeinated implements Runnable {
        Activity activity;

        public CheckCaffeinated(Activity activity) {

            this.activity = activity;
        }

        @Override
        public void run() {
            SharedPreferences preferences = activity
                    .getPreferences(Activity.MODE_PRIVATE);

            if (preferences.contains(STAYAWAKE)) {
                stayAwake = preferences.getBoolean(STAYAWAKE, false);
            }
            }
        }
    public boolean isVibrateOnThread(final Activity activity) {

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.i(CLASS_NAME, "isVibrateOn ---> " + Thread.currentThread().getId());
                SharedPreferences preferences = activity.getPreferences(Activity.MODE_PRIVATE);
                if (preferences.contains(VIBRATE)) {
                    vibrateOn = preferences.getBoolean(VIBRATE, false);
                }
            }};

        Thread thread = new Thread(r);
        thread.start();
//        handler = new Handler();
//        checkVibrate = new CheckVibrate(activity);
//        handler.postDelayed(checkVibrate, 1);
////        Log.i(CLASS_NAME, "=====Vibrate is =====" + vibrateOn);
        return vibrateOn;
    }






}





