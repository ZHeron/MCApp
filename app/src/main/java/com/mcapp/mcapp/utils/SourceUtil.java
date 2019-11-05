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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SourceUtil {
    public static List<Photo> photos = new ArrayList<>();
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
}
