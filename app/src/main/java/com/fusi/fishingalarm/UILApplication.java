/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.fusi.fishingalarm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import solid.ren.skinlibrary.base.SkinBaseApplication;


public class UILApplication extends SkinBaseApplication {

    private static String TAG = "UILApplication";

    private static Context mContext;
    private static Context mCurrContext;
    private static Thread mThread;
    private static long mThreadID;
    private static Handler mHandler;

    public static final int PORT = 8088;
    public static final String HOST = "11.11.11.254";
    public static final int ALARM_QUERY_TIME = 5000;
    public static final int SETUP_TIME = 200;
    public static final int QUERY_TIME = 50;

    //    报警器数量
    public static byte ALARM_COUNT = 0x00;
    //    夜间模式
    public static boolean hasDayMode = false;
    //贯通整个项目
    public static Socket socket;
    public static DataOutputStream out = null;
    public static DataInputStream in = null;
    //    是否连接钓鱼报警器 wifi
    public static boolean hasConnectWifi = false;
    //    已经有服务器返回ready了
    public static boolean ISLOGIN = false;
    //成功连上socket
    public static boolean hasSucess;
    public static boolean hasShowError;
    public static int errerCount;
    //    默认吧
    public static int power = 80;
    //    3.6v,4.2v
    public static int powerMin = 360;
    public static int powerMax = 420;
    public static boolean hasCashFish = false;

    //命令
    public static int commandCount = 0;
    //设置错误次数
    public static int setErrorCount = 0;

    public static long hearTime = 50;
    public static boolean hasWifiAlter = true;

    public static int responseCount=0;

    public void onCreate() {

        super.onCreate();

        mContext = getApplicationContext();
        mThread = Thread.currentThread();
        mThreadID = mThread.getId();
        mHandler = new Handler(getMainLooper());
//        initVolley();
//        查询主报警器就绪状态
    }


//    private void initVolley() {
//        MyVolley.init(this);
//    }

    public static Context getApplication() {
        return mContext;
    }

    public static Thread getMainThread() {
        return mThread;
    }

    public static long getMainThreadId() {
        return mThreadID;
    }

    public static Handler getMainThreadHandler() {
        return mHandler;
    }

    public static String getAppVersionName() {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = getApplication().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getApplication().getPackageName(), 0);
            versionName = pi.versionName;
            // versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

//    public static void Countdowmtimer(long dodate) {
//        new CountDownTimer(dodate, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            // 计时结束
//            public void onFinish() {
//                Intent intent=new Intent(mContext, LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);
//            }
//        }.start();
//    }

    public static void resetSocket() {
        while (isServerClose(socket)) {
            try {
                socket = new Socket(HOST, PORT);
                in = new DataInputStream(socket.getInputStream()); // 获得DataInputStream对象d
                out = new DataOutputStream(socket.getOutputStream());
                commandCount = 0;//重启，重新查看状态
                hasSucess = true;
                errerCount = 0;
                hasShowError = false;
                Log.i(TAG, "resetSocket: commandCount");
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            } catch (IOException e) {
                Log.i(TAG, "正在重连....");
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     * @param socket
     * @return
     */
    public static Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }

    public static void closeSocket() {
        try {
            //关闭socket
            Log.i(TAG, "exceptionResetSocket: closeSocket: ");
            if (socket != null && socket.isConnected()) {
                out.close();
                in.close();
                socket.close();
                out = null;
                in = null;
                socket = null;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
