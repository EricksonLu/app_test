package com.example.lushan.handlerthreadtest;

/**
 * Created by lushan on 2015/8/18.
 */
public class Runner implements Runnable{

    @Override
    public void run() {
// TODO Auto-generated method stub
// 子线程3中执行 随便产生一个随机数吧
        double randomData = Math.random();
        System.out.println("The random number is = " + randomData);
        System.out.println("线程3的ID号是 = " + Thread.currentThread().getId());
    }

}