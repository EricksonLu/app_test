package com.androiddevbook.onyourbike.chapter5.activities;

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

import com.androiddevbook.onyourbike.chapter5.OnYourBike;
import com.androiddevbook.onyourbike.chapter5.R;
import com.androiddevbook.onyourbike.chapter5.helpers.Toaster;
import com.androiddevbook.onyourbike.chapter5.model.Settings;

/**
 * SettingsActivity
 * 
 * Setting Activity for the "On Your Bike" application.
 * 
 * Copyright [2013] Pearson Education, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @author androiddevbook.com
 * @version 1.0
 */
public class SettingsActivity extends Activity {

    private static String CLASS_NAME;

    private CheckBox vibrate;
    private CheckBox stayAwake;

    public SettingsActivity() {
        CLASS_NAME = getClass().getName();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)

    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(CLASS_NAME, "onCreate");
        setContentView(R.layout.activity_settings);

        vibrate = (CheckBox) findViewById(R.id.vibrate_checkbox);
        stayAwake = (CheckBox) findViewById(R.id.awake_checkbox);

        Settings settings = ((OnYourBike) getApplication()).getSettings();

        vibrate.setChecked(settings.isVibrateOn(this));
        stayAwake.setChecked(settings.isCaffeinated(this));

        setupActionBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(CLASS_NAME, "onStop");

        Settings settings = ((OnYourBike) getApplication()).getSettings();

        Log.i(CLASS_NAME, "Saving settings");
        settings.setVibrate(this, vibrate.isChecked());
        settings.setCaffeinated(this, stayAwake.isChecked());
    }

    private void gotoHome() {
        Log.d(CLASS_NAME, "gotoHome");

        Intent timer = new Intent(this, TimerActivity.class);
        timer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(timer);
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

    /*
     * Called when the vibrate CheckBox is checked or unchecked.
     */
    public void vibrateChanged(View view) {
        Toaster toast = new Toaster(this);

        if (vibrate.isChecked()) {
            toast.make(R.string.vibrate_on);
        } else {
            toast.make(R.string.vibrate_off);
        }
    }

    /*
     * Called when the stay awake CheckBox is checked or unchecked.
     */
    // NOTE must get callback name right or RTE - no compilation error if you
    // get wrong
    public void awakeChanged(View view) {
        Toaster toast = new Toaster(this);

        if (stayAwake.isChecked()) {
            toast.make(R.string.awake_on);
        } else {
            toast.make(R.string.awake_off);
        }
    }

    public void goBack(View view) {
        finish();
    }
}
