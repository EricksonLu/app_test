package com.example.lushan.handlerthreadtest;

/**
 * Created by lushan on 2015/8/18.
 */
class UpdateUI implements Runnable{

    @Override
    public void run() {
// TODO Auto-generated method stub
// 更新显示文本
        handlerthread_activity.myText.setText(handlerthread_activity.textDisplay);
        System.out.println("UI线程的ID号是 = " + Thread.currentThread().getId());
    }

}