package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

import android.util.Log;

public class CrimeLab {
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";
//    crime数组对象存储在一个单例里，在创建单例的实例时，一个类仅允许创建一个实例
//    应用能够在内存里存在多久，单例就能存在多久。
//    要创建单例，需要创建一个带有私有构造方法及get方法的类。其中get方法返回实例，如果实例已经存在。

    //    私有构造方法,关联到
    private ArrayList<Crime> mCrimes;
    private CriminalIntentJSONSerializer mSerializer;

    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    private CrimeLab(Context appContext) {
        mAppContext = appContext;
        mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);

        try {
            mCrimes = mSerializer.loadCrimes();
        } catch (Exception e) {
            mCrimes = new ArrayList<Crime>();
            Log.e(TAG, "Error loading crimes: ", e);
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
    
    public void addCrime(Crime c) {
        mCrimes.add(c);
        saveCrimes();
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public void deleteCrime(Crime c) {
        mCrimes.remove(c);
        saveCrimes();
    }

    public boolean saveCrimes() {
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "crimes saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving crimes: " + e);
            return false;
        }
    }
}

