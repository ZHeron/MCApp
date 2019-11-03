package com.mcapp.mcapp.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import indi.liyi.viewer.ImageLoader;

public class PhotoLoader extends ImageLoader {
    @Override
    public void displayImage(Object src, ImageView imageView, LoadCallback callback) {
        Glide.with(imageView.getContext())
                .load(src)
                .into(imageView);

    }
}
