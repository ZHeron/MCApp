package com.mcapp.mcapp.utils;

import android.util.Base64;
import android.util.Log;

import com.mcapp.mcapp.constant.URL;
import com.mcapp.mcapp.model.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SourceUtil {
    public static List<Photo> photos = new ArrayList<>();
    public static List<String> groups = new ArrayList<>() ;
    public static String FaceToken;
    public static String OauthToken;
    public static boolean show=false;
    public static byte[] imagesByte;
    public static void getImageList() {

        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String listUrl = URL.listUrl;
                final Request request = new Request.Builder()
                        .url(listUrl)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(responseData);
                        //防止重复刷新数据
                        photos.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Photo photo = new Photo();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            photo.setId(jsonObject.getString("_id"));
                            photo.setName(jsonObject.getString("name"));
                            photo.setContent(jsonObject.getString("content"));
                            photo.setCreateTime(jsonObject.getInt("createTime"));
                            //将字符串base64转byte[]
                            photo.setImagesByte(Base64.decode(photo.getContent(), Base64.DEFAULT));
                            photos.add(photo);
                        }
                        Log.d("获取图片数据", "成功");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public static void getGroupList() {
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String listUrl = URL.faceGroupUrl;
                final Request request = new Request.Builder()
                        .url(listUrl)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseData);
                    //防止重复刷新数据
                    groups.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        groups.add(jsonArray.getString(i));
                    }
                    Log.d("获取用户组", "成功");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public static void getTokens() {
        //获取人脸识别token
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token";
                RequestBody requestBody = new FormBody
                        .Builder()
                        .add("grant_type", "client_credentials")
                        .add("client_id", "1M4tou1TLOhvtopY4WKk4fTG")
                        .add("client_secret", "HaToOWRndf6vUQ0q92KDdZQkRTOTpzra")
                        .build();
                final Request request = new Request.Builder()
                        .url(tokenUrl)
                        .post(requestBody)
                        .build();
                Response response = null;
                //发起请求
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("获取人脸识别Token", "失败" + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        //获得返回response.body()
                        Log.d("获取人脸识别Token", "成功");
                        try {
                            String responseData = response.body().string();
                            JSONObject jsonObject=new JSONObject(responseData);
                            FaceToken=jsonObject.getString("access_token");
                            Log.d("获取人脸识别Token", FaceToken);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }.start();

        //获取动植物识别token
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token";
                RequestBody requestBody = new FormBody
                        .Builder()
                        .add("grant_type", "client_credentials")
                        .add("client_id", "6GLkKfTT9TO2esq7K177BycW")
                        .add("client_secret", "IFKbLvV4hRTXXpXGUVmpHzYqAAR4ZWtR")
                        .build();
                final Request request = new Request.Builder()
                        .url(tokenUrl)
                        .post(requestBody)
                        .build();
                Response response = null;
                //发起请求
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("获取动植物识别Token", "失败" + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        //获得返回response.body()
                        Log.d("获取动植物识别Token", "成功");
                        try {
                            String responseData = response.body().string();
                            JSONObject jsonObject=new JSONObject(responseData);
                            OauthToken=jsonObject.getString("access_token");
                            Log.d("获取动植物识别Token", OauthToken);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }.start();

    }
    public static void setPhotos(List<Photo> photos) {
        SourceUtil.photos = photos;
    }
}
