package com.bignerdranch.android.criminalintent;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


//复用创建Activity类的方法，形成抽象方法
public abstract class SingleFragmentActivity extends FragmentActivity {
//    这个方法是抽象方法继承通用类来生成Activity的方法，务必在子类return一个new 的方法。？？？？能上网了再查
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

//        通过代码的方式将fragment添加到activity中，直接调用activity的FragmentManager。因为使用了支持库及FragmentActivity类，因此调用的
//        方法是getSupportFragmentManager，如果不考虑版本兼容问题，可直接调用继承自Activity的getFragmentManager()

        FragmentManager manager = getSupportFragmentManager();
//        首先使用findFragmentById向FragmentManager请求获取fragment，如要获取的fragment在队列中已经存在，FragmentManager随机返还
//        当设备旋转的时候或回收内存被销毁重建时，CrimeActivity.onCreate方法回响应activity的重建而被调用。activity被销毁时，它的FragmentManager
//        会将fragment队列保存下来。这样，activity重建时，新的FragmentManager会首先获取保存的队列，然后重建fragment队列，从而恢复到原来的状态
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

//        如果指定容器视图资源ID的fragment不存在，则fragment变量为控制，这时应创建一个新的CrimeFragment
//        CrimeActivity因设备旋转或回收内存被销毁后，重建时，CrimeActivity.onCreate方法会响应activity的重建而被调用。
//        activity被销毁时，它的FragmentManager会将fragment队列保存下来。重建时候，新的FragmentManager首先获取保存的队列，然后重建fragment队列。
        if (fragment == null) {
            fragment = createFragment();
//            beginTransaction方法创建并返回FragmentTransaction实例。FragmentTransaction类使用了fluent interface接口方法，通过该方法可以返回FragmentTransaction对象
            manager.beginTransaction()
//                    第一个参数是容器视图资源ID，第二个参数是
                    .add(R.id.fragmentContainer, fragment)
//                    提交Transaction
                .commit();

//            这个FragmentManager管理着list和crime
        }
    }
}
