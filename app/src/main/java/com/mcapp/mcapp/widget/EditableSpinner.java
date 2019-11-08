package com.mcapp.mcapp.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;

import androidx.annotation.Nullable;

import com.mcapp.mcapp.R;

public class EditableSpinner extends LinearLayout implements AdapterView.OnItemClickListener {

    private ImageButton mImgBtnDown;
    private EditText mEtInput;
    private ListPopupWindow mListPopupWindow;
    private OnItemClickListener mOnItemClickListener;
    private ArrayAdapter mArrayAdapter;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public EditableSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.editable_spinner,this);

        mImgBtnDown = findViewById(R.id.img_btn_down);
        mEtInput = findViewById(R.id.et_input);
        mListPopupWindow = new ListPopupWindow(context);

        TypedArray attrArr = context.obtainStyledAttributes(attrs, R.styleable.EditableSpinner);
        Drawable downImage = attrArr.getDrawable(R.styleable.EditableSpinner_downBtnImage);
        float textSize = attrArr.getDimension(R.styleable.EditableSpinner_textSize, 14);
        int downBtnWidth = (int) attrArr.getDimension(R.styleable.EditableSpinner_downBtnWidth, dp2px( 40));
        int textColor = attrArr.getColor(R.styleable.EditableSpinner_textColor, 0x000000);
        int downBtnBgColor = attrArr.getColor(R.styleable.EditableSpinner_downBtnBackground, 0x00ffffff);

        mImgBtnDown.setImageDrawable(downImage);
        mImgBtnDown.setBackgroundColor(downBtnBgColor);
        LinearLayout.LayoutParams lp = (LayoutParams) mImgBtnDown.getLayoutParams();
        lp.width = downBtnWidth;
        mImgBtnDown.setLayoutParams(lp);

        mImgBtnDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListPopupWindow.show();
            }
        });

        mEtInput.setTextSize(px2dp(textSize));
        mEtInput.setTextColor(textColor);
    }

    public EditableSpinner setAdapter(ArrayAdapter<String> adapter) {

        mArrayAdapter = adapter;

        mListPopupWindow.setAdapter(adapter);
        mListPopupWindow.setAnchorView(mEtInput);
        mListPopupWindow.setModal(true);
        mListPopupWindow.setOnItemClickListener(this);
        return this;
    }

    public EditableSpinner setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }

    public String getSelectedItem() {
        return mEtInput.getText().toString();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListPopupWindow.dismiss();
        mEtInput.setText((CharSequence) mArrayAdapter.getItem(position));
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(position);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dp(float pxValue) {
        return (pxValue / Resources.getSystem().getDisplayMetrics().density);
    }
    public void setText(String s){
        mEtInput.setText(s);
    }
}