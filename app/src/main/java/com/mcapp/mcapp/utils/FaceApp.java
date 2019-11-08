package com.mcapp.mcapp.utils;

import android.util.Base64;
import android.util.Log;

import com.mcapp.mcapp.FaceAddActivity;
import com.mcapp.mcapp.FaceSearchActivity;
import com.mcapp.mcapp.constant.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;

public class FaceApp {
    static String detectUrl = "https://aip.baidubce.com/rest/2.0/face/v3/detect?access_token=";
    static String addUrl = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token=";
    static String searchUrl = "https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=";

    static String access_token = "";

    static {
        access_token = SourceUtil.FaceToken;
        detectUrl += access_token;
        addUrl += access_token;
        searchUrl += access_token;
    }

    public static String toBase64(InputStream inputStream) {
        String s = null;
        try {
            byte[] buf = new byte[inputStream.available()];
            inputStream.read(buf);
            s = Base64.encodeToString(buf, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String detectFaceWithUrl(String faceUrl) {
        String rs = "";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("image_type", "URL")
                .add("image", faceUrl)
                .build();
        Request request = new Request.Builder()
                .url(detectUrl)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try {
            Response resp = client.newCall(request).execute();
            rs = resp.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rs;
    }


    public static String detectFaceWithBase64(String Base64) {
        String rs = "";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("image_type", "BASE64")
                .add("image", Base64)
                .build();
        Request request = new Request.Builder()
                .url(detectUrl)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try {
            Response resp = client.newCall(request).execute();
            rs = resp.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rs;
    }

    //人脸库增加
    public static void addFaceWithBase64(final String Base64, final String groupId, final String userId, final String userInfo, final FaceAddActivity faceAddActivity) {
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("image_type", "BASE64")
                        .add("image", Base64)
                        .add("group_id", groupId)
                        .add("user_id", userId)
                        .add("user_info", userInfo)
                        .build();
                Request request = new Request.Builder()
                        .url(addUrl)
                        .header("Content-Type", "application/json")
                        .post(body)
                        .build();
                try {
                    Response resp = client.newCall(request).execute();
                    String rs = resp.body().string();
                    JSONObject jsonObject = new JSONObject(rs);
                    if (jsonObject != null) {
                        if (jsonObject.getString("error_msg").equals("pic not has face")) {
                            faceAddActivity.makeToast("该图片没有人脸");
                        } else if (jsonObject.getString("error_msg").equals("SUCCESS")) {
                            addInServer(Base64, groupId, userId, userInfo, faceAddActivity);
                            faceAddActivity.makeToast("添加成功");
                        } else if (jsonObject.getString("error_msg").equals("face is already exist")) {
                            faceAddActivity.makeToast("该人脸已经存在");
                        } else {
                            faceAddActivity.makeToast("参数错误");
                        }
                    }
                    Log.d("新增人脸", rs);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //服务器增加用户信息
    public static void addInServer(final String Base64, final String groupId, final String userId, final String userInfo, final FaceAddActivity faceAddActivity) {
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody
                        .Builder()
                        .add("groupId", groupId)
                        .add("userId", userId)
                        .add("userInfo", userInfo)
                        .build();
                final Request request = new Request.Builder()
                        .url(URL.faceAddUrl)
                        .post(requestBody)
                        .build();
                Response response = null;
                //发起请求
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("添加", "失败" + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        SourceUtil.getGroupList();
                        //获得返回response.body()
                        Log.d("添加", "成功");
                    }
                });
            }
        }.start();
    }

    public static void searchFaceWithBase64(final String Base64, final String groupIdList, final FaceSearchActivity faceSearchActivity) {
        new Thread() {
            @Override
            public void run() {
                String rs = "";
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("image_type", "BASE64")
                        .add("image", Base64)
                        .add("group_id_list", groupIdList)
                        .build();
                Request request = new Request.Builder()
                        .url(searchUrl)
                        .header("Content-Type", "application/json")
                        .post(body)
                        .build();

                try {
                    Response resp = client.newCall(request).execute();
                    rs = resp.body().string();
                    JSONObject jsonObject = new JSONObject(rs);
                    if (jsonObject != null) {
                        if (jsonObject.getString("error_msg").equals("pic not has face")) {
                            faceSearchActivity.makeToast("该图片没有人脸");
                        } else if (jsonObject.getString("error_msg").equals("SUCCESS")) {
                            JSONObject res= (JSONObject) jsonObject.getJSONObject("result").getJSONArray("user_list").get(0);
                            faceSearchActivity.updateText(res.getString("user_id"),res.getString("user_info"),res.getString("score"));
                            faceSearchActivity.makeToast("识别成功");
                        } else {
                            faceSearchActivity.makeToast("参数错误");
                        }
                    }
                    Log.d("搜索结果", rs);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}

