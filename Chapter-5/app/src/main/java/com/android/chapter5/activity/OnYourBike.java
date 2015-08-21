package com.android.chapter5.activity;

import android.app.Application;

import com.android.chapter5.model.Settings;


/**
 * Created by lushan on 2015/8/11.
 */
public class OnYourBike extends Application {
    protected static Settings settings;
    public static Settings getSettings() {
        if (settings == null) {
            settings = new Settings();
        }

        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
