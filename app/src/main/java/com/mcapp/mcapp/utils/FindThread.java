package com.mcapp.mcapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.mcapp.mcapp.FaceFragment;
import com.mcapp.mcapp.LastFragment;
import com.mcapp.mcapp.MainActivity;
import com.mcapp.mcapp.constant.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FindThread extends Thread {
    private Integer findType;
    private byte[] imageData;
    private byte[] imageData2;
    private LastFragment lastFragment;
    private FaceFragment faceFragment;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public FindThread(byte[] imageData, Integer findType, LastFragment lastFragment) {
        this.imageData = imageData;
        this.findType = findType;
        this.lastFragment = lastFragment;
    }

    public FindThread(byte[] imageData, Integer findType, FaceFragment faceFragment) {
        this.imageData = imageData;
        this.findType = findType;
        this.faceFragment = faceFragment;
    }

    public FindThread(byte[] imageData1, byte[] imageData2, Integer findType, FaceFragment faceFragment) {
        this.imageData = imageData1;
        this.imageData2 = imageData2;
        this.findType = findType;
        this.faceFragment = faceFragment;
    }

    public void run() {
//        OkHttpClient client = new OkHttpClient();
//        //上传文件域的请求体部分
//        RequestBody formBody = RequestBody
//                .create(imageData, MediaType.parse("image/jpeg"));
//        //整个上传的请求体部分（普通表单+文件上传域）
//        String Token = "";
//        if (findType == 0) {
//            Token = SourceUtil.FaceToken;
//        } else {
//            Token = SourceUtil.OauthToken;
//        }
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("findType", findType + "")
//                .addFormDataPart("Token", Token)
//                //filename:avatar,originname:abc.jpg
//                .addFormDataPart("avatar", "findImage", formBody)
//                .build();
//        final Request request = new Request.Builder()
//                .url(URL.findUrl)
//                .post(requestBody)
//                .build();
//        //发起请求
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("识别结果", "失败" + e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) {
//                //获得返回response.body()
//                try {
//                    String result = response.body().string();
//                    JSONObject jsonObject = new JSONObject(result);
//                    //处理人脸识别返回
//                    if (findType == 0) {
//                        if (jsonObject.get("error_msg").equals("SUCCESS")) {
//                            markFace(jsonObject);
//                        } else if (jsonObject.get("error_msg").equals("pic not has face_add")) {
//                            lastFragment.makeToast("没有识别到人脸!");
//                        } else if(jsonObject.get("error_msg").equals("timeout")){
//                            lastFragment.makeToast("请求超时，请稍后重试！");
//                        }else{
//                            lastFragment.makeToast("识别错误!");
//                        }
//                    } else if (findType == 1) {
//                        JSONArray results = jsonObject.getJSONArray("result");
//                        if(results.length()==0){
//                            lastFragment.makeToast("请求超时，请稍后重试！");
//                        }else if (results.getJSONObject(0).getString("name").equals("非动物")) {
//                            lastFragment.makeToast("非动物!");
//                        }else
//                            {
//                            drawText(results);
//                        }
//                    } else if (findType == 2) {
//                        JSONArray results = jsonObject.getJSONArray("result");
//                        if(results.length()==0){
//                            lastFragment.makeToast("请求超时，请稍后重试！");
//                        }else if (results.getJSONObject(0).getString("name").equals("非植物")) {
//                            lastFragment.makeToast("非植物!");
//                        } else {
//                            drawText(results);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


//        RequestBody requestBody=new FormBody
//                .Builder()
//                .add("imageData",new String(imageData))
//                .add("findType",findType+"")
//                .build();
//        final Request request = new Request.Builder()
//                .url(URL.findUrl)
//                .post(requestBody)
//                .build();
//        Response response = null;
//        //发起请求
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("识别","失败"+e);
//            }
//            @Override
//            public void onResponse(Call call, Response response){
//             //获得返回response.body()
//                Log.d("识别","成功");
//                int a=10;
//            }
//        });

        String Token = "";
        if (findType == 0 || findType == 3) {
            Token = SourceUtil.FaceToken;
        } else {
            Token = SourceUtil.OauthToken;
        }
        OkHttpClient client = new OkHttpClient();
        Request request = null;
        RequestBody requestBody = null;
        Response response = null;
        if (findType == 0) {
            String faceURL = "https://aip.baidubce.com/rest/2.0/face/v3/detect" + "?access_token=" + Token;
            requestBody = new FormBody
                    .Builder()
                    .add("image_type", "BASE64")
                    .add("image", Base64.encodeToString(imageData, Base64.DEFAULT))
                    .add("max_face_num", "5")
                    .build();
            request = new Request.Builder()
                    .url(faceURL)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();
        } else if (findType == 1) {
            String faceURL = "https://aip.baidubce.com/rest/2.0/image-classify/v1/animal" + "?access_token=" + Token;
            requestBody = new FormBody
                    .Builder()
                    .add("image", Base64.encodeToString(imageData, Base64.DEFAULT))
                    .add("top_num", "3")
                    .build();
            request = new Request.Builder()
                    .url(faceURL)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(requestBody)
                    .build();
        } else if (findType == 2) {
            String faceURL = "https://aip.baidubce.com/rest/2.0/image-classify/v1/plant" + "?access_token=" + Token;
            requestBody = new FormBody
                    .Builder()
                    .add("image", Base64.encodeToString(imageData, Base64.DEFAULT))
                    .build();
            request = new Request.Builder()
                    .url(faceURL)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(requestBody)
                    .build();
        } else if (findType == 3) {
            String faceURL = "https://aip.baidubce.com/rest/2.0/face/v3/match" + "?access_token=" + Token;

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject1 = new JSONObject();
            JSONObject jsonObject2 = new JSONObject();
            try {
                jsonObject1.put("image", Base64.encodeToString(imageData, Base64.DEFAULT));
                jsonObject1.put("image_type", "BASE64");
                jsonArray.put(jsonObject1);
                jsonObject2.put("image", Base64.encodeToString(imageData2, Base64.DEFAULT));
                jsonObject2.put("image_type", "BASE64");
                jsonArray.put(jsonObject2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            requestBody=RequestBody.create(JSON,jsonArray.toString());
            request = new Request.Builder()
                    .url(faceURL)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();
        }
        //发起请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("识别结果", "失败" + e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                //获得返回response.body()
                try {
                    String result = response.body().string();
                    JSONObject jsonObject = new JSONObject(result);
                    //处理人脸识别返回
                    if (findType == 0) {
                        if (jsonObject.get("error_msg").equals("SUCCESS")) {
                            markFace(jsonObject);
                        } else if (jsonObject.get("error_msg").equals("pic not has face")) {
                            faceFragment.makeToast("没有识别到人脸!");
                        } else if (jsonObject.get("error_msg").equals("timeout")) {
                            faceFragment.makeToast("请求超时，请稍后重试！");
                        } else {
                            faceFragment.makeToast("识别错误!");
                        }
                    } else if (findType == 1) {
                        JSONArray results = jsonObject.getJSONArray("result");
                        if (results.length() == 0) {
                            lastFragment.makeToast("请求超时，请稍后重试！");
                        } else if (results.getJSONObject(0).getString("name").equals("非动物")) {
                            lastFragment.makeToast("非动物!");
                        } else {
                            drawText(results);
                        }
                    } else if (findType == 2) {
                        JSONArray results = jsonObject.getJSONArray("result");
                        if (results.length() == 0) {
                            lastFragment.makeToast("请求超时，请稍后重试！");
                        } else if (results.getJSONObject(0).getString("name").equals("非植物")) {
                            lastFragment.makeToast("非植物!");
                        } else {
                            drawText(results);
                        }
                    }if(findType == 3){
                        if (jsonObject.get("error_msg").equals("SUCCESS")) {
                            markScore(jsonObject);
                        } else if (jsonObject.get("error_msg").equals("pic not has face")) {
                            faceFragment.makeToast("没有识别到人脸!");
                        } else if (jsonObject.get("error_msg").equals("timeout")) {
                            faceFragment.makeToast("请求超时，请稍后重试！");
                        } else {
                            faceFragment.makeToast("识别错误!");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void markScore(JSONObject jsonObject) throws JSONException {
//        Log.d("人脸检测", jsonObject.toString());
        JSONObject res = jsonObject.getJSONObject("result");
        if (res != null) {
            double score = res.getDouble("score");
            faceFragment.setScore(score);
        }

    }

    public void markFace(JSONObject jsonObject) throws JSONException {
//        Log.d("人脸检测", jsonObject.toString());
        JSONObject res = jsonObject.getJSONObject("result");
        if (res != null) {
            int face_num = res.getInt("face_num");
            JSONArray face_list = res.getJSONArray("face_list");
            Log.d("人脸检测", face_list.toString());
            double[][] location = new double[face_num][4];
            for (int i = 0; i < face_list.length(); i++) {
                JSONObject loc = face_list.getJSONObject(i).getJSONObject("location");
                location[i][0] = loc.getDouble("left");
                location[i][1] = loc.getDouble("top");
                location[i][2] = location[i][0] + loc.getDouble("width");
                location[i][3] = location[i][1] + loc.getDouble("height");
            }
            drawRect(location);
        }

    }

    public void drawRect(double[][] location) {
        Bitmap photo = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        Bitmap tempBitmap = photo.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(tempBitmap);

        //图像上画矩形
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);//不填充
        paint.setStrokeWidth(10);  //线的宽度
        for (int i = 0; i < location.length; i++) {
            canvas.drawRect((float) location[i][0], (float) location[i][1], (float) location[i][2], (float) location[i][3], paint);
        }
        faceFragment.setBitmapImg(tempBitmap);
    }

    public void drawText(JSONArray results) throws JSONException {
        int num = 3;
        String names[] = new String[num];
        for (int i = 0; i < num; i++) {
            names[i] = results.getJSONObject(i).getString("name");
        }
        Bitmap photo = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        Bitmap tempBitmap = photo.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(tempBitmap);

        Paint paint = new Paint();
        // 抗锯齿
        paint.setAntiAlias(true);
        // 防抖动
        paint.setDither(true);
        paint.setTextSize(50);
        paint.setColor(Color.parseColor("#ff0000"));
        float x = (tempBitmap.getWidth() / 5);
        float y = (tempBitmap.getHeight() / 2);
        for (int i = 0; i < num; i++) {
            canvas.drawText((i + 1) + "." + names[i], x, y, paint);
            y += paint.descent() - paint.ascent();
        }


        lastFragment.setBitmapImg(tempBitmap);
    }
}
