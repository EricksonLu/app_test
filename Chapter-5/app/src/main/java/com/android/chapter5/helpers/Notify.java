package com.android.chapter5.helpers;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.chapter5.R;

/**
 * Created by lushan on 2015/8/12.
 */
public class Notify {
    private final NotificationManager manager;
    private final Context context;
    private static String CLASS_NAME;
    public  int smallIcon = R.drawable.ic_launcher;
    private static final int MESSAGE_ID =  1;
    public Notify(Activity activity) {
        CLASS_NAME = getClass().getName();
        manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        context = activity.getApplicationContext();
    }

    private Notification create(String title, String message, long when) {
//        Log.d(CLASS_NAME, "create()");

        // NOTE must supply icon otherwise notification dosn't show

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title).setContentText(message).setWhen(when)
                .setSmallIcon(smallIcon).build();

        return notification;
    }


    public void notify(String title, String message, long when) {
//        Log.d(CLASS_NAME, "notify()");

        Notification notification = create(title, message, when);

        manager.notify(MESSAGE_ID, notification);
    }

    public void notify(String title, String message) {
//        Log.d(CLASS_NAME, "notify()");

        Notification notification = create(title, message,
                System.currentTimeMillis());

        manager.notify(MESSAGE_ID, notification);
    }
}