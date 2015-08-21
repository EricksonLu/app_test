package com.android.chapter5.helpers;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by lushan on 2015/8/12.
 */
public class Toaster {
    private static int TOAST_DURATION = Toast.LENGTH_SHORT;
    private final Context context;
    private static String CLASS_NAME;
    public Toaster(Context context) {
        this.context = context;
        CLASS_NAME = getClass().getName();
    }
    public void make (int resource) {
//        Log.i(CLASS_NAME, "===make=== ");
        Toast toast = Toast.makeText(context,resource,TOAST_DURATION);
        toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
        toast.show();
    }

}
