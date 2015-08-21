package com.android.chapter5.model;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by lushan on 2015/8/19.
 */

    public class MyHandler extends Handler {

        public MyHandler(Looper looper) {
// TODO Auto-generated constructor stub
// 使用父类的构造函数，此项必须要有
            super(looper);
        }
}
