package com.android.chapter5.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.android.chapter5.R;
import com.android.chapter5.helpers.Toaster;
import com.android.chapter5.model.Settings;

/**
 * Created by lushan on 2015/8/11.
 */
public class SettingsActivity extends Activity {
    private static String CLASS_NAME;

    private CheckBox vibrate;
    private CheckBox stayAwake;
    public SettingsActivity() {
        CLASS_NAME = getClass().getName();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TT", "----onCreate " + Thread.currentThread().getId());
//        Log.d(CLASS_NAME, "onCreate");
        setContentView(R.layout.activity_settings);

        vibrate = (CheckBox) findViewById(R.id.vibrate_checkbox);
        stayAwake = (CheckBox) findViewById(R.id.awake_checkbox);

        Settings settings = ((OnYourBike) getApplication()).getSettings();

        vibrate.setChecked(settings.isVibrateOn(this));
        stayAwake.setChecked(settings.isCaffeinated(this));

        setupAcitonBar();
    }

    /**打开settings画面后再操作栏图标左面的小于号，可以返回主画面。这个是第二个返回画面的方式*/
    private void setupAcitonBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.i("TT", "----queue " + Thread.currentThread().getId());
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                gotoHome();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }
    private void gotoHome () {
//        Log.i(CLASS_NAME, "gotoHome ");
        Intent timer = new Intent(getApplicationContext(),TimerActivity.class);
        timer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(timer);
    }
    public void onStop() {
        super.onStop();
//        Log.d(CLASS_NAME, "onStop");

        Settings settings = ((OnYourBike) getApplication()).getSettings();

//        Log.i(CLASS_NAME, "Saving settings");
        settings.setVibrate(this, vibrate.isChecked());
        settings.setCaffeinated(this, stayAwake.isChecked());
    }
    public void vibrateChanged (View view) {
        Toaster toast = new Toaster(getApplicationContext());

        if (vibrate.isChecked()) {
            toast.make(R.string.vibrate_on);
        } else {
            toast.make(R.string.vibrate_off);
        }
    }
    public void goBack (View view) {
        finish();
    }
}
