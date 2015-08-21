package com.android.chapter5.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.PowerManager;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.android.chapter5.R;
import com.android.chapter5.functions.Timer;
import com.android.chapter5.functions.Widget;
import com.android.chapter5.helpers.Notify;
import com.android.chapter5.model.MyHandler;
import com.android.chapter5.model.Settings;
import com.android.chapter5.model.TimerState;

/**
 android.util.Log常用的方法有以下5个：Log.v() Log.d() Log.i() Log.w() 以及 Log.e() 。根据首字母对应VERBOSE，DEBUG,INFO, WARN，ERROR

 1、Log.v 的调试颜色为黑色的，任何消息都会输出，这里的v代表verbose啰嗦的意思，平时使用就是Log.v("","");

 2、Log.d的输出颜色是蓝色的，仅输出debug调试的意思，但他会输出上层的信息，过滤起来可以通过DDMS的Logcat标签来选择.

 3、Log.i的输出为绿色，一般提示性的消息information，它不会输出Log.v和Log.d的信息，但会显示i、w和e的信息

 4、Log.w的意思为橙色，可以看作为warning警告，一般需要我们注意优化Android代码，同时选择它后还会输出Log.e的信息。

 5、Log.e为红色，可以想到error错误，这里仅显示红色的错误信息，这些错误就需要我们认真的分析，查看栈的信息了。
 */

public class TimerActivity extends Activity {

    private static final boolean DEVELOPER_MODE = true;
    private static String CLASS_NAME;
    protected TimerState timer;


    public TimerActivity() {
        CLASS_NAME = getClass().getName();
        timer = new TimerState();

    }

    //private static final String TAG = TimerActivity.class.getName();
    private static long UPDATE_EVERY = 200;
    protected TextView counter;
    protected static Button start;
    protected static Button stop;
    protected long lastSeconds;//确保如果vibratecheck在1秒内被多次调用，如果seconds==lastseconds这个条件使得设备不会进入震动状态
    protected Vibrator vibrate;
    protected Handler handler;






    public static Handler UIHandler;
    public static HandlerThread bHandlerthread;


    private Notify notify;

    private Timer timer_functions;
    private Widget widget_fuctions;
    protected UpdateTimer updateTimer;










    /**
     * Bundle 是key-map格式的，一种类型。savedInstanceState是用来恢复app状态的变量。
     * http://developer.android.com/intl/zh-CN/reference/android/app/Activity.html#onCreate(android.os.Bundle)
     */



    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Log.d("2","2222");*/
        /*Log.d(TAG,"FFFFFFFFFFFFFFFFFFFFFFFF");这句和前面那句是一样的效果,见前面className定义*/
//        Log.d(CLASS_NAME, "onCreate");
        // note findViewById must be called after setContentView or we'll get an
        // RTE(run time error)

        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        setContentView(R.layout.activity_timer);
        counter = (TextView) findViewById(R.id.Tv_timer);
        start = (Button) findViewById(R.id.start_button);
        stop = (Button) findViewById(R.id.stop_button);

//        HandlerThread bHandlerthread = new HandlerThread("111");
//        bHandlerthread.start();
//        MyHandler handler1 = new MyHandler(bHandlerthread.getLooper());
//
//        handler1.post(uodatetimer);
//         UIHandler =new Handler();


        vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (vibrate == null) {
            Log.w(CLASS_NAME, "No vibration service exists.");
        }

        timer.reset();

        timer_functions.stayAwakeOrNot();

        // NOTE Needs to be created here not in constructor or you'll get an RTE

