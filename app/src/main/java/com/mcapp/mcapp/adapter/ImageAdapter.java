package com.mcapp.mcapp.adapter;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcapp.mcapp.R;
import com.mcapp.mcapp.model.Photo;
import com.mcapp.mcapp.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter {
//    private List<byte[]> mImgList=new ArrayList<>();
    private List<Photo> mImgList=new ArrayList<>();
    private OnItemClickCallback mCallback;
    private OnItemLongClickCallback mLongCallback;

    public ImageAdapter() {
    }

    public void setData(List<Photo> list) {
        this.mImgList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_vertical, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;
        //侧滑按钮传参---图片位置
        itemHolder.btnFaceFind.setTag(R.id.face_first,"face_add");
        itemHolder.btnFaceFind.setTag(R.id.face_second,mImgList.get(position));
        itemHolder.btnOthersFind.setTag(R.id.last_first,"last");
        itemHolder.btnOthersFind.setTag(R.id.last_second,mImgList.get(position));
        itemHolder.btnDelete.setTag(mImgList.get(position).getId());
        GlideUtil.loadImage(itemHolder.iv_pic.getContext(), mImgList.get(position).getImagesByte(), itemHolder.iv_pic);
        final int fp = position;
        itemHolder.iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onItemClick(fp, itemHolder.iv_pic);
                }
            }
        });
//        itemHolder.iv_pic.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (mLongCallback != null) {
//                    mLongCallback.onItemLongClick(fp, itemHolder.iv_pic);
//                }
//                return true;
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mImgList != null ? mImgList.size() : 0;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        private ImageView iv_pic;
        private Button btnFaceFind;
        private Button btnOthersFind;
        private Button btnDelete;
        public ItemHolder(View itemView) {
            super(itemView);
            iv_pic = itemView.findViewById(R.id.iv_pic);
            btnFaceFind = itemView.findViewById(R.id.btn_faceDetect);
            btnOthersFind = itemView.findViewById(R.id.btn_othersFind);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public void setOnItemClickCallback(OnItemClickCallback clickCallback) {
        this.mCallback = clickCallback;
    }
    public void setOnItemLongClickCallback(OnItemLongClickCallback clickLongCallback) {
        this.mLongCallback = clickLongCallback;
    }

    public interface OnItemClickCallback {
        void onItemClick(int position, ImageView view);
    }
    public interface OnItemLongClickCallback {
        void onItemLongClick(int position, ImageView view);
    }
}
