package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;

import android.widget.ImageView;

public class PictureUtils {
    /**
     * Get a BitmapDrawable from a local file that is scaled down
     * to fit the current Window size.
     */
    @SuppressWarnings("deprecation")
//    如果能将图片缩放至完美匹配ImageView视图的尺寸最好了。但是通常无法及时获得用来显示图片的视图尺寸。
//    例如在onCreateView方法中，就无法获得ImageView视图的尺寸。设备的默认屏幕大小是固定可知的，因此稳妥起见
//    可以缩放图片至设备的默认显示屏大小。注意，用来显示图片的视图可能会小于默认的屏幕显示尺寸，但大于屏幕默认的显示尺寸肯定不行
    public static BitmapDrawable getScaledDrawable(Activity a, String path) {
        Display display = a.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        // read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round((float)srcHeight / (float)destHeight);
            } else {
                inSampleSize = Math.round((float)srcWidth / (float)destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return new BitmapDrawable(a.getResources(), bitmap);
    }

//    清理图片方法，这个将在CrimeFragment的onStop中方法调用
//    在onStart方法中加载图片，然后再onStop方法中卸载图片是一个好习惯，这些方法标志着用户可以看到activity的时间点，
//    如果在onResume和onPause方法中加载和卸载图片，用户体验会不好
//    暂停的activity也可能部分可见，不如说非全屏的activity视图显示在暂停的activity视图之上时。如果使用了onResume方法和onPause方法，那么图像消失后
//    因为没有被全部遮住，它有显示在了屏幕上，所以，activity的视图一出现时就加载图片，然后等到activity再也不可见的情况下再对他们卸载。
    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable)) 
            return;

        // clean up the view's image for the sake of memory
        BitmapDrawable b = (BitmapDrawable)imageView.getDrawable();
//        recycle方法释放了bitmap占用的原始存储空间。如果不主动调用recycle()方法释放内存，占用的内存也会被清理，但是它是在将来某个时间点再finalizer重清理
//        而不是在bitmap自身的垃圾回收时清理，这以为这很可能在finalizer调用之前，应用已经耗尽了内存
//        如果应用使用的图片文件很大，最好主动调用recycle方法，避免内存好近问题
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }
}

