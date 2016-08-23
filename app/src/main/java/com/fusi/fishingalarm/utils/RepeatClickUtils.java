package com.fusi.fishingalarm.utils;

/**
 * Created by dev on 2016/2/4.
 */
public class RepeatClickUtils {
    private static long lastClickTime;

    public static boolean isFastDoubleClick(long timeInterval) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < timeInterval) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

}
