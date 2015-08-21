package com.android.chapter5.model;

import android.util.Log;

/**
 * Created by lushan on 2015/8/11.
 */
public class TimerState {
    public static String CLASS_NAME;
    public static boolean timerRunning;
    public static long startedAt;
    public static long lastStopped;

    public TimerState(){
        CLASS_NAME=getClass().getName();
    }
    public void reset(){
        timerRunning = false;
        startedAt = System.currentTimeMillis();

        }
    public void start(){
        timerRunning = true;
        startedAt = System.currentTimeMillis();
    }
    public void stop(){
        timerRunning = false;
        lastStopped = System.currentTimeMillis();

    }
    public boolean isRunning()  {
        return  timerRunning;
    }
    public long elapsedTime()   {
        long timeNow;
        if (isRunning())    {
            timeNow = System.currentTimeMillis();
        } else {
            timeNow = lastStopped;
        }
        return timeNow - startedAt;
    }
    public String display() {
        String display;
        long timeNow;
        long diff;
        long seconds;
        long minutes;
        long hours;

//        Log.d(CLASS_NAME, "setTimeDisplay");


        diff = elapsedTime();

        // no negative time
        if (diff < 0) {
            diff = 0;
        }

        seconds = diff / 1000;
        minutes = seconds / 60;
        hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        display = String.format("%d", hours) + ":"
                + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds);

//        Log.i(CLASS_NAME, "Time is " + display);

        return display;
    }







}
