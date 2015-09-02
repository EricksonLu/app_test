package com.bignerdranch.android.criminalintent;

import java.util.UUID;

import android.support.v4.app.Fragment;

import java.util.UUID;



//继承自SingleFragmentActivity，所以SingleFragmentActivity的onCreate方法也同样继承了，生成了一个fragmentmanager。管理activity_fragment.

//在activity中托管一个UI fragment有两种方式，添加fragment到activity布局中。或者在activity代码中添加fragment。这个例子采用第二种方法
public class CrimeActivity extends SingleFragmentActivity {
//    这个方法是从抽象方法继承通用类来生成Activity的方法，务必在子类return一个new 的方法。？？？？能上网了再查
//    新建的实例用来传递crimeID
	@Override
    protected Fragment createFragment() {
//        从CrimeActivity的intent中获取extra数据，然后新建Fragment
        UUID crimeId = (UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);

//        获取传入了参数的Fragment
        return CrimeFragment.newInstance(crimeId);
    }
}
