package com.mcapp.mcapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mcapp.mcapp.model.Photo;
import com.mcapp.mcapp.utils.FindThread;
import com.mcapp.mcapp.utils.SourceUtil;


public class FaceFragment extends Fragment implements View.OnClickListener {
    private ImageView imageView;
    private ImageView imageView2;
    private TextView scoreText;
    private Photo photo;
    private Button btnFaceAdd;
    private Button btnFaceSearch;
    private Button btnFaceFind;
    private Button btnFaceMatch;
    private static final int COMPLETED = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            photo = null;
            if (msg.what == COMPLETED) {
                imageView.setImageDrawable(null); //UI更改操作
            }
        }
    };

    public static FaceFragment newInstance(String text) {
        FaceFragment fragmentCommon = new FaceFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text", text);
        fragmentCommon.setArguments(bundle);
        return fragmentCommon;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        String s = getArguments().getString("text");
        View view = inflater.inflate(R.layout.fragment_face, container, false);
        imageView = view.findViewById(R.id.img_3);
        imageView2 = view.findViewById(R.id.img_4);
        scoreText = view.findViewById(R.id.scoreText);
        if(SourceUtil.show){
            imageView2.setVisibility(View.VISIBLE);
        }
        if (photo != null) {
            Bitmap decodedByte = BitmapFactory.decodeByteArray(photo.getImagesByte(), 0, photo.getImagesByte().length);
            if(!SourceUtil.show){
                SourceUtil.imagesByte=photo.getImagesByte().clone();
                imageView.setImageBitmap(decodedByte);
            }else{
                imageView2.setImageBitmap(decodedByte);
                Bitmap decodedByte2 = BitmapFactory.decodeByteArray(SourceUtil.imagesByte, 0, SourceUtil.imagesByte.length);
                imageView.setImageBitmap(decodedByte2);
            }

        }

        btnFaceAdd = view.findViewById(R.id.btn_FaceAdd);
        btnFaceSearch = view.findViewById(R.id.btn_FaceSearch);
        btnFaceFind = view.findViewById(R.id.btn_FaceFind);
        btnFaceMatch = view.findViewById(R.id.btn_FaceMatch);
        btnFaceAdd.setOnClickListener(this);
        btnFaceSearch.setOnClickListener(this);
        btnFaceFind.setOnClickListener(this);
        btnFaceMatch.setOnClickListener(this);

        imageView2.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                scoreText.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
                imageView2.setImageDrawable(null);
                SourceUtil.show=false;
                return false;
            }
        });
        return view;
    }

    public void setImg(Photo photo) {
        this.photo = photo;
    }

    public void setBitmapImg(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
    public void setScore(Double score) {
        java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.00");
        final String str = myformat.format(score);
        new Handler(getContext().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                scoreText.setText("分数:"+str);
                scoreText.setVisibility(View.VISIBLE);
            }
        });

    }

    public void makeToast(final String s) {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
    }

    public void clearImage() {
        photo = null;
        new Thread() {
            @Override
            public void run() {
                imageView.setImageDrawable(null);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        if (photo == null) {
            makeToast("请先选择图片!");
            return;
        }
        switch (v.getId()) {
            case R.id.btn_FaceAdd:
                Intent intent = new Intent(getActivity(), FaceAddActivity.class);
                intent.putExtra("id", photo.getId());
                startActivity(intent);
//                new FindThread(photo.getImagesByte(), 1, this).start();
                break;
            case R.id.btn_FaceSearch:
                Intent intent2 = new Intent(getActivity(), FaceSearchActivity.class);
                intent2.putExtra("id", photo.getId());
                startActivity(intent2);
                break;
            case R.id.btn_FaceFind:
                new FindThread(photo.getImagesByte(), 0, this).start();
                break;
            case R.id.btn_FaceMatch:
                if(!SourceUtil.show){
                    SourceUtil.show =true;
                    imageView2.setVisibility(View.VISIBLE);
                    makeToast("请再选择一张图片!");
                }else{
                    new FindThread(SourceUtil.imagesByte,photo.getImagesByte(), 3, this).start();
                }

                break;
        }
    }
}
