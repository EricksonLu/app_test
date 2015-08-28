package com.bignerdranch.android.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;

//实现动态登记

//从概念上讲，常规broadcast intent可同时被其他应用所接收，而现在，onReceiver方法是在主线程上调用的，所以实际上，receiver并没有同步并发运行
//因而，指望它们按照某种顺序依次运行，或知道他们什么时候全部结束运行也是不可能的，结果，这给broadcast receiver之间的通信，或intent发送者接受receiver的信息都带来了麻烦
//为了解决问题，可使用有序broadcast intent实现双向通信。有序broadcast允许多个broadcast receiver依序处理broadcast intent。另外，传入一个名为result receiver的特别broadcast receiver
//有序broadcast还可实现让broadcast的发送者接收broadcast接收者发送的返回结果
//从接收方来看，这看上去与常规broadcast没什么区别，然而，他确实一套改变接收者返回值的方法。这里我们需要取消通知消息，可通过一个简单的整数结果代码，将此需要告知信息发送者。
//使用setResultCode方法，设置结果码为Activity.RESULT_CANCELE。
public abstract class VisibleFragment extends Fragment {
//    这个类是用来隐藏前台通知的通用fragment
    public static final String TAG = "VisibleFragment";

//    这个是用来接收的动态broadcast receiver
    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // if we receive this, we're visible, so cancel
            // the notification
            Log.i(TAG, "canceling notification");
//            另外，还有setResultData(String)  setResultRxtras(Bundle)
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
//        这里的动态注册相当于动态的在manifest中声明了
//        其他应用可以通过创建自己的broadcast intent来出发broadcast receiver同样，在registerReceiver方法中传入自定义权限
        getActivity().registerReceiver(mOnShowNotification, filter, PollService.PERM_PRIVATE, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mOnShowNotification);
    }
}

//########################################################
//最后的也是最重要！
//对于过滤前台通知消息这个东西，有一个NotificationReceiver和一个VisibleFragment两个receiver和一个showBackgroundNotification
//第三个是用来发送的，发送broadcast是在定时器内部的，由IntentService执行，不停的在应用内部广播，有一个动态的注册的receiver是这个VisibleFragment，
//和一个在manifest中注册了的receiver这个注册的receiver只要应用启动过都会运行，如果有了新的图片就会响应，并发送通知。动态注册的receiver表示我们现在就在这个应用内部
//不需要通知我们有新的图片更新，所以取消通知。这就是动态和静态的两个receiver的作用