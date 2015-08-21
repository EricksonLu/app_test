package HandlerThread;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.widget.Toast;

import com.example.lushan.myapplication2_message.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by lushan on 2015/8/17.
 */
public class Activity_HandlerThread extends Activity {
    private Handler mUiHandler = new Handler();
    private MyWorkerThread mWorkerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("TT", "----Create " + Thread.currentThread().getId());
//        mWorkerHandler is tied to MyWorkerThread_message by specifying its Looper

        mWorkerThread = new MyWorkerThread("myWorkerThread");
        Runnable task = new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 4; i++) {
                    try {
                        Log.i("TT", "----task "+Thread.currentThread().getId());
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i == 2) {
                        mUiHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(Activity_HandlerThread.this,
                                        "I am at the middle of background task",
                                        Toast.LENGTH_LONG)
                                        .show();
                                Log.i("TT", "----task i >2 " + Thread.currentThread().getId());
                            }
                        });
                    }
                }
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Activity_HandlerThread.this,
                                "Background task is completed",
                                Toast.LENGTH_LONG)
                                .show();
                        Log.i("TT", "----task completed " + Thread.currentThread().getId());
                    }
                });
            }
        };
        mWorkerThread.start();
        mWorkerThread.prepareHandler();
        mWorkerThread.postTask(task);
        Log.i("TT", "----last " + Thread.currentThread().getId());
//        mWorkerThread.postTask(task);
    }

    @Override
    protected void onDestroy() {
        mWorkerThread.quit();
        super.onDestroy();
    }
}

