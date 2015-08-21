package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.support.v4.view.ViewPager;







//因为CrimePagerActivity是通过CrimeListFragment的里的
//Intent i = new Intent(getActivity(), CrimePagerActivity.class);
//        通过extra将EXTRA_CRIME_ID告诉CrimeFragment
//        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
//        startActivityForResult(i, 0);
//这里启动的，同过intent来传递的crimeID
//这个viewPager实际启动的是CrimeFragment，可以从后面的getItem看到

public class CrimePagerActivity extends FragmentActivity {
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
//        在values下建立的ids.xml中建立viewpager的资源id并分配给他
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        final ArrayList<Crime> crimes = CrimeLab.get(getApplicationContext()).getmCrimes();
//        final ArrayList<Crime> crimes1 = CrimeLab.get(this).getmCrimes();
//        final ArrayList<Crime> crimes2 = CrimeLab.get(getApplicationContext()).getmCrimes();
//        这三个等效，


//        获取activity的FragmentManager实例，为FragmentStatePagerAdapter提供参数。
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return crimes.size();
            }

//            获取crime数组指定位置的Crime，返回一个已配置的用于显示指定位置为crime信息的CrimeFragment
//            此方法返回的fragment添加给activity，才能使用fragment完成自己的工作，这就是FragmentManager的用途
            @Override
            public Fragment getItem(int pos) {
                UUID crimeId =  crimes.get(pos).getId();

//                新建每个页的实例
                return CrimeFragment.newInstance(crimeId);
            }
        }); 

//        从CrimeListFragment启动的intent传递来的EXTRA_CRIME_ID获得crimeID
        UUID crimeId = (UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        for (int i = 0; i < crimes.size(); i++) {
            if (crimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            } 
        }
    }
}
