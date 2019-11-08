package com.mcapp.mcapp.loader;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mcapp.mcapp.R;

import indi.liyi.viewer.ImageLoader;

public class PhotoLoader extends ImageLoader {
    @Override
    public void displayImage(Object src, ImageView imageView, LoadCallback callback) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.img_placeholder)
                .skipMemoryCache(true)
                .diskCacheStrategy( DiskCacheStrategy.NONE );
        Glide.with(imageView.getContext())
                .load(src)
                .apply(requestOptions)
                .into(imageView);
    }
}
