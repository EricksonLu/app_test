package com.bignerdranch.android.photogallery;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class ThumbnailDownloader extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

//    后台handler
    Handler mHandler;
//    Map用来存图像和URL
    Map<ImageView,String> requestMap = Collections.synchronizedMap(new HashMap<ImageView,String>());
//    前台handler
    Handler mResponseHandler;

//    构造函数
    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }
    
    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    ImageView imageView = (ImageView)msg.obj;
                    handleRequest(imageView);
//                    Log.i(TAG, "Got a request for url: " + requestMap.get(imageView));
                }
            }
        };
    }

    private void handleRequest(final ImageView imageView) {
        try {
            final String url = requestMap.get(imageView);
            if (url == null) 
                return;
            
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes,0, bitmapBytes.length);
            
            mResponseHandler.post(new Runnable() {
                public void run() {
                    if (requestMap.get(imageView) != url)
                        return;
                    requestMap.remove(imageView);
                    imageView.setImageBitmap(bitmap);
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }
    
    public void queueThumbnail(ImageView imageView, String url) {
//        放入hashmap中
        requestMap.put(imageView, url);
//        从imageView中并加入到后台处理队列中
//        Message msg = mHandler.obtainMessage(MESSAGE_DOWNLOAD, imageView);
//        msg.arg1 = i;
//        msg.sendToTarget;
//        obtainMessage直接从message池中获得消息，而不用新建消息。
//        下面这句等于上面3句，因为不更改message信息，只需要send给自己的mHandler。
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, imageView).sendToTarget();
    }
    
    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }
}
