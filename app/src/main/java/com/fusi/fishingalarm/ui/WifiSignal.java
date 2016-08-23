package com.fusi.fishingalarm.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fusi.fishingalarm.R;

/**
 * Created by user90 on 2016/5/31.
 */
public class WifiSignal extends FrameLayout {

    private static String TAG = "WifiSignal";

    ImageView mWifi1;
    //    信号强度，1->wifi1图片显示，2->wifi2图片显示，3->wifi3图片显示，4->wifi4图片显示,默认是1
    int signal = 1;
    //标志
    int scale1 = 50;
    int scale2 = 70;
    int scale3 = 100;
    int scale4 = 100;


    public WifiSignal(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.ui_wifi_signal, this);
        initView();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WifiSignal);
        signal = array.getInteger(R.styleable.WifiSignal_signal, 1);
        showSignal();

        array.recycle();

    }


    private void initView() {
        mWifi1 = (ImageView) findViewById(R.id.wifi1);
    }

    public void showSignal() {
        switch (signal) {
            case 4:
                mWifi1.setImageDrawable(getResources().getDrawable(R.drawable.img_wifi_4));
                break;
            case 2:
                mWifi1.setImageDrawable(getResources().getDrawable(R.drawable.img_wifi_2));
                break;
            case 3:
                mWifi1.setImageDrawable(getResources().getDrawable(R.drawable.img_wifi_3));
                break;
            default:
                mWifi1.setImageDrawable(getResources().getDrawable(R.drawable.img_wifi_1));
        }
    }

    public void setSignal(int setSignal) {

        if (setSignal>100){
            signal = 1;
        }else if (setSignal>80){
            signal = 2;
        }else if (setSignal>50){
            signal = 3;
        }else {
            signal = 4;
        }
        showSignal();
    }
}
