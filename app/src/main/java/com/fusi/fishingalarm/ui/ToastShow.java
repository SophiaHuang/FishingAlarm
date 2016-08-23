package com.fusi.fishingalarm.ui;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by user90 on 2016/7/14.
 */
public class ToastShow {
    public static void  showToastCenter(Context context, String content) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public static void  showToastCenterLong(Context context, String content) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
