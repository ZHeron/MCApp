package com.mcapp.mcapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.heima.tabview.library.TabView;
import com.heima.tabview.library.TabViewChild;
import com.mcapp.mcapp.constant.URL;
import com.mcapp.mcapp.model.Photo;
import com.mcapp.mcapp.utils.SourceUtil;

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

public class MainActivity extends AppCompatActivity {
    IndexFragment indexFrag = null;
    ListFragment listFrag = null;
    FaceFragment faceFrag = null;
    LastFragment lastFrag = null;
    TabView tabView = null;
    List<TabViewChild> tabViewChildList = null;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //首先去请求图片列表数据
        SourceUtil.getImageList();
        //首先去请求用户组列表数据
        SourceUtil.getGroupList();

        SourceUtil.getTokens();
        tabView = findViewById(R.id.tabView);
        indexFrag = IndexFragment.newInstance("首页");
        listFrag = ListFragment.newInstance("列表");
        faceFrag = FaceFragment.newInstance("人脸识别");
        lastFrag = LastFragment.newInstance("其他识别");
        tabViewChildList = new ArrayList<>();
        TabViewChild tabViewChild01 = new TabViewChild(R.drawable.tab01_sel, R.drawable.tab01_unsel, "首页", indexFrag);
        TabViewChild tabViewChild02 = new TabViewChild(R.drawable.tab03_sel, R.drawable.tab03_unsel, "列表", listFrag);
        TabViewChild tabViewChild03 = new TabViewChild(R.drawable.tab05_sel, R.drawable.tab05_unsel, "人脸识别", faceFrag);
        TabViewChild tabViewChild04 = new TabViewChild(R.drawable.tab02_sel, R.drawable.tab02_unsel, "其他识别", lastFrag);


        tabViewChildList.add(tabViewChild01);
        tabViewChildList.add(tabViewChild02);
        tabViewChildList.add(tabViewChild03);
        tabViewChildList.add(tabViewChild04);
        fragmentManager = getSupportFragmentManager();
        tabView.setTabViewChild(tabViewChildList, fragmentManager);
        tabView.setOnTabChildClickListener(new TabView.OnTabChildClickListener() {
            @Override
            public void onTabChildClick(int position, ImageView currentImageIcon, TextView currentTextView) {
//                Toast.makeText(getApplicationContext(),"position:"+position,Toast.LENGTH_SHORT).show();
            }
        });
        //配置tabView动态样式
//        tabView.setTextViewSelectedColor(Color.BLUE);
//        tabView.setTextViewUnSelectedColor(Color.BLACK);
//        tabView.setTabViewBackgroundColor(Color.YELLOW);
//        tabView.setImageViewTextViewMargin(2);
//        tabView.setTextViewSize(14);
//        tabView.setTabViewGravity(Gravity.TOP);
//        tabView.setTabViewDefaultPosition(2);
    }

    public void jump(Photo Img,String frag) {
        if(frag.equals("last")){
            lastFrag.setImg(Img);
//        //删除所有add的fragment
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction();
            if (lastFrag.isAdded()) {
                transaction.remove(lastFrag);
            }
            transaction.remove(listFrag).commitNow();
            tabView.setTabViewDefaultPosition(3);
            tabView.setTabViewChild(tabViewChildList, fragmentManager);
        }else if(frag=="face_add"){
            faceFrag.setImg(Img);
//        //删除所有add的fragment
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction();
            if (faceFrag.isAdded()) {
                transaction.remove(faceFrag);
            }
            transaction.remove(listFrag).commitNow();
            tabView.setTabViewDefaultPosition(2);
            tabView.setTabViewChild(tabViewChildList, fragmentManager);
        }
    }


    //侧滑按钮监听事件
    public void faceFind(View view) {
        String frag = (String) view.getTag(R.id.face_first);
        Photo p = (Photo) view.getTag(R.id.face_second);
        jump(p,frag);
        Log.d("侧滑图片","跳转");
    }

    public void othersFind(View view) {
        String frag = (String) view.getTag(R.id.last_first);
        Photo p = (Photo) view.getTag(R.id.last_second);
        jump(p,frag);
        Log.d("侧滑图片","跳转");
    }

    public void delete(View view) {
        String id = (String) view.getTag();
        listFrag.delete(id);
        Log.d("侧滑图片位置", id + "");
    }


    public void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

//        users.remove(index);
//        adapter.notifyDataSetChanged();
}
