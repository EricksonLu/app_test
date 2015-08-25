package com.bignerdranch.android.criminalintent;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
//P319
//实现双版面布局，修改SingleFragmentActivity，不在硬编码实例化布局
//创建包含两个fragment容器的布局
//修改CrimeListActivity，实现在
//虽然SingleFragmentActivity抽象类和以前一样，但是它的子类剋选择覆盖getLayoutResId，方法返回所需布局，而不再使用固定不变的activity_fragment
//在包浏览器中，创建activity_twoPane.xml创建
public abstract class SingleFragmentActivity extends FragmentActivity {
    protected abstract Fragment createFragment();

    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        渲染布局
        setContentView(getLayoutResId());
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
//        无论怎样，都会创建一个单版的布局
        if (fragment == null) {
//            这里是返回Fragment
            fragment = createFragment();
//            渲染容器
            manager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit();
        }
    }
}
