package com.bignerdranch.android.criminalintent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";

//    相片保存的路径
    public static final String EXTRA_PHOTO_FILENAME = "CrimeCameraFragment.filename";

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;

//    实现ShutterCallback接口，显示进度条视图
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            // display the progress indicator
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
//    实现PictureCallback接口命名并保存已拍摄的JPEG图片文件
    private Camera.PictureCallback mJpegCallBack = new Camera.PictureCallback() {
//    onPictureTaken放法创建了一个UUID字符串作为图片文件名，然后用javaIO打开输出流，将Camera传入的JPEG数据写入文件
//    注意，代表进度指示条的mProgressContainer变量没有再设置回不可见状态，既然在onPictureTaken方法中，fragment视图最终会随着activity的销毁而销毁，那就没有
//    必要关心mProgressContainer的处理了
        public void onPictureTaken(byte[] data, Camera camera) {
            // create a filename
            String filename = UUID.randomUUID().toString() + ".jpg";
            // save the jpeg data to disk
            FileOutputStream os = null;
            boolean success = true;
            try {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            } catch (Exception e) {
                Log.e(TAG, "Error writing to file " + filename, e);
                success = false;
            } finally {
                try {
                    if (os != null)
                        os.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing file " + filename, e);
                    success = false;
                } 
            }
            
//            判断照片处理状态，如果照片保存成功，就创建一个intent并设置结果代码为RESULT_OK,反之，则设置结果代码为canceled
            if (success) {
                // set the photo filename on the result intent
                if (success) {
                    Intent i = new Intent();
                    i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                    getActivity().setResult(Activity.RESULT_OK, i);
                } else {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                }
            }
            getActivity().finish();
        }
    };

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);

//        先设置进度条不可见
        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);
        Button takePictureButton = (Button)v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCamera != null) {
//                    mCamera.takePicture(Camera.ShutterCallback, Camera.PictureCallback raw, Camera.PictureCallback jpeg);
//                    ShutterCallback回调方法会在相机不活图像时调用，但此时，图像数据还未处理完成，第一个PictureCallback回调方法是在原始图像数据可用使调用
//                    通常来说，是在加工处理原始图像数据且没有存储之前
//                    第二个PictureCallback回调方法是在JPEG版本的图像可用时调用
//                    可以实现takePicture方法P286页有这几个方法的调用
            	    mCamera.takePicture(mShutterCallback, null, mJpegCallBack);
            	}
            } 
        });

//        surfaceholder是我们与surface对象的联系。
//        surface对象也有生命周期，SurfaceView出现在屏幕上时，会创建Surface，SurfaceView从屏幕上消失时，surface随即被销毁。
//        surface销毁后，再将camera从surfaceHolder上断开
//        surface不存在是，必须保证没有任何内容要在其上绘制
//        cmera是surface的客户端
        mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        // deprecated, but required for pre-3.0 devices
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        surfaceHolder提供了另外一个接口坚挺surface生命周期中的事件，这样控制surface与其客户端协同工作
        holder.addCallback(new SurfaceHolder.Callback() {

//            包含SrufaceView的视图层级结构被放倒屏幕上时调用该方法，这也是Surface与其客户端进行关联的地方
            public void surfaceCreated(SurfaceHolder holder) {
                // tell the camera to use this surface as its preview area
                try {
                    if (mCamera != null) {
//                        连接Camera与Surface
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException exception) {
                    Log.e(TAG, "Error setting up preview display", exception);
                }
            }

//            SurfaceView从屏幕上移除时，Surface也随即被销毁。通过该方法通知Surface的客户端停止使用Surface
            public void surfaceDestroyed(SurfaceHolder holder) {
                // we can no longer display on this surface, so stop the preview.
                if (mCamera != null) {
//                    该停止在Surface上绘制帧
                    mCamera.stopPreview();
                }
            }

//            Surface首次显示在屏幕上时调用该方法，通过传入参数，可以知道Surface的像素格式以及它的宽度和高度，该方法，通知客户端
//            其有多大的绘制区域可以使用
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            	if (mCamera == null) return;
            	
//            	通过Camera.Parameters嵌套类获取系统支持的相机预览尺寸列表
                // the surface has changed size; update the camera preview size
                Camera.Parameters parameters = mCamera.getParameters();
//                getSupportedPreviewSizes返回android.hardware.Camera.Size类实例的一个列表，每个实例封装了一个具体的图片宽高尺寸。
//                要找到适合Surface的预览尺寸，可以将列表中的预览尺寸与传入surfaceChanged方法的Surface的宽高进行比较。
//                这里是设置预览尺寸为合适的尺寸
                Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
                parameters.setPreviewSize(s.width, s.height);
//                设置图片尺寸与设置预览一样，getSupportedPictureSizes来获得所有图片尺寸
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);
                try {
//                    该方法用来在Surface上绘制帧
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }
        });
        
        return v; 
    }

    @TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();
//        open(int)是api9引入的，0默认代表后置摄像头。
//        在CrimeCameraFragment生命周期中，应该在onResume()和onPause()回调方法中打开和释放相机资源
//        这两个方法可确定用户能够同fragment视图交互的时间边界，只有在用户能够同fragment视图交互时，相机才可以使用。
//        （即使fragment首次开始出现在屏幕上，onResume方法也会被调用）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(0);
        } else {
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

//        实现空值检查可以防止应用意外崩溃。这是需要采纳的
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /** a simple algorithm to get the largest size available. For a more 
     * robust version, see CameraPreview.java in the ApiDemos 
     * sample app from Android. */
//    这段是通过遍历相机尺寸获得最大的符合屏幕尺寸的相机尺寸
    private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
        Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }
    
}
