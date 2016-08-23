package com.fusi.fishingalarm.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fusi.fishingalarm.R;

/**
 * Created by user90 on 2016/5/27.
 */
public class TopTitleLayout extends LinearLayout {

    private static String TAG="TopTitleLayout";

    //返回图片
    ImageView back;
    //标题
    TextView title;
    //标题
    String mTitle;
    View line;
    //是否显示横线，默认不显示
    boolean hasLine;
    //    是否要自己做finish事件
    boolean hasEditFinish;

    public TopTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.ui_top_title,this);
        back= (ImageView) findViewById(R.id.back);
        //删除
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
        title= (TextView) findViewById(R.id.title);
        line=findViewById(R.id.line);

        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.TopTitle);
        mTitle=array.getString(R.styleable.TopTitle_showTitle);
        Log.i(TAG, "TopTitleLayout: mTitle:"+mTitle);
        if (!TextUtils.isEmpty(mTitle)){
            title.setText(mTitle);
        }
        hasLine=array.getBoolean(R.styleable.TopTitle_hasLine,false);
        if (hasLine){
            line.setVisibility(VISIBLE);
        }

        array.recycle();
    }
}
