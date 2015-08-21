package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity {
    //    这个方法是从抽象方法继承通用类来生成Activity的方法，务必在子类return一个new 的方法。？？？？能上网了再查
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

//继承自SingleFragmentActivity，所以SingleFragmentActivity的onCreate方法也同样继承了，生成了一个fragmentmanager。管理activity_fragment.



}
