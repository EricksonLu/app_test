package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class CrimeFragment extends Fragment {
    public static final String EXTRA_CRIME_ID = "criminalintent.CRIME_ID";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;

    Crime mCrime;
    EditText mTitleField;
    Button mDateButton;
    CheckBox mSolvedCheckBox;

//    传递crimeId的新建实例(当CrimeActivity)创建CrimeFragment时，应调用CrimeFragment.newInstance，并传入从它的extra中获取的UUID参数值，
//    这个fragment保留着这个值，不需要从上级actvity获取
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }


//    因为需要被托管fragment的任何activity调用，所以是公共方法
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        从Fragment的argument中获取crimeId，不需要从上级托管了的activity中获取。
        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
//        获取CrimeActivity的intent返回至此Fragment,让fragment直接获取托管activity的intent，
        UUID cirmeID1 = (UUID) getActivity().getIntent().getSerializableExtra(EXTRA_CRIME_ID);

//        getActivity返回CrimeActivity的intent
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    public void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }


//    parent是视图的父视图，第三个参数是告知布局生成器是否将生成的视图添加给父视图，因为将通过activity代码的方式添加视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
 
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());

//        添加text_watcher,CharSequence代表用户输入
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                c.notify();
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });
        
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {                	            	
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
//                创建DatePickerFragment，并传递给其构造函数日期数据
                DatePickerFragment dialog = DatePickerFragment
                    .newInstance(mCrime.getDate());
//                将CrimeFragment设置成DatePickerFragment的目标fragment，建立这种关联
//                在CrimeFragment使用REQUEST_DATE
//
//                使用REQUEST_DATE通知是哪个fragment在返回数据
//                目标target，CrimeFragment使用getTarget和gettargetrequestcode方法获取它们
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);

//                使用dialog.show(FragmentManager fm,String tag);显示日期控件
                dialog.show(fm, DIALOG_DATE);              	
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // set the crime's solved property
                mCrime.setSolved(isChecked);
            }
        });

 
        
        return v;
    }

//    处理由DatePickerFragment返回的数据
//    此处的requestCode和resultCode是Fragment的一种管理方式。
//    requestCode是自己定义的，用来告知CrimeFragment的这个是来自于哪个Fragment，有个能多个Fragment和这个CrimeFragment向关联是就要区分了
//    而这个resultCode是由FragmentManager统一管理的RESULT_OK，这个是Activity的属性，一定要优先调用，否则会ANR
//    写onActivityResult，要如这个例子。
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
    }
}
