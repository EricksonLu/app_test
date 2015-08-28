package com.bignerdranch.android.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
//这里将学习如何监听系统发送的broadcast intent，以及如何在运行的应用中动态的发送与接收它们。首先来监听一个宣告设备已启动完毕的broadcast，然后学习发送和接受我们自己的broadcast intent
//设备重启后，那些持续运行的应用通常也需要重启。通过监听具有BOOT_COMPLETED操作的broadcast intent可得知设备是否完成启动

//在配置文件中登记的broadcast receiver来接收intent，但这里该方法行不通，所以，需要在PhotoGalleryFragment存在的时候接收intent，而在配置文件中声明的独立receiver很难做到，
//因为receiver在不断接收intent的同时，还需要另一种方法来知晓PhotoGalleryFragment的存在状态，使用动态broadcast receiver可解决这个问题。
//动态broadcast receiver是在代码中，而不是在配置文件中完成登记声明的。

//Broadcast Receiver是接受intent的组件，必须在系统登记后才能使用，在manifest配置文件中登记，并配置实用权限
//在配置文件中完成声明后，即使应用当前并未运行，只有匹配的broadcast intent发来，receiver就会接收，一收到intent，receiver的onReceive就开始运行
//然后receiver被销毁。所以，在这个应用里，系统重启后，定时运行的定时器也需要进行重置，通过这个broadcast 启动定时器
//receiver需要知道定时器的启停状态，，



//这个类一开始的时候就会被加载用来启动定时器服务，别无他用,因为在manifest中声明了
public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());
//        设备重启后启动定时器
//        从SharedPreferences获取定时器状态后，启动定时器
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isOn = prefs.getBoolean(PollService.PREF_IS_ALARM_ON, false);
//        注意这个setServiceAlarm的调用的参数，在fragment中使用getActivity，在StartupReceiver中使用onReceive中的context。
        PollService.setServiceAlarm(context, isOn);

    }
}
