package com.example.lushan.myapplication2_message;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MyWorkerThread_message extends HandlerThread {
    private Handler mWorkerHandler;
    private Handler mResponseHandler;
    private static final String TAG = MyWorkerThread_message.class.getSimpleName();
    private Map<ImageView, String> mRequestMap = new HashMap<ImageView, String>();
    private Callback mCallback;


    @Override
    public void run() {
//        子线程
        Log.i("TT", "----run " + Thread.currentThread().getId());
        super.run();
    }

    public static interface Callback {
        public void onImageDownloaded(ImageView imageView, Bitmap bitmap, int side);
    }

    public MyWorkerThread_message(Handler responseHandler, Callback callback) {

        super(TAG);
        mResponseHandler = responseHandler;
//        仅为了后面的request实现MyActivity里的callback加载图片到UI用
        mCallback = callback;
//        start主线程
        Log.i("TT", "----...start " + Thread.currentThread().getId());
    }

    public void queueTask(String url, int side, ImageView imageView) {
        mRequestMap.put(imageView, url);
        Log.i(TAG, url + " added to the queue");
//        从ui线程获得消息并发送回ui线程
        mWorkerHandler.obtainMessage(side, imageView).sendToTarget();
//        queue主线程
        Log.i("TT", "----queue " + Thread.currentThread().getId());
    }

    @Override
    protected void onLooperPrepared() {
//        子线程
        Log.i("TT", "----onLooperPrepared " + Thread.currentThread().getId());
        super.onLooperPrepared();
    }

    //    创建ui的工作looper并绑定到mWorkerHandler
    public void prepareHandler() {
//        before_prepare主线程
        Log.i("TT", "----before_prepare " + Thread.currentThread().getId());
//        回调方法用来更新ui操作

//        通过创建handler和looper和callback来更新ui里的
        mWorkerHandler = new Handler(getLooper(), new Handler.Callback() {

//            创建处理来自messagequeue中的message
            @Override
            public boolean handleMessage(Message msg) {
                Log.i("TT", "----msg_handle " + Thread.currentThread().getId());


//                从messagequeue中传递msg给
                ImageView imageView = (ImageView) msg.obj;
                String side = (msg.what == MyActivity.LEFT_SIDE ? "left side" : "right side");
//                Log.i(TAG, String.format("Processing %s, %s", mRequestMap.get(imageView), side));
//                msg 子线程
                Log.i("TT", "----msg " + Thread.currentThread().getId());
                handleRequest(imageView, msg.what);
                msg.recycle();
                return true;
            }
        });
    }
//
    private void handleRequest(final ImageView imageView, final int side) {
        String url = mRequestMap.get(imageView);
//        in_handleRequest 子线程
        Log.i("TT", "----in_handleRequest " + Thread.currentThread().getId());
        try {
//            try_handleRequest 子线程
            Log.i("TT", "----try_handleRequest " + Thread.currentThread().getId());
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(url).openConnection();
//            从url获取图片到bitmap
            final Bitmap bitmap = BitmapFactory.decodeStream((InputStream) connection.getContent());
            mRequestMap.remove(imageView);
            mResponseHandler.post(new Runnable() {
                @Override

                public void run() {
//                    ----mResponseHandler 主线程
                    Log.i("TT", "----mResponseHandler " + Thread.currentThread().getId());
                    mCallback.onImageDownloaded(imageView, bitmap, side);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





