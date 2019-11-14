package com.mcapp.mcapp;

import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mcapp.mcapp.model.Photo;
import com.mcapp.mcapp.utils.FaceApp;
import com.mcapp.mcapp.utils.SourceUtil;
import com.mcapp.mcapp.widget.EditableSpinner;

import java.util.List;

public class FaceSearchActivity extends AppCompatActivity implements View.OnClickListener{
    private Spinner groupIdSpinner;
    private EditText userIdEdit;
    private EditText userInfoEdit;
    private EditText userScoreEdit;
    private Button btnConfirm;
    private Button btnCancel;

    private String groupId;
    private String userId;
    private String userInfo;
    private String imageBase64;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_search);
        groupIdSpinner=findViewById(R.id.groupId2);
        userIdEdit=findViewById(R.id.userId2);
        userInfoEdit=findViewById(R.id.userInfo2);
        userScoreEdit=findViewById(R.id.userScore2);
        btnConfirm=findViewById(R.id.btn_confirm2);
        btnCancel=findViewById(R.id.btn_cancel2);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        //数据处理
        if (getIntent().getStringExtra("id") != null){
            List<Photo> lists= SourceUtil.photos;
            for(Photo p:lists){
                if(p.getId().equals(getIntent().getStringExtra("id"))){
                    imageBase64 = p.getContent();
                }
            }
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, SourceUtil.groups);  //创建一个数组适配器
        groupIdSpinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_confirm2:
                groupId=groupIdSpinner.getSelectedItem().toString();
                FaceApp.searchFaceWithBase64(imageBase64,groupId,this);
                break;
            case R.id.btn_cancel2:
                new Thread(){
                    public void run() {
                        try{
                            Instrumentation inst = new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                        }
                        catch (Exception e) {
                            Log.e("Exception when onBack", e.toString());
                        }
                    }
                }.start();
                break;
        }
    }

    public void makeToast(final String s) {
        final FaceSearchActivity faceSearchActivity =this;
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(faceSearchActivity, s, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
    }
    public void updateText(String userId,String userInfo,String score){
        Looper.prepare();
        userIdEdit.setText(userId);
        userInfoEdit.setText(userInfo);
        userScoreEdit.setText(score);
        Looper.loop();
    }
}
