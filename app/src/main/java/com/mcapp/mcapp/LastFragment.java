package com.mcapp.mcapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mcapp.mcapp.constant.URL;
import com.mcapp.mcapp.model.Photo;
import com.mcapp.mcapp.utils.FindThread;
import com.mcapp.mcapp.utils.SourceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LastFragment extends Fragment implements View.OnClickListener {
    private ImageView imageView;
    private Photo photo;
    private Button btnAnimal;
    private Button btnPlant;
    private static final int COMPLETED = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            photo=null;
            if (msg.what == COMPLETED) {
                imageView.setImageDrawable(null); //UI更改操作
            }
        }
    };

    public static LastFragment newInstance(String text) {
        LastFragment fragmentCommon = new LastFragment();
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
        View view = inflater.inflate(R.layout.fragment_last, container, false);
        imageView = view.findViewById(R.id.img_2);
        if (photo != null) {
            Bitmap decodedByte = BitmapFactory.decodeByteArray(photo.getImagesByte(), 0, photo.getImagesByte().length);
            imageView.setImageBitmap(decodedByte);
        }
        btnAnimal = view.findViewById(R.id.btn_AnimalFind);
        btnPlant = view.findViewById(R.id.btn_PlantFind);
        btnAnimal.setOnClickListener(this);
        btnPlant.setOnClickListener(this);

        return view;
    }

    public void setImg(Photo photo) {
        this.photo = photo;
    }

    public void setBitmapImg(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
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
    public void clearImage(){
        photo=null;
        new Thread(){
            @Override
            public void run() {
                    imageView.setImageDrawable(null);
            }
        }.start();
    }

    public void deletPhoto() {
//        if (photo == null) {
//            Toast.makeText(getActivity(), "请先选择图片!", Toast.LENGTH_SHORT);
//        } else {
//            new Thread() {
//                @Override
//                public void run() {
//                    OkHttpClient client = new OkHttpClient();
//                    String deleteUrl = URL.deleteUrl;
//                    RequestBody requestBody = new FormBody
//                            .Builder()
//                            .add("id", photo.getId())
//                            .build();
//                    final Request request = new Request.Builder()
//                            .url(URL.deleteUrl)
//                            .post(requestBody)
//                            .build();
//                    Response response = null;
//                    //发起请求
//                    client.newCall(request).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            makeToast("删除失败!");
//                        }
//                        @Override
//                        public void onResponse(Call call, Response response) {
//                            //获得返回response.body()
//                            //清空图片框显示
//                            Message message = new Message();
//                            message.what = COMPLETED;
//                            handler.sendMessage(message);
//                            //更新列表数据
//                            SourceUtil.getImageList();
//                            FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
//                            for(int i=0;i<fragmentManager.getFragments().size();i++){
//                                if(fragmentManager.getFragments().get(i).getArguments()!=null){
//                                    String a= (String) fragmentManager.getFragments().get(i).getArguments().get("text");
//                                    if(a=="列表"){
//                                        ListFragment listFragment= (ListFragment) fragmentManager.getFragments().get(i);
//                                        listFragment.updateData("id");
//                                    }
//                                }
//                            }
//                            makeToast("删除成功!");
//                        }
//                    });
//
//                }
//            }.start();
//        }
    }


    @Override
    public void onClick(View v) {
        if (photo== null) {
            makeToast("请先选择图片!");
            return;
        }
        switch (v.getId()) {
            case R.id.btn_AnimalFind:
                new FindThread(photo.getImagesByte(), 1, this).start();
                break;
            case R.id.btn_PlantFind:
                new FindThread(photo.getImagesByte(), 2, this).start();
                break;
        }
    }
}
