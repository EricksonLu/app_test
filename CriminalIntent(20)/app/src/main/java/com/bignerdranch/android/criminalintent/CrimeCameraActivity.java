package com.bignerdranch.android.criminalintent;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.Window;
import android.view.WindowManager;

//相机是独占性资源，一次只能有一个activity能够调用相机。SurfaceView是一种特殊的视图，可直接将要显示的内用渲染输出到设备的屏幕上，
public class CrimeCameraActivity extends SingleFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // hide the window title.
//        fragment无法在其托管activity视图创建之前添加
//        下面是淫才操作栏和状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // hide the status bar and other OS-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}

