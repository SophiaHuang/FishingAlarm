package com.fusi.fishingalarm.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.UILApplication;
import com.fusi.fishingalarm.bean.ActivityCollector;

import solid.ren.skinlibrary.base.SkinBaseActivity;

/**
 * Created by user90 on 2016/6/13.
 */
public class BaseActivity extends SkinBaseActivity {

    String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_base);
        ActivityCollector.addActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }


}
