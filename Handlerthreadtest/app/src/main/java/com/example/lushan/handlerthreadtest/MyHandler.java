package com.example.lushan.handlerthreadtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by lushan on 2015/8/18.
 */
public class MyHandler extends Handler {

    public MyHandler(Looper looper) {
// TODO Auto-generated constructor stub
// 使用父类的构造函数，此项必须要有
        super(looper);
    }
    // 别看仅仅只有一个消息处理函数，其实系统会fork几个线程各自执行自己的handlerMessage方法。并行处理的
    @Override
    public void handleMessage(Message msg) {
// TODO Auto-generated method stub
        super.handleMessage(msg);
// 所有消息的处理都在此处进行,此处对消息进行分类处理
        Bundle data = msg.getData();
// 如果此消息是由handler01发送的，将会在子线程1中执行下面if的代码。此线程中没有关于UI的操作
        if(data.getInt("threadNum") == 1){
            String str = data.getString("name");
            System.out.println(str);
            System.out.println("线程1的ID号是 = " + Thread.currentThread().getId());
        }
// 如果此消息是由handler02发送的，将会在子线程2中执行下面else if的代码。为了区分子线程1，线程2中加入了UI的操作。
// Android系统中是禁止子线程执行更新UI的操作的。所以如果想要将子线程的执行数据结果用来更新UI，那么可以借助UI线程
// 的handlerUI,这样就可以调去UI线程执行更新。只需用UI线程的handler post或send一个消息过去即可。
        else if(data.getInt("threadNum") == 2){

//            ************************这里通过设置textDisplay更新UI，和主线程的public handlerUI在这里更新UI线程
            handlerthread_activity.textDisplay = data.getString("display") + data.getString("text");
//倘若直接执行myText.setText(textDisplay);便会发生异常
            handlerthread_activity.handlerUI.post(handlerthread_activity.updateUI);


            System.out.println("线程2的ID号是 = " + Thread.currentThread().getId());
        }
    }

}