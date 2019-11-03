package com.mcapp.mcapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;


public class IndexFragment extends Fragment implements View.OnClickListener {
    private Uri imgUri; //记录拍照后的照片文件的地址(临时文件)
    private ImageView imgView;
    private Button cameraBtn;
    private Button galleryBtn;
    private Button uploadBtn;
    private String uploadFileName;
    private byte[] fileBuf;
//    private String uploadUrl = "http://114.55.36.41:8000/upload";
    private String uploadUrl = "http://192.168.1.103:8000/index/upload";
//    private String uploadUrl = "http://192.168.43.2:8000/index/upload";
    public static IndexFragment newInstance(String text) {
        IndexFragment fragmentCommon = new IndexFragment();
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
        View view =inflater.inflate(R.layout.fragment_index, container, false);
        imgView=view.findViewById(R.id.img_1);
        cameraBtn=view.findViewById(R.id.btn_cancel);
        galleryBtn=view.findViewById(R.id.btn_cance2);
        uploadBtn=view.findViewById(R.id.btn_cance3);
        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        return view;
    }

    //拍照
    public void photo(View view) throws Exception{
        //删除并创建临时文件，用于保存拍照后的照片
        //android 6以后，写Sdcard是危险权限，需要运行时申请，但此处使用的是"关联目录"，无需！
        uploadFileName="IMG_";
        long timeStamp = System.currentTimeMillis();
        uploadFileName=uploadFileName+timeStamp+".jpg";
        File outImg=new File(getContext().getExternalCacheDir(),uploadFileName);
        if(outImg.exists()) outImg.delete();
        outImg.createNewFile();

        //复杂的Uri创建方式
        if(Build.VERSION.SDK_INT>=24)
            //这是Android 7后，更加安全的获取文件uri的方式（需要配合Provider,在Manifest.xml中加以配置）
            imgUri= FileProvider.getUriForFile(getActivity(),"com.myapp.fileprovider",outImg);
        else
            imgUri= Uri.fromFile(outImg);
        //利用actionName和Extra,启动《相机Activity》
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        startActivityForResult(intent,1);

    }
    //拍照、相册回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case 1:
                //此时，相机拍照完毕
                if (resultCode == RESULT_OK) {
                    try {
                        InputStream inputStream = getContext().getContentResolver().openInputStream(imgUri);
                        fileBuf=convertToBytes(inputStream);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
                        imgView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                handleSelect(data);
        }
    }
    //申请权限调用相册
    private void gallery() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        //进行sdcard的读写请求
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else {
            openGallery(); //打开相册，进行选择
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(getActivity(), "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
                }
        }
    }

    //打开相册,进行照片的选择
    private void openGallery() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    //选择后照片的读取工作
    private void handleSelect(Intent intent) {
        if(intent==null){
            return;
        }
        Cursor cursor = null;
        Uri uri = intent.getData();
        cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            uploadFileName = cursor.getString(columnIndex);
        }
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            fileBuf=convertToBytes(inputStream);
            Bitmap bitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
            imgView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
    }

    private byte[] convertToBytes(InputStream inputStream) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return  out.toByteArray();
    }


    //文件上传的处理
    public void upload(View view) {
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                if(fileBuf==null){
                    Looper.prepare();
                    Toast.makeText(getActivity(), "请先选择图片！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }
                //上传文件域的请求体部分
                RequestBody formBody = RequestBody
                        .create(fileBuf, MediaType.parse("image/jpeg"));
                //整个上传的请求体部分（普通表单+文件上传域）
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("title", "Square Logo")
                        //filename:avatar,originname:abc.jpg
                        .addFormDataPart("avatar", uploadFileName, formBody)
                        .build();
                Request request = new Request.Builder()
                        .url(uploadUrl)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    Looper.prepare();
                    Toast.makeText(getActivity(), "上传成功！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    Log.i("数据", response.body().string() + "....");
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }

    //打开相机、相册
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_cancel:
                try {
                    photo(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_cance2:
                gallery();
                break;
            case R.id.btn_cance3:
                upload(v);
                break;
        }
    }


}
