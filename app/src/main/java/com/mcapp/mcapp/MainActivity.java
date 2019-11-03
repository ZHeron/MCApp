package com.mcapp.mcapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.heima.tabview.library.TabView;
import com.heima.tabview.library.TabViewChild;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    IndexFragment indexFrag = null;
    ListFragment listFrag = null;
    LastFragment lastFrag = null;
    TabView tabView = null;
    List<TabViewChild> tabViewChildList = null;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabView = findViewById(R.id.tabView);
        indexFrag = IndexFragment.newInstance("首页");
        listFrag = ListFragment.newInstance("列表");
        lastFrag = LastFragment.newInstance("未知");
        tabViewChildList = new ArrayList<>();
        TabViewChild tabViewChild01 = new TabViewChild(R.drawable.tab01_sel, R.drawable.tab01_unsel, "首页", indexFrag);
        TabViewChild tabViewChild02 = new TabViewChild(R.drawable.tab02_sel, R.drawable.tab02_unsel, "列表", listFrag);
        TabViewChild tabViewChild03 = new TabViewChild(R.drawable.tab03_sel, R.drawable.tab03_unsel, "未知", lastFrag);

        tabViewChildList.add(tabViewChild01);
        tabViewChildList.add(tabViewChild02);
        tabViewChildList.add(tabViewChild03);
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

    public void jump(Integer Img) {
        lastFrag.setImg(Img);
//        //删除所有add的fragment
        FragmentTransaction transaction =fragmentManager
                .beginTransaction();
        if(lastFrag.isAdded()){
            transaction.remove(lastFrag);
        }
        transaction.remove(listFrag).commitNow();
        tabView.setTabViewDefaultPosition(2);
        tabView.setTabViewChild(tabViewChildList,fragmentManager);


    }

}
