package com.bignerdranch.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "criminalintent.DATE";

    Date mDate;


//    fragment和fragment之间交互数据
    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);
//        将Date作为args传递给fragment
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);

//        在下面的onCreateDialog中，创建的时候通过getArguments来获得时间的参数
        return fragment;
    }

//    创建一个intent，将日期数据作为extra附加到intent上，最后调用onActivityResult，返回给CrimeFragment
//    这是一个与传入setTargetFragment方法向匹配的请求代码，用以告知目标fragment返回结果来自于哪里
    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) 
            return;

        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);
//        这个DatePicker是实时更新mDate的，这不的处理不需要关心，只需要知道mDate是用户交互设置的时间就好
//        调用CrimeFragment的onActivityResult，返回事件数据
        getTargetFragment()
            .onActivityResult(getTargetRequestCode(), resultCode, i);
    }


    //        在屏幕上显示DialogFragment时候，托管activity的Fragmentmanager回调用这个方法
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        如果设备旋转了可以保从保留的arguments获取数据用来重建fragment
        mDate = (Date)getArguments().getSerializable(EXTRA_DATE);

//        转换日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = getActivity().getLayoutInflater()
            .inflate(R.layout.dialog_date, null);

        DatePicker datePicker = (DatePicker)v.findViewById(R.id.dialog_date_datePicker);
        datePicker.init(year, month, day, new OnDateChangedListener() {
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                mDate = new GregorianCalendar(year, month, day).getTime();

                // update argument to preserve selected value on rotation
                //        如果设备旋转了可以保从保留的arguments获取数据用来重建fragment

                getArguments().putSerializable(EXTRA_DATE, mDate);
            }
        });


//        AlertDialog.Builder类，以流接口fluent interface的方式创建一个AlertDialog实例，首先通过getactivity传入AlertDialog.Builder类一个Context参数给这个构造方法
//        返回一个AlertDialog.Builder实例，然后一个个的设置控件
        return new AlertDialog.Builder(getActivity())
            .setView(v)
            .setTitle(R.string.date_picker_title)
//                setPositiveButton需要两个参数，一个是字符串资源ID，一个是实现DialogInterface.OnClickListener接口的对象
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    调用sendResult将日期数据返回给CrimeFragment
//                    RESULT_OK是
                    public void onClick(DialogInterface dialog, int which) {
//                        从Activity的FragmentManager中调用RESULT_OK常量，代表这个Fragment已经完成。用以告知CrimeFragment的onActivity做判断
                        sendResult(Activity.RESULT_OK);
                }
            })
            .create();
    }
}
