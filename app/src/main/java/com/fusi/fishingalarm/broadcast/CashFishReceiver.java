package com.fusi.fishingalarm.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fusi.fishingalarm.activity.MainActivity;
import com.fusi.fishingalarm.bean.ActivityCollector;

/**
 * Created by user90 on 2016/8/4.
 */
public class CashFishReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityCollector.finishAll();//销毁所有活动
//        Intent intentToMain=new Intent(context, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intentToMain);//重新启动MainActivity
    }
}
