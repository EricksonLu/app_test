package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.ListFragment;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {
    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.crimes_title);
//        获取单例，getActivity返回和这个fragment相关联的activity，这里是从关联到应用的CrimeLab中获取getCrimes。
//        你会发现mCrimes = CrimeLab.getCrimes()方法不能用，因为1，CrimeLab是私有的构造方法，必须使用get来获得
//        2，传入的参数只能是context的，而通过自动补全弹出的第一个就是getActivity。
//        3.Context是各种服务的超类，因此包含了所有方法。
//        在创建mCrimes时候，如果是Fragment只能用getActPagerActivity
//        如果是继承自Activity可以用getAPP。。。this。。见Crime
        mCrimes = CrimeLab.get(getActivity()).getmCrimes();



//        adapter是一个控制器对象，从模型层获取数据，并将之提供给ListView显示：负责:
//        1创建必要的视图对象
//        2用模型层数据填充视图对象
//        3将准备好的视图对象返回给listview
        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
//        这是ListFragment的一个便利方法，可以为CrimeListFragment管理的内置ListView设置adapter
        setListAdapter(adapter);
    }

//    刷新显示列表
    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }


//    响应列表的点击事件，使adapter返回被点击的列表项所对应的Crime对象
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // get the Crime from the adapter
        Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
        // start an instance of CrimePagerActivity
        Intent i = new Intent(getActivity(), CrimePagerActivity.class);
//        通过extra将EXTRA_CRIME_ID告诉CrimeFragment
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());

        startActivityForResult(i, 0);

    }


// 此方法为Fragment。 onActivityResult
//  从托管activity的被启动的activity获取返回结果
//    从被启动的activity获取返回结果，从托管的actvity返回到fragment
//    用于通知listpageractivity已经更改.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

//    传递给被启动的....
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {
        public CrimeAdapter(ArrayList<Crime> crimes) {
//           继承ArrayAdapter的构造方法，并传入参数，
//            1时一个Context对象
//            2时资源id
//            3时数据集对象
            super(getActivity(), android.R.layout.simple_list_item_1, crimes);

        }

//        默认的getView依赖于Crime的toSting方法
//        覆盖原方法，定制列表项
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            convertView是一个已存在的列表项，adapter可重新配置并返回它
            // if we weren't given a view, inflate one
            if (null == convertView) {
                convertView = getActivity().getLayoutInflater()
                    .inflate(R.layout.list_item_crime, null);
            }

            // configure the view for this Crime
            Crime c = getItem(position);

//            注意这个convertView被多次赋值。。其实不是。。网好了再查？？？？？？
            TextView titleTextView =
                (TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.getTitle());
            TextView dateTextView =
                (TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);
            dateTextView.setText(c.getDate().toString());
            CheckBox solvedCheckBox =
                (CheckBox)convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(c.isSolved());

            return convertView;
        }
    }
}