        notify = new Notify(this);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


/**
 *onCreateOptionsMenu仅被调用一次用来设置activity的菜单。
 */
//        Log.d(CLASS_NAME, "Showing menu................");
        /**
         * 通俗的说,inflate就相当于将一个xml中定义的布局找出来.
         * 因为在一个Activity里如果直接用findViewById()的话,对应的是setConentView()的那个layout里的组件.
         * 因此如果你的Activity里如果用到别的layout,比如对话框上的layout,你还要设置对话框上的layout里的组件(像图片ImageView,文字TextView)上的内容,你就必须用inflate()先将对话框上的layout找出来,然后再用这个layout对象去找到它上面的组件,如:
         * Viewview=View.inflate(this,R.layout.dialog_layout,null);
         * TextViewdialogTV=(TextView)view.findViewById(R.id.dialog_tv);
         * 如果组件R.id.dialog_tv是对话框上的组件,而你直接用this.findViewById(R.id.dialog_tv)肯定会报错.
         * 三种方式可以生成LayoutInflater：
         * LayoutInflaterinflater=LayoutInflater.from(this);
         * LayoutInflaterinflater=getLayoutInflater();
         * LayoutInflaterinflater=(LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
         * 然后调用inflate方法将xml布局文件转成View
         * publicView inflate(intresource,ViewGrouproot,booleanattachToRoot)
         * 在View类中，也有inflate方法
         * public static View inflate(Contextcontext,intresource,ViewGrouproot)
         */
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /**
             * menusettings在是在mennu_activity_main.xml下的item控件
             */
            case R.id.menu_settings:
                clickedSettings(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Acivity加载函数顺序：oncreate--oncreate里面的函数--onstart--onstart里面的函数--然后是onstart后面的函数，activity里面都加载完毕后，程序界面显示，并可以运行
     */
    public void clickedStart(View view) {
//        Log.i(CLASS_NAME, "clikedStart ---> " + Thread.currentThread().getId());
        timer.start();

        widget_fuctions.enableButtons();
        handler = new Handler();
        updateTimer = new UpdateTimer(this);
        handler.postDelayed(updateTimer, UPDATE_EVERY);

    }




    public void clickedStop(View view) {
//        Log.d(CLASS_NAME, "clickedStop");
        TimerState.timerRunning = false;
        TimerState.lastStopped = System.currentTimeMillis();

        Widget.enableButtons();
        handler.removeCallbacks(updateTimer);
        updateTimer = null;
        handler = null;
    }

    public void clickedSettings(View view) {
//        Log.d(CLASS_NAME, "clickedSettings");
//        Log.d(CLASS_NAME, "clickedSettings");

        Intent settings = new Intent(getApplicationContext(),
                SettingsActivity.class);

        startActivity(settings);
    }













    UpdateTimer uodatetimer = new UpdateTimer(this);

    public class UpdateTimer implements Runnable {

        Activity activity;

        public UpdateTimer(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {

            Settings settings,settings1;
            settings = ((OnYourBike) getApplication()).getSettings();


            UIHandler.post(rsetTimeDisplay);
//            setTimeDisplay();
            if (TimerState.timerRunning && settings.isVibrateOn(activity)) {
                vibrateCheck();
            }

            timer_functions.stayAwakeOrNot();

            if (handler != null) {
                handler.postDelayed(this, UPDATE_EVERY);
            }
        }
    }




    protected void vibrateCheck() {


        long diff = timer.elapsedTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;

//        Log.d(CLASS_NAME, "vibrateCheck");

        seconds = seconds % 60;
        minutes = minutes % 60;

        if (vibrate != null && seconds == 0 && seconds != lastSeconds) {
            long[] once = {0, 100};
            long[] twice = {0, 100, 400, 100};
            long[] thride = {0, 100, 400, 100, 400, 100};

            // every hour
            if (minutes == 0) {
//                Log.i(CLASS_NAME, "Vibrate 3 times");
                vibrate.vibrate(thride, -1);

            }
            // every 15 minutes
            else if (minutes % 15 == 0) {
//                Log.i(CLASS_NAME, "Vibrate 2 time");
                vibrate.vibrate(twice, -1);
            }
            // every 5 minutes
            else if (minutes % 5 == 0) {
//                Log.i(CLASS_NAME, "Vibrate once");
                vibrate.vibrate(once, -1);
            }
        }

        lastSeconds = seconds;
    }







    @Override
    public void onStart() {

        super.onStart();
//        Log.i(CLASS_NAME, "onStart");
        Log.i(CLASS_NAME, "clikedStart ---> " + Thread.currentThread().getId());
        if (TimerState.timerRunning) {
            handler = new Handler();
            updateTimer = new UpdateTimer(this);
            handler.postDelayed(updateTimer, UPDATE_EVERY);
            Log.d(CLASS_NAME, "onStart ---> " + Thread.currentThread().getId());
        }
    }




    RsetTimeDisplay rsetTimeDisplay = new RsetTimeDisplay();
    class RsetTimeDisplay implements Runnable{

        @Override
        public void run() {
            setTimeDisplay();
        }
    }
    public void setTimeDisplay() {
//        Log.i(CLASS_NAME, "setTimeDisplay");

        counter.setText(timer.display());
    }

















    @Override
    public void onResume() {
        super.onResume();
//        Log.i(CLASS_NAME, "onResume");

        enableButtons();
        setTimeDisplay();
    }

    /**
     * Called when the Activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
//        Log.i(CLASS_NAME, "onPause");
    }

    /**
     * Called when the Activity is stopped.
     */
    @Override
    public void onStop() {
        super.onStop();
//        Log.i(CLASS_NAME, "onStop");

        Settings settings = ((OnYourBike) getApplication()).getSettings();

        if (TimerState.timerRunning) {
            handler.removeCallbacks(updateTimer);
            updateTimer = null;
            handler = null;
        }

        if (settings.isCaffeinated(this)) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK, CLASS_NAME);

            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
    }

    /**
     * Called when the Activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d(CLASS_NAME, "onDestroy");
    }


    /**
     * Called when the Activity is restarted.
     */
    @Override
    public void onRestart() {
        super.onRestart();
//        Log.d(CLASS_NAME, "onRestart");
    }
}




