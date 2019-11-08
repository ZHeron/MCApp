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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mcapp.mcapp.model.Photo;
import com.mcapp.mcapp.utils.FaceApp;
import com.mcapp.mcapp.utils.SourceUtil;
import com.mcapp.mcapp.widget.EditableSpinner;

import java.util.List;

public class FaceAddActivity extends AppCompatActivity implements View.OnClickListener{
    private EditableSpinner groupIdSpinner;
    private EditText userIdEdit;
    private EditText userInfoEdit;
    private Button btnConfirm;
    private Button btnCancel;

    private String groupId;
    private String userId;
    private String userInfo;
    private String imageBase64;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_add);
        groupIdSpinner=findViewById(R.id.groupId);
        userIdEdit=findViewById(R.id.userId);
        userInfoEdit=findViewById(R.id.userInfo);
        btnConfirm=findViewById(R.id.btn_confirm);
        btnCancel=findViewById(R.id.btn_cancel);
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
        groupIdSpinner.setAdapter(adapter).setOnItemClickListener(new EditableSpinner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                groupIdSpinner.setText(adapter.getItem(position));
                // .....
                Log.d("选中组",adapter.getItem(position));
            }
        });;


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_confirm:
                groupId=groupIdSpinner.getSelectedItem();
                userId=userIdEdit.getText().toString();
                userInfo=userInfoEdit.getText().toString();
                FaceApp.addFaceWithBase64(imageBase64,groupId,userId,userInfo,this);
                break;
            case R.id.btn_cancel:
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
        final FaceAddActivity faceAddActivity =this;
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(faceAddActivity, s, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
    }
}
