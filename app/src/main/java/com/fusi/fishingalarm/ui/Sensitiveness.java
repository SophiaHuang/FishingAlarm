package com.fusi.fishingalarm.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.fusi.fishingalarm.R;

/**
 * Created by user90 on 2016/5/31.
 */
public class Sensitiveness extends FrameLayout {

    private static String TAG = "Sensitiveness";

    ImageView mSensitive;
    //    灵敏度，1->img_sensitiveness1图片显示，2->img_sensitiveness2图片显示
    int sensitiveness = 1;
    //标志
    int scale1 = 20;
    int scale2 = 40;
    int scale3 = 60;
    int scale4 = 80;
    int scale5 = 100;
    int scale6 = 120;
    int scale7 = 140;


    public Sensitiveness(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.ui_sensitiveness, this);
        initView();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Sensitiveness);
        sensitiveness = array.getInteger(R.styleable.Sensitiveness_sensitiveness, 1);
        showSensitiveness();

        array.recycle();

    }


    private void initView() {
        mSensitive = (ImageView) findViewById(R.id.sensitive);
    }

    public void showSensitiveness() {
        switch (sensitiveness) {
            case 8:
                mSensitive.setImageDrawable(getResources().getDrawable(R.drawable.img_sensitiveness8));
                break;
            case 7:
                mSensitive.setImageDrawable(getResources().getDrawable(R.drawable.img_sensitiveness7));
                break;
            case 6:
                mSensitive.setImageDrawable(getResources().getDrawable(R.drawable.img_sensitiveness6));
                break;
            case 5:
                mSensitive.setImageDrawable(getResources().getDrawable(R.drawable.img_sensitiveness5));
                break;
            case 4:
                mSensitive.setImageDrawable(getResources().getDrawable(R.drawable.img_sensitiveness4));
                break;
            case 3:
                mSensitive.setImageDrawable(getResources().getDrawable(R.drawable.img_sensitiveness3));
                break;
            case 2:
                mSensitive.setImageDrawable(getResources().getDrawable(R.drawable.img_sensitiveness2));
                break;
            default:
                mSensitive.setImageDrawable(getResources().getDrawable(R.drawable.img_sensitiveness1));
        }
    }

    public void setSensitiveness(int setSignal) {
        if (setSignal <= scale1) {
            sensitiveness = 1;
        } else if (setSignal > scale1 && setSignal <= scale2) {
            sensitiveness = 2;
        } else if (setSignal > scale2 && setSignal <= scale3) {
            sensitiveness = 3;
        } else if (setSignal > scale3 && setSignal <= scale4) {
            sensitiveness = 4;
        } else if (setSignal > scale4 && setSignal <= scale5) {
            sensitiveness = 5;
        } else if (setSignal > scale5 && setSignal <= scale6) {
            sensitiveness = 6;
        } else if (setSignal > scale6 && setSignal <= scale7) {
            sensitiveness = 7;
        } else if (setSignal > scale7) {
            sensitiveness = 8;
        }
        showSensitiveness();
    }
}
