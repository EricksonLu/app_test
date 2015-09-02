package com.bignerdranch.android.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {
//ImageFragment需要知道Crime照片的文件路径，在ImageFragment中加入newInstance(String)方法，该方法接受照片文件路径并放置到argument bundle中

    public static final String EXTRA_IMAGE_PATH = "path";

    public static ImageFragment createInstance(String imagePath) {
//    ImageFragment不需要显示AlterDialog视图自带的标题和按钮，如果fragment采用覆盖onCreateView方法并使用简单视图的方式，要比覆盖onCtrateDialog方法并使用Dialog更简洁
        Bundle args = new Bundle();
//        创建ImageView并从argument获取文件路径，然后获取缩小版的图片并设置给ImageView
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
//                设置fragment样式为STYLE_NO_TITLE获得简洁用户界面
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    private ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup parent, Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        String path = (String)getArguments().getSerializable(EXTRA_IMAGE_PATH);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);

        mImageView.setImageDrawable(image);

        return mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }
}

