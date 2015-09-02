package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

public class CrimeFragment extends Fragment {
    public static final String EXTRA_CRIME_ID = "criminalintent.CRIME_ID";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_DATE = 0;
//    CrimeCameraFragment会将图片文件名防止在extra中并不加到intent上然后传入CrimeCameraActivity.setResult(int,Intent)方法
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTACT = 2;

    Crime mCrime;
    EditText mTitleField;
    Button mSuspectButton;
    Button mDateButton;
    CheckBox mSolvedCheckBox;
    ImageButton mPhotoButton;
    ImageView mPhotoView;
    Callbacks mCallbacks;

//    传递crimeId的新建实例(当CrimeActivity)创建CrimeFragment时，应调用CrimeFragment.newInstance，并传入从它的extra中获取的UUID参数值，
//    这个fragment保留着这个值，不需要从上级actvity获取
//    添加CrimeFragment给crime明细fragment容器，让CrimeListActivity可以展示一个完整的双版面用户界面
//    第一反应是平板设备需要实现一个CrimeListFragment.onListItemClick()监听器方法就行。这样就不需要启动新的CrimePagerActivity
//    onListItemClick()方法会获取CrimeListActivity的FragmentManager然后提交一个fragment事务，将CrimeFragment添加到明细fragment容器中
//    public void onListItemClick(ListView l ,View v, int position ,long id){
//        Crime cirme = ((CrimeAdapter) getListAdapter().getItem(position)));
//        Fragment fragment = CrimeFragment.newInstance(mCrime.getId());
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        fm.beginTransaction().add(R.id.detailFragmentContainer,fragment).commit()
//    }
//    fragment天生是一种独立的开发构件，如果要开发一个fragment用来添加到其他fragment到activity的FragmentManager，那么这个frament就必须知道托管activity是如何
//    工作的，这样一来，该fragment就再也无法作为独立的开发构件使用了
//    上面代码CrimeListFragment将CrimeFragment添加给CrimeListActivity并且它知道CrimeListActivity的布局里包含有一个detailFragmentContainer但实际上，
//    CrimeListFragment根本就不关心这些，这都是它的托管activity应该处理的事情。
//    为了保持fragment的独立性，可以在fragment中定义回调接口，委托托管activity来完成那些不应由fragment处理的任务，托管activity将实现回调接口，履行托管fragment的任务
//    为了委托工作任务给托管activity，通常的做法是由fragment定义名为Callbacks的回调接口。回调接口定义了fragment委托给托管activity处理的工作任务。
//    任何打算托管目标fragment的activity必须实现这些定义的接口
//    这个Callbacks会被CrimeListActivity重写，用来重新加载crime列表，如果Crime对象的标题或问题处理状态发生改变，出发调用onCrimeUpdated方法
    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

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
//        getActivity返回CrimeActivity的intent
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        setHasOptionsMenu(true);
    }

    public void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }


//    parent是视图的父视图，第三个参数是告知布局生成器是否将生成的视图添加给父视图，因为将通过activity代码的方式添加视图
    @Override
    @TargetApi(11)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

//        让应用CrimeFragment的图标转化为按钮并显示一个向左的图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());

//        添加text_watcher,CharSequence代表用户输入
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
                mCallbacks.onCrimeUpdated(mCrime);
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });
        
        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // set the crime's solved property
                mCrime.setSolved(isChecked);
                mCallbacks.onCrimeUpdated(mCrime);
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
        
        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
//        启动摄像机
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // launch the camera activity
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
//                以接收返回值的方式启动CrimeCameraActivity
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });
        
        // if camera is not available, disable camera functionality
//        对于不带摄像机的应该禁用这个button
//        通过查询getPackageManager
        PackageManager pm = getActivity().getPackageManager();
//        hasSystemFeature传入表示设备特色功能的常量
//        FEATURE_CAMERA常量代表后置相机。
//        FEATURE_CAMERA_FRONT代表前置相机
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
//            对于没有相机的禁用button
//            前面已经在配置文件中强制将界面以水平模式展现
            mPhotoButton.setEnabled(false);
        }

//        预显示图片区域
        mPhotoView = (ImageView)v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p == null) 
                    return;

//                显示图片
//                通过调用show方法，将ImageFragment实例添加给CrimePagerActivity的FragmentManager，
//                另外还需要一个字符串常量为一定为FragmentManager中的ImageFragment
                FragmentManager fm = getActivity()
                    .getSupportFragmentManager();
                String path = getActivity()
                    .getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.createInstance(path)
                    .show(fm, DIALOG_IMAGE);
            }
        });


