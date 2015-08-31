package shareescience_p01.com.shareescience;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.TextView;

/**
 * Created by lushan on 2015/8/31.
 */
public class MyHandlerThread extends HandlerThread {
    Handler mHandler;
    Handler mResponseHandler;
    private static final String TAG = "MyHandlerThread";
    private static final int MESSAGE_DOWNLOAD = 0;

    public MyHandlerThread(Handler responsehandler){
        super(TAG);
        mResponseHandler = responsehandler;
    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what ==MESSAGE_DOWNLOAD){


                    mResponseHandler.post(new Runnable() {
                        @Override
                        public void run() {


                        }
                    });

                };
            }
        };
    }
    private void handlerequest(){
    }

    private void queueHttp(FetcherJSON result,TextView view){
        mHandler.obtainMessage(MESSAGE_DOWNLOAD,view).sendToTarget();
    }
}
