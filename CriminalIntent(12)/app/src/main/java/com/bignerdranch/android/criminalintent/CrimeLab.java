package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

public class CrimeLab {


//    crime数组对象存储在一个单例里，在创建单例的实例时，一个类仅允许创建一个实例
//    应用能够在内存里存在多久，单例就能存在多久。
//    要创建单例，需要创建一个带有私有构造方法及get方法的类。其中get方法返回实例，如果实例已经存在。
    private ArrayList<Crime> mCrimes;
    private static CrimeLab sCrimeLab;
    private Context mAppContext;

//    私有构造方法,关联到
    private CrimeLab(Context appContext) {
        mAppContext = appContext;
        mCrimes = new ArrayList<Crime>();
        for (int i = 0; i < 100; i++) {
            Crime c = new Crime();
            c.setTitle("Crime #" + i);
            c.setSolved(i % 2 == 0); // every other one
            mCrimes.add(c);
        }
    }




    public static CrimeLab get(Context c) {
        if (sCrimeLab == null) {
//            从全局context中获得context来对CrimeLab赋值，并新建
            sCrimeLab = new CrimeLab(c.getApplicationContext());
        }
        return sCrimeLab;
    }

    public Crime getCrime(UUID id) {
        for (Crime c : mCrimes) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }
    
    public ArrayList<Crime> getmCrimes() {
        return mCrimes;
    }
}

