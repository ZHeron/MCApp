package com.mcapp.mcapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class LastFragment extends Fragment {
    ImageView imageView;
    Integer Img;
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
        View view =inflater.inflate(R.layout.fragment_last, container, false);
        imageView=view.findViewById(R.id.img_2);
        if(Img!=null){
            imageView.setImageResource(Img);
        }
        return view;
    }
    public void setImg(Integer Img){
        this.Img=Img;
    }

}
