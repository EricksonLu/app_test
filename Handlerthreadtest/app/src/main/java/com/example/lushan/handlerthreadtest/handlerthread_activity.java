package com.example.lushan.handlerthreadtest;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class handlerthread_activity extends Activity {
//handlerUI创建UI线程用来更新UI的handler
    public static Handler handlerUI = new Handler();
    public  static TextView myText = null;
    public static String textDisplay;
    public static UpdateUI updateUI = new UpdateUI();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handlerthread_activity);
// 根据ID获取控件
        myText = (TextView)findViewById(R.id.isss);
// 实现一个Runnable接口，实现其中的run方法，以便在子线程3中执行。

// 创建一个Runna类的runnable对象，供handle03发送。
        Runner run03 = new Runner();

// 实现一个Runnable接口，实现其中的run方法，以便完成子线程2要更新UI的操作。

// 为了完成线程2的功能，此处需要创建UI线程的Handler。因为系统默认创建的Handler便是UI的handler，所以无需根据UI线程的Looper获取。


// 创建一个Runna类的runnable对象，供handle03发送。


// 重写Handler的handlemsg方法，以便在不同的线程当中处理各个消息。


// 以下每个线程都是有HandlerThread创建，所以3个子线程都有其Looper实例。如果采用Thread类创建新线程则没有，这是两个线程类最根本的区别。
        HandlerThread subThread01 = new HandlerThread("子线程1");
        HandlerThread subThread02 = new HandlerThread("子线程2");
        HandlerThread subThread03 = new HandlerThread("子线程3");

// 在使用getLooper方法之前必须要先执行start方法，这是官方文档的重要说明。
        subThread01.start();
        subThread02.start();
        subThread03.start();



// 要想将消息对象或runnable对象分发到各个子线程，并让子线程执行。必须要获得发送消息的工具：Handler对象。
// 注意：每个特定的handler对象只负责向一个特定的线程发送消息，让那个特定的线程来执行和处理消息。比如：UI线程的handler，它的post runnable
// 或者 send message方法都只会将消息发送到主线程，让主线程来从其消息队列中取出并执行。
// 因此，下面对各个特定线程分别创建它们的handler对象
        MyHandler handler01 = new MyHandler(subThread01.getLooper());
        MyHandler handler02 = new MyHandler(subThread02.getLooper());
        MyHandler handler03 = new MyHandler(subThread03.getLooper());

// 接下来我们便可以利用各个线程对应的handler对象实例来向各个线程分发消息或runnable对象了。分发完成以后，各个线程将在CPU的调度下，并行运行，
//各自在自己的线程中去处理自己的消息。
// 因为handler既能发送消息，也能发送runnable对象，所以下面将给出不同。

// handler01发送一个消息到子线程1中去处理消息
        Message msg01 = new Message();
// 创建bundle对象，封装消息的数据信息
        Bundle data01 = new Bundle();
        data01.putString("name", "您好！我是李茂！");
        data01.putInt("threadNum", 1);
        msg01.setData(data01);
// 异步机制：此处发送完消息以后，sendMessage函数会立马返回。消息被放入到子线程1的Looper中的消息队列中了，等待Looper取出msg01执行。
        handler01.sendMessage(msg01);

// handler02发送一个消息到子线程2中去处理消息
        Message msg02 = new Message();
        Bundle data02 = new Bundle();
        data02.putString("display", "这是Handler02");
        data02.putString("text", "发送的更新UI的消息");
        data02.putInt("threadNum", 2);
        msg02.setData(data02);
        handler02.sendMessage(msg02);

// handler03发送一个runnable对象，然后在子线程三种执行该runnable对象中的run方法。
        handler03.post(run03);
    }
}