//        隐式(Intent)实现让用户从联系人应用里选择嫌疑人，新建的隐式Intent将由操作以及数据获取位置组成，操作为ACTION_PICK，联系人数据获取位置为：ContactsContract.Contacts.CONTENT_URI
//        请求Android协助从联系人数据库里获取某个具体联系人，因为要获取启动activity的返回结果，所以调用startActivityForResult()
        mSuspectButton = (Button)v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Android提供了一个深度定制的API用于处理联系人信息，主要是通过ConenteProvider类实现的，
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//                注意在前面onActivityResult接受一个intent处理这个activity的返回结果
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }



//        发送陋习报告按钮(隐式Intent)，陋习报告是由字符串组成的文本信息，因此，隐士intent操作是ACTION_SEND，它不指向任何数据，也不包含任何类别，但会指定数据类型为text/plain
        Button reportButton = (Button)v.findViewById(R.id.crime_reportButton);

//        隐式intent主要组成部分：1.要执行的操作。2。要访问数据的位置3.操作涉及的数据类型4可选类别
        reportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
//                putExtra都使用的是Intent中的常量，注意，这是隐式Intent的用法
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                通过R资源文件的getString获得字符串
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                创建一个选择器显示响应隐式Intent的全部Activity
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            } 
        });
        
        return v; 
    }
//    处理由DatePickerFragment返回的数据
//    此处的requestCode和resultCode是Fragment的一种管理方式。
//    requestCode是自己定义的，用来告知CrimeFragment的这个是来自于哪个Fragment，有个能多个Fragment和这个CrimeFragment向关联是就要区分了
//    而这个resultCode是由FragmentManager统一管理的RESULT_OK，这个是Activity的属性，一定要优先调用，否则会ANR
//    写onActivityResult，要如这个例子。
//    设置私有方法，将缩放后的图片设置给ImageView视图，在onStart调用
    private void showPhoto() {
        // (re)set the image button's image based on our photo
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity()
                .getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }
        mPhotoView.setImageDrawable(b);
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    @Override
    public void onStart() {
        super.onStart();
//        只要CrimeFragment视图一出现在屏幕上，就调用showphoto
//        在onActivityResult方法中，同样调用showPhoto方法，以确保用户从CrimeCameraActivity返回后
//        ImageView视图可以显示用户所拍照片
        showPhoto();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mCallbacks.onCrimeUpdated(mCrime);
            updateDate();
//            处理请求的代码如果是照片请求来的就获取照片文件名
        } else if (requestCode == REQUEST_PHOTO) {
            // create a new Photo object and attach it to the crime
            String filename = data
                .getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                Photo p = new Photo(filename);
                mCrime.setPhoto(p);
                mCallbacks.onCrimeUpdated(mCrime);
                showPhoto();
            }
        } else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
//            返回全部联系人的显示名字
            String[] queryFields = new String[] { ContactsContract.Contacts.DISPLAY_NAME_PRIMARY };
//            查询数据库并获得一个Cursor，因为假设已经知道数据库没有人同名，所以这个Cursor只包含一条记录
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            if (c.getCount() == 0) {
                c.close();
                return; 
            }

//            把c移动到第一个位置
            c.moveToFirst();
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            mCallbacks.onCrimeUpdated(mCrime);
            mSuspectButton.setText(suspect);
            c.close();
        }
    }
    
    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            无需在XML文件中定义或生成应用图标菜单项，它已经具有资源ID，这个是CrimeFragment左上角的图标
            case android.R.id.home:
//                为实现用户点击向上按钮返回至crime列表界面。在PagerActivity添加他的父activity是list
//                为什么要在这里写这个navigateUpFromSameTask????
//                1PagerActivity其实也是启动的这个CrimeFragment
//                配合AndioidManifest文件 ,对CrimePagerActivity添加mets-data属性，如下
//                <meta-data android:name="android.support.PARENT_ACTIVITY"
//                android:value=".CrimeListActivity"/>
//                这样子，无论是新建Crime的活动还是pager的活动都能实现返回
                if(NavUtils.getParentActivityName(getActivity()) != null){
                NavUtils.navigateUpFromSameTask(getActivity());}
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } 
    }
}
