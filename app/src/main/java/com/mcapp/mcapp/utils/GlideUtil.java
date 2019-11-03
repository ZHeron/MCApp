package com.mcapp.mcapp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mcapp.mcapp.R;

public class GlideUtil {
    public static void loadImage(Context context, final Object src, final ImageView view) {
        Glide.with(context)
                .load(src)
                .apply(new RequestOptions().error(R.drawable.img_placeholder))
                .into(view);
    }

    public static void loadImage(Context context, final Object src, final ImageView view, RequestListener<Drawable> listener) {
        Glide.with(context)
                .load(src)
                .apply(new RequestOptions().error(R.drawable.img_placeholder))
                .listener(listener)
                .into(view);
    }

    public static void loadImage(Context context, final Object src, final SimpleTarget<Drawable> target) {
        Glide.with(context)
                .load(src)
                .thumbnail(0.1f)
                .apply(new RequestOptions().error(R.drawable.img_placeholder))
                .into(target);
    }
}
