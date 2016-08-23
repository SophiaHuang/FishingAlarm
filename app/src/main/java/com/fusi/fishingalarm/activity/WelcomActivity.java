package com.fusi.fishingalarm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.fusi.fishingalarm.R;

/**
 * Created by user90 on 2016/5/27.
 */
public class WelcomActivity extends Activity {

    private static final int TO_MAIN=1;

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==TO_MAIN){
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcom);
        mHandler.sendEmptyMessageDelayed(TO_MAIN,1000);

    }
}
