package com.mcapp.mcapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcapp.mcapp.adapter.ImageAdapter;
import com.mcapp.mcapp.model.MyViewData;
import com.mcapp.mcapp.model.Photo;
import com.mcapp.mcapp.utils.PhotoLoader;
import com.mcapp.mcapp.utils.SourceUtil;
import com.mcapp.mcapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import indi.liyi.viewer.ImageDrawee;
import indi.liyi.viewer.ImageViewer;
import indi.liyi.viewer.ViewData;
import indi.liyi.viewer.ViewerStatus;
import indi.liyi.viewer.listener.OnItemChangedListener;
import indi.liyi.viewer.listener.OnItemLongPressListener;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ListFragment extends Fragment {
    private int[] images;
    private ImageViewer imageViewer;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearManager;
    private ImageAdapter adapter;

    private Point mScreenSize;
//    private List<byte[]> mImgList=new ArrayList<>();
    private List<Photo> photosList;
    private List<ViewData> mVdList;
    private int mStatusBarHeight;

    public static ListFragment newInstance(String text) {
        ListFragment fragmentCommon = new ListFragment();
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
        View view = inflater.inflate(R.layout.photo_recycler, container, false);
        imageViewer = view.findViewById(R.id.imageViewer);
        recyclerView = view.findViewById(R.id.recyclerview);

        initData();

        linearManager = new LinearLayoutManager(getContext());
        linearManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearManager);
        adapter = new ImageAdapter();
        adapter.setData(photosList);

        imageViewer.overlayStatusBar(false).viewData(mVdList) // 数据源
                .imageLoader(new PhotoLoader())
                .playEnterAnim(true) // 是否开启进场动画，默认为true
                .playExitAnim(true) // 是否开启退场动画，默认为true
                .showIndex(true); // 是否显示图片索引，默认为true

        addListener();
        return view;
    }


    private void initData() {
        mScreenSize = Utils.getScreenSize(getContext());
        mStatusBarHeight = Utils.getStatusBarHeight(getContext());
        photosList=SourceUtil.photos;
        mVdList = new ArrayList<>();
        for (int i = 0; i < photosList.size(); i++) {
            MyViewData viewData = new MyViewData();
            viewData.setImageSrc(photosList.get(i).getImagesByte());
            viewData.setTargetX(Utils.dp2px(getContext(), 10));
            viewData.setTargetWidth(mScreenSize.x - Utils.dp2px(getContext(), 20));
            viewData.setTargetHeight(Utils.dp2px(getContext(), 200));
            viewData.setId(photosList.get(i).getId());
            mVdList.add(viewData);
        }
    }

    public void addListener() {
        adapter.setOnItemClickCallback(new ImageAdapter.OnItemClickCallback() {
            @Override
            public void onItemClick(int position, ImageView view) {
                //处理更新数据慢异常
                if(mVdList!=null&&mVdList.size()!=0){
                    mVdList.clear();
                }
                for (int i = 0; i < photosList.size(); i++) {
                    MyViewData viewData = new MyViewData();
                    viewData.setImageSrc(photosList.get(i).getImagesByte());
                    viewData.setTargetX(Utils.dp2px(getContext(), 10));
                    viewData.setTargetWidth(mScreenSize.x - Utils.dp2px(getContext(), 20));
                    viewData.setTargetHeight(Utils.dp2px(getContext(), 200));
                    viewData.setId(photosList.get(i).getId());
                    mVdList.add(viewData);
                }

                int[] location = new int[2];
                // 获取在整个屏幕内的绝对坐标
                view.getLocationOnScreen(location);
                // 去掉状态栏的高度
                mVdList.get(position).setTargetY(location[1] - mStatusBarHeight);
                imageViewer.viewData(mVdList)
                        .playEnterAnim(true) // 是否开启进场动画，默认为true
                        .playExitAnim(true) // 是否开启退场动画，默认为true
                        .showIndex(true) // 是否显示图片索引，默认为true
                        .watch(position);


            }
        });

        adapter.setOnItemLongClickCallback(new ImageAdapter.OnItemLongClickCallback(){
            @Override
            public void onItemLongClick(int position, ImageView view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.jump(photosList.get(position));
            }
        });

        recyclerView.setAdapter(adapter);
        linearManager.scrollToPositionWithOffset(0, 0);

        imageViewer.setOnItemChangedListener(new OnItemChangedListener() {
            @Override
            public void onItemChanged(int position, ImageDrawee drawee) {
                if (imageViewer.getViewStatus() == ViewerStatus.STATUS_WATCHING) {
                    int top = getTop(imageViewer.getCurrentPosition());
                    mVdList.get(imageViewer.getCurrentPosition()).setTargetY(top);
                    linearManager.scrollToPositionWithOffset(imageViewer.getCurrentPosition(), top);
                }
            }
        });
        imageViewer.setOnItemLongPressListener(new OnItemLongPressListener() {
            @Override
            public boolean onItemLongPress(int position, ImageView imageView) {
                Log.d("LongPress", "" + position);
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.jump(photosList.get(position));
                return true;
            }
        });

    }

    private int getTop(int position) {
        int top = 0;
        // 当前图片的高度
        float imgH = Float.valueOf(mVdList.get(position).getTargetHeight());
        // 图片距离 imageViewer 的上下边距
        int dis = (int) ((imageViewer.getHeight() - imgH) / 2);
        // 如果图片高度大于等于 imageViewer 的高度
        if (dis <= 0) {
            return top + dis;
        } else {
            float th1 = 0;
            float th2 = 0;
            // 计算当前图片上方所有 Item 的总高度
            for (int i = 0; i < position; i++) {
                // Utils.dp2px(this, 210) 是 Item 的高度
                th1 += Utils.dp2px(getContext(), 210);
            }
            // 计算当前图片下方所有 Item 的总高度
            for (int i = position + 1; i < photosList.size(); i++) {
                // Utils.dp2px(this, 210) 是 Item 的高度
                th2 += Utils.dp2px(getContext(), 210);
            }
            if (th1 >= dis && th2 >= dis) {
                return top + dis;
            } else if (th1 < dis) {
                return (int) (top + th1);
            } else if (th2 < dis) {
                return (int) (recyclerView.getHeight() - imgH);
            }
        }
        return 0;
    }


    public void updateData(){
        photosList=SourceUtil.photos;
        adapter.notifyDataSetChanged();
        if(mVdList!=null&&mVdList.size()!=0){
            mVdList.clear();
        }
        for (int i = 0; i < photosList.size(); i++) {
            MyViewData viewData = new MyViewData();
            viewData.setImageSrc(photosList.get(i).getImagesByte());
            viewData.setTargetX(Utils.dp2px(getContext(), 10));
            viewData.setTargetWidth(mScreenSize.x - Utils.dp2px(getContext(), 20));
            viewData.setTargetHeight(Utils.dp2px(getContext(), 200));
            viewData.setId(photosList.get(i).getId());
            mVdList.add(viewData);
        }
        imageViewer.overlayStatusBar(false).viewData(mVdList) // 数据源
                .imageLoader(new PhotoLoader())
                .playEnterAnim(true) // 是否开启进场动画，默认为true
                .playExitAnim(true) // 是否开启退场动画，默认为true
                .showIndex(true); // 是否显示图片索引，默认为true

        addListener();

    }

}
