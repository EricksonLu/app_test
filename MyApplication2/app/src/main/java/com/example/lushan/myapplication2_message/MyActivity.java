package com.example.lushan.myapplication2_message;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;

import HandlerThread.MyWorkerThread;

/**
 * Created by lushan on 2015/8/17.
 */
public class MyActivity extends Activity implements MyWorkerThread_message.Callback{
    private static final boolean DEVELOPER_MODE = true ;
    private static boolean isVisible;
    public static final int LEFT_SIDE = 0;
    public static final int RIGHT_SIDE = 1;
    private LinearLayout mLeftSideLayout;
    private LinearLayout mRightSideLayout;
    private MyWorkerThread_message mWorkerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        setContentView(R.layout.layout);
        isVisible = true;
        mLeftSideLayout = (LinearLayout) findViewById(R.id.leftSideLayout);
        mRightSideLayout = (LinearLayout) findViewById(R.id.rightSideLayout);
        String[] urls = new String[]{"http://img2.imgtn.bdimg.com/it/u=3042410010,1849246464&fm=21&gp=0.jpg",
                "http://img1.imgtn.bdimg.com/it/u=2783552916,508796275&fm=21&gp=0.jpg",
                "http://img1.imgtn.bdimg.com/it/u=298400068,822827541&fm=21&gp=0.jpg",
                "http://img3.imgtn.bdimg.com/it/u=806135425,801140138&fm=21&gp=0.jpg"};
        mWorkerThread = new MyWorkerThread_message(new Handler(),this);
//        创建新线程
        mWorkerThread.start();

        mWorkerThread.prepareHandler();
        Random random = new Random();
//       添加图片到message
        for (String url : urls){
            mWorkerThread.queueTask(url, random.nextInt(2), new ImageView(this));
        }
    }

    @Override
    protected void onPause() {
        isVisible = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWorkerThread.quit();
        super.onDestroy();
    }

    /**
     * 加载图片到UI*/
    @Override
    public void onImageDownloaded(ImageView imageView, Bitmap bitmap, int side) {
        Log.i("TT", "----onImageDownloaded--in——ui " + Thread.currentThread().getId());
        imageView.setImageBitmap(bitmap);
        if (isVisible && side == LEFT_SIDE){
            mLeftSideLayout.addView(imageView);
        } else if (isVisible && side == RIGHT_SIDE){
            mRightSideLayout.addView(imageView);
        }
    }

}
