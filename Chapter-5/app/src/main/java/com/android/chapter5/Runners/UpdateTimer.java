package com.android.chapter5.Runners;

import android.app.Activity;
import android.util.Log;

import com.android.chapter5.activity.OnYourBike;
import com.android.chapter5.model.Settings;
import com.android.chapter5.model.TimerState;

/**
 * Created by lushan on 2015/8/19.
 */
class UpdateTimer implements Runnable {

    Activity activity;

    public UpdateTimer(Activity activity) {
        this.activity = activity;
    }

    /**
     * Updates the counter display and vibrated if needed
     */
    public void run() {
        // Log.d(CLASS_NAME, display);
        Settings settings = ((OnYourBike) getApplication()).getSettings();

        setTimeDisplay();

        if (timer.isRunning()) {
            if (settings.isVibrateOn(activity)) {
                vibrateCheck();
            }
            notifyCheck();
        }

        stayAwakeOrNot();

        if (handler != null) {
            handler.postDelayed(this, UPDATE_EVERY);
        }
    }

    protected void vibrateCheck() {
        long seconds;
        long minutes;

        Log.d(CLASS_NAME, "vibrateCheck");

        timer.elapsedTime();
        seconds = timer.seconds();
        minutes = timer.minutes();

        // NOTE done this way to avoid Array/ArrayList issues
        // NOTE hasVibrator() only on API 11+
        // NOTE try/catch to stop force close on emulator
        // NOTE very easy to get manifest wrong!
        try {
            // NOTE seconds != lastSeconds so it only vibrates once/second
            if (vibrate != null && seconds == 0 && seconds != lastSeconds) {
                long[] once = { 0, 100 };
                long[] twice = { 0, 100, 400, 100 };
                long[] thrice = { 0, 100, 400, 100, 400, 100 };

                // every hour
                if (minutes == 0) {
                    Log.i(CLASS_NAME, "Vibrate 3 times");
                    vibrate.vibrate(thrice, -1);
                }
                // every 15 minutes
                else if (minutes % 15 == 0) {
                    Log.i(CLASS_NAME, "Vibrate 2 time");
                    vibrate.vibrate(twice, -1);
                }
                // every 5 minutes
                else if (minutes % 5 == 0) {
                    Log.i(CLASS_NAME, "Vibrate once");
                    vibrate.vibrate(once, -1);
                }
            }
        } catch (Exception e) {
            Log.w(CLASS_NAME, "Exception: " + e.getMessage());
        }

        lastSeconds = seconds;
    }

    protected void notifyCheck() {
        long seconds;
        long minutes;
        long hours;

        Log.d(CLASS_NAME, "notifyCheck");

        timer.elapsedTime();
        seconds = timer.seconds();
        minutes = timer.minutes();
        hours = timer.hours();

        if (minutes % 15 == 0 && seconds == 0 && seconds != lastSeconds) {
            String title = getResources().getString(R.string.time_title);
            String message = getResources().getString(
                    R.string.time_running_message);

            if (hours == 0 && minutes == 0) {
                message = getResources().getString(
                        R.string.time_start_message);
            }

            message = String.format(message, hours, minutes);

            notify.notify(title, message);
        }

        lastSeconds = seconds;
    }
}
}