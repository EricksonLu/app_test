package com.bignerdranch.android.criminalintent;

//在SingleFragmentActivity的onCreat里：SingleFragmentActivity，然后其onCreat被调用，渲染activity_masterdetail布局文件，渲染fragmentContainer fragment事务
//这里的createFragment是为了
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class CrimeListActivity extends SingleFragmentActivity 
    implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

//    启动CrimeListFragment，因为SingleFragmentActivity的onCreate()中有个createFragment，而这里采用继承SingleFragmentActivity的方式，
//    相当于不用写onCreate()
//    createFragment返回给SingleFragmentActivity里onCreate的fragment = createFragment();
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

//    由SingleFragmentActivity的getLayoutResId函数，返回在refs.xml的别名资源指向layout/activity_fragment的视图
//    用于在SingleFragmentActivity，里onCreate()函数中的setContentView(getLayoutResId());
//    这里的这个资源如果设备是平板的话会加载2个fragment的资源，如果不是就加载fragmentContainer
//    先是指向activity_masterdetail，通过这个再去指向activity_fragment或者activity_twopane
//    这两个一个是单版布局，一个是双版布局
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

//    如果使用手机用户界面布局，启动CrimePagerActivity
//    如果使用平板设备用户界面布局，将CrimeFragment放入detailFragmentContainer中。
//    为确定需实例化手机还是平板界面布局，可以检查布局ID，但最好最准确方式是检查布局是否包含detailFragmentContainer,因为，布局文件名随时可能更改，
//    并且我们也不关心布局是从哪个文件实例化产生，我们只需知道，布局文件是否包含可以放入CrimeFragment的detaiFragmentContainer。
//    如果正式布局包含detaiFragmentContainer，那么就会创建一个
//    因为List托管了CrimeListFragment布局，所以就要实现这个onCrimeSelected
    public void onCrimeSelected(Crime crime) {
//        如果是手机设备就不渲染detailFragmentContainer
        if (findViewById(R.id.detailFragmentContainer) == null) {
            // start an instance of CrimePagerActivity
            Intent i = new Intent(this, CrimePagerActivity.class);
            i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
//            启动CrimePagerActivity
            startActivityForResult(i, 0);
        } else {
//            渲染detailFragmentContainer
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

//            这里是对FragmentManager的一个操作，如果旧的detailFragmentContainer存在就移除
//            将我们需要的CrimeFragment添加到detailFragmentContainer
            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);

            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
//            如果之前就有CrimeFragment存在，应从detailFragmentContainer中移除它
            if (oldDetail != null) {
                ft.remove(oldDetail);
            } 

            ft.add(R.id.detailFragmentContainer, newDetail);
            ft.commit();
        }
    }

    public void onCrimeUpdated(Crime crime) {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment listFragment = (CrimeListFragment) fm.findFragmentById(R.id.fragmentContainer);
        listFragment.updateUI();
    }
}
