package HandlerThread;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by lushan on 2015/8/17.
 */

//initialize HandlerThread general method
public class MyWorkerThread extends HandlerThread {
    private Handler mWorkerHandler;

    public MyWorkerThread(String name) {
        super(name);
    }

    public void postTask(Runnable task){
        mWorkerHandler.post(task);
    }

    public void prepareHandler(){
        mWorkerHandler = new Handler(getLooper());
    }
}
