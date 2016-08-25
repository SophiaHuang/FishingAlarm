package com.fusi.fishingalarm.activity;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.UILApplication;
import com.fusi.fishingalarm.bean.ActivityCollector;
import com.fusi.fishingalarm.ui.ToastShow;
import com.fusi.fishingalarm.utils.RepeatClickUtils;
import com.fusi.fishingalarm.utils.SharedPrefrenceUtils;
import com.fusi.fishingalarm.utils.StringUtils;
import com.lsd.atcmd.DeviceListActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import solid.ren.skinlibrary.base.SkinBaseActivity;
import solid.ren.skinlibrary.listener.ILoaderListener;
import solid.ren.skinlibrary.loader.SkinManager;


public class MainActivity extends SkinBaseActivity {

    private static String TAG = "MainActivity";

    public static final String CASH_SUCC = "com.fusi.fishingalarm.cash.succ";

    //    报警器显示
    TextView alrm;
    //就绪成功
    private static final int SUCESS = 1;
    private static final int CATCH_FISH = 2;
    private static final int FAIL = 3;
    private static final int NIGHT_MODE_RESULT = 4;
    private static final int OPEN_CAMERA_PERMISSIONS = 5;
    //alarm准备就绪
    byte[] buf = new byte[5];
    //报警器个数
    byte[] alarmCount = new byte[5];
    //报警器状态查询
    byte[] alarmState = new byte[5];
    //夜间模式查询
    byte[] bufNightMode = new byte[5];
    //电源查询
    byte[] bufPower = new byte[5];
    //重启服务器
    byte[] commandReset = new byte[5];

    private InitSocketThread initSocketThread = new InitSocketThread();

    ReplyThread rThread = new ReplyThread();

    byte[] Total = new byte[128];
    int StartPoint = 0;
    int EndPoint = 0;

    //为销毁线程而用
    boolean hasRunningReply = true;
    //中鱼音乐提醒
    MediaPlayer mMediaPlayer = null;
    //中鱼参数
    List cashList = new ArrayList();
    //中鱼振动或音乐时长,10s
    long effectDuration = 10000;
    //    闪光灯
    Camera.Parameters parameter;
    private Camera camera;
    int cashCountIndex = 0;
    RelativeLayout bg;

    //    成功连接后数据的心跳连接
    Timer heartBeatTimer = new Timer();
    TimerTask heartBeatTask = new TimerTask() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (UILApplication.hasSucess && UILApplication.in != null) {
//                UILApplication.responseCount++;
//                Log.i(TAG, "run: UILApplication.responseCount:" + UILApplication.responseCount);
//                if (UILApplication.responseCount >= 30) {
//                    exceptionResetSocket();
//                    return;
//                }
                switch (UILApplication.commandCount) {
                    case 0:
                        try {
                            Log.i(TAG, "Function: to send 01");
                            UILApplication.out.write(SumCheck(buf));
                            UILApplication.out.flush();

                        } catch (IOException e) {
                            exceptionResetSocket();
                            e.printStackTrace();
                        }

                        break;
                    case 1:

                        UILApplication.setErrorCount = 0;
                        //报警器数量
                        try {
                            Log.i(TAG, "Function: to send 02");
                            UILApplication.out.write(SumCheck(alarmCount));
                            UILApplication.out.flush();
                        } catch (IOException e) {
                            exceptionResetSocket();
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        //报警器状态
                        try {
                            Log.i(TAG, "Function: to send 03");
                            UILApplication.out.write(SumCheck(alarmState));
                            UILApplication.out.flush();
                        } catch (IOException e) {
                            exceptionResetSocket();
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        try {
                            Log.i(TAG, "Function: to send 0A");
                            UILApplication.out.write(SumCheck(bufNightMode));
                            UILApplication.out.flush();
                        } catch (IOException e) {
                            exceptionResetSocket();
                            e.printStackTrace();
                        }

                        break;
                    case 4:
                        try {
                            Log.i(TAG, "Function: to send 07");
                            UILApplication.out.write(SumCheck(bufPower));
                            UILApplication.out.flush();
                        } catch (IOException e) {
                            exceptionResetSocket();
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
    };

    Handler mHandler = new Handler() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCESS:
                    ToastShow.showToastCenter(getBaseContext(), "准备监测中鱼情况");
//                  下次出错则可以显示提示
                    UILApplication.errerCount = 0;
                    UILApplication.hasShowError = false;
                    break;
                case CATCH_FISH:
                    Log.i(TAG, "handleMessage:show text cashList.size():"+cashList.size());
                    if (cashList.size() > 0) {
                        alrm.setVisibility(View.VISIBLE);
                        if (ActivityCollector.activities.size() >= 1) {
                            Intent intent = new Intent(CASH_SUCC);
                            sendBroadcast(intent);
                        }
                        if (cashList.get(cashCountIndex).equals(0)) {//主报警器是红色的
                            alrm.setText("主报警器");
                            alrm.setTextColor(getResources().getColor(R.color.white));
                            alrm.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_circle_white));
                            alrm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        } else {
                            alrm.setText(String.valueOf(cashList.get(cashCountIndex)));
                            alrm.setTextColor(getResources().getColor(R.color.red));
                            alrm.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_circle_red));
                            alrm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        }
                        cashCountIndex++;
                        if (cashCountIndex > cashList.size() - 1) {
                            cashCountIndex = 0;
                        }
                        mHandler.sendEmptyMessageDelayed(CATCH_FISH, 1000);
                    } else {
                        alrm.setVisibility(View.GONE);
                    }
                    break;
                case FAIL:
                    closeCashFishEffect();
                    ToastShow.showToastCenter(getBaseContext(), "抱歉，连接服务器异常");
                    UILApplication.hasShowError = true;
                    break;
                case NIGHT_MODE_RESULT:
                    if (msg.arg1 == 0x01 && !UILApplication.hasDayMode) {//01是白天，原来是夜晚模式才换肤
                        SkinManager.getInstance().loadSkin("skin_day.skin",
                                new ILoaderListener() {
                                    @Override
                                    public void onStart() {
//                                        ToastShow.showToastCenter(getApplicationContext(), "正在切换中", Toast.LENGTH_SHORT);
                                    }

                                    @Override
                                    public void onSuccess() {
//                                        ToastShow.showToastCenter(getApplicationContext(), "切换成功", Toast.LENGTH_SHORT);
                                        UILApplication.hasDayMode = true;
                                    }

                                    @Override
                                    public void onFailed(String errMsg) {
                                        ToastShow.showToastCenter(getApplicationContext(), "切换失败");
                                    }

                                    @Override
                                    public void onProgress(int progress) {

                                    }
                                }

                        );

                    } else if (msg.arg1 == 0x00 && UILApplication.hasDayMode) {//白天模式，原来是夜间模式，才换肤
                        SkinManager.getInstance().restoreDefaultTheme();
                        UILApplication.hasDayMode = false;
                    }
                    break;
                case OPEN_CAMERA_PERMISSIONS:
                    ToastShow.showToastCenter(MainActivity.this, "闪光灯设置为开启，请打开调用摄像头权限");
                    break;
                default:
                    //
                    break;
            }
            //刷新
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bg = (RelativeLayout) findViewById(R.id.bg);
        alrm = (TextView) findViewById(R.id.alrm);
//        getForeData();
//        监测是否连接了服务器wifi
        wifiConnectedState(MainActivity.this);
        initCommand();
//        连接服务器
        initSocketThread.start();
        konwVolum();
    }

    int maxVolume;

    private void konwVolum() {
        //    音量控制
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private void initCommand() {
        //        初始化查询报警器就绪状态
        buf[0] = (byte) 0x05;
        buf[1] = (byte) 0x01;
        buf[2] = (byte) 0x00;
        buf[3] = (byte) 0x01;

        //        初始化查询报警器个数
        alarmCount[0] = (byte) 0x05;
        alarmCount[1] = (byte) 0x02;
        alarmCount[2] = (byte) 0x00;
        alarmCount[3] = (byte) 0x01;

        //        初始化报警器状态查询
        alarmState[0] = (byte) 0x05;
        alarmState[1] = (byte) 0x03;
        alarmState[2] = (byte) 0x00;
        alarmState[3] = (byte) 0x01;

        //        初始化夜间模式查询
        bufNightMode[0] = (byte) 0x05;
        bufNightMode[1] = (byte) 0x0A;
        bufNightMode[2] = (byte) 0x00;
        bufNightMode[3] = (byte) 0x01;

//                电池
        bufPower[0] = (byte) 0x05;
        bufPower[1] = (byte) 0x07;
        bufPower[2] = (byte) 0x00;
        bufPower[3] = (byte) 0x01;
        //重启
        commandReset[0] = (byte) 0x05;
        commandReset[1] = (byte) 0x0c;
        commandReset[2] = (byte) 0x00;
        commandReset[3] = (byte) 0x01;


    }

    /*设置*/
    public void setUp(View view) {
        Log.i(TAG, "setUp: ");
        if (RepeatClickUtils.isFastDoubleClick(800)) {
            return;
        }
        startActivity(new Intent(getApplicationContext(), SetUpActivity.class));
    }

    private void initSocket() {

        try {
            if (UILApplication.socket == null) {
                UILApplication.socket = new Socket();
            }
            UILApplication.socket.connect(new InetSocketAddress(UILApplication.HOST, UILApplication.PORT), UILApplication.ALARM_QUERY_TIME);
            UILApplication.hasSucess = true;
            UILApplication.errerCount = 0;
        } catch (IOException e) {
//            服务器异常
            UILApplication.errerCount++;
            Log.i(TAG, "initSocket: UILApplication.errerCount:" + UILApplication.errerCount);
            if (!UILApplication.hasShowError && UILApplication.errerCount > 3) {
                mHandler.sendEmptyMessage(FAIL);
            }
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    class InitSocketThread extends Thread {
        @Override
        public void run() {
            //1. 连接服务端
            UILApplication.socket = new Socket();
            while (!UILApplication.hasSucess) {
                if (UILApplication.hasConnectWifi) {//判断是否连接服务器wifi
                    initSocket();
                }
            }
          /*  try {
//                3s内超时
                socket.setSoTimeout(3000);
            } catch (SocketException e) {
                e.printStackTrace();
            }*/
            //2. 发送查询类帧消息
            //抛出一个消息到handle执行 开启两个线程，一个向服务器发消息， 一个接收服务器消息。
            try {
                Log.i(TAG, "run:  UILApplication.in");
                UILApplication.in = new DataInputStream(UILApplication.socket.getInputStream()); // 获得DataInputStream对象d
                UILApplication.out = new DataOutputStream(UILApplication.socket.getOutputStream());
                startHeartBeatThread();   //200MS Timer
                rThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        hasRunningReply = false;
        UILApplication.closeSocket();
        //关闭中鱼闹铃资源
        closeCashFishEffect();
        if (heartBeatTimer != null) {
            heartBeatTimer = null;
            heartBeatTask.cancel();
        }
        UILApplication.hasSucess = false;
        UILApplication.ISLOGIN = false;
        UILApplication.hasShowError = false;
        UILApplication.errerCount = 0;
        UILApplication.hasConnectWifi = false;
        UILApplication.hasCashFish = false;
        UILApplication.commandCount = 0;
        UILApplication.power = 0;
        UILApplication.hasDayMode = false;
        UILApplication.responseCount = 0;
        SkinManager.getInstance().restoreDefaultTheme();
        super.onDestroy();
    }

    /**
     * 校验和
     *
     * @param msg 需要计算校验和的byte数组
     * @return 计算出的校验和数组
     */
    public byte[] SumCheck(byte[] msg) {
        byte mSum = 0x00;
//        因为每一次都要checksum
        msg[2]++;
//        test
//        msg[2]=0x01;
        for (int i = 0; i < msg.length - 1; i++) {
            mSum = (byte) (mSum + msg[i]);
        }
        msg[msg.length - 1] = (byte) (mSum ^ 0xff);
        return msg;
    }

    /**
     * 启动心跳线程
     */
    private void startHeartBeatThread() {
        // 启动心跳线程
        if (null != heartBeatTimer) {
            heartBeatTimer.schedule(heartBeatTask, 0, UILApplication.hearTime);//att
        }

    }

    //是否连接WIFI
    public void wifiConnectedState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        DhcpInfo infoD = wifiManager.getDhcpInfo();
        if (wifiNetworkInfo.isConnected()) {
            if (ipIntToString(infoD.serverAddress).equals(UILApplication.HOST)) {
                UILApplication.hasConnectWifi = true;
            } else {
                ToastShow.showToastCenter(getBaseContext(), "未能连接服务器wifi，连接服务异常");
                UILApplication.hasConnectWifi = false;
            }
        } else {
            ToastShow.showToastCenter(getBaseContext(), "未能连接服务器wifi，连接服务异常");
            UILApplication.hasConnectWifi = false;
        }

    }

    private void cashFishRing() {
        Uri uri = null;
        if (SharedPrefrenceUtils.containsKey(getBaseContext(), "sys_ring")) {
            if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(getBaseContext(), "sys_ring"))) {
                // 使用来电铃声的铃声路径
                uri = Uri.parse(SharedPrefrenceUtils.getString(getBaseContext(), "sys_ring"));
            }
        } else {
            uri = RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_ALARM);
        }
        Log.i(TAG, "cashFishRing: uri:" + uri);
        if (null == uri) {//若为空，则是设置无了，不用响铃了
            return;
        }

//         如果为空，才构造，不为空，说明之前有构造过
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            Log.i(TAG, "cashFishRing: new MediaPlaye:");
        }
        try {
            mMediaPlayer.setDataSource(MainActivity.this, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (SharedPrefrenceUtils.containsKey(MainActivity.this, "sys_volume")) {
            mMediaPlayer.setVolume(SharedPrefrenceUtils.getInt(getBaseContext(), "sys_volume"), SharedPrefrenceUtils.getInt(getBaseContext(), "sys_volume"));
        } else {
//            从未设置音量
            mMediaPlayer.setVolume(maxVolume / 2, maxVolume / 2);
//            mMediaPlayer.setVolume(16, 16);
        }
        Log.i(TAG, "cashFishRing: maxVolume:" + maxVolume);
        mMediaPlayer.setLooping(true); //循环播放
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        Log.i(TAG, "cashFishRing: maxVolume start");
    }


    public class ReplyThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (hasRunningReply) {
                if (UILApplication.hasSucess && UILApplication.in != null) {
                    StringBuffer receive = new StringBuffer();
                    try {
                        byte[] receiveBuffer = new byte[UILApplication.in.available()];//att
                        if (null != receiveBuffer && UILApplication.in.read(receiveBuffer) > 0) {

                            for (int i = 0; i < receiveBuffer.length; i++) {
                                Total[EndPoint] = receiveBuffer[i];
                                EndPoint++;
                                if (EndPoint >= 128) {
                                    EndPoint = 0;
                                }
                                receive.append(receiveBuffer[i]);
                            }
                            Log.i(TAG, "Function: to receiveBuffer:" + receive);
                            UILApplication.responseCount = 0;
                        }
                        Function();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //    记录中鱼情况
    Short AlarmOld = 0;




    Vibrator vibrator = null;

    private void cashFish() {
        Log.i(TAG, "Function: to receive 03 cashFish");
        if (SharedPrefrenceUtils.containsKey(getBaseContext(), "has_vibrate")) {
            if (SharedPrefrenceUtils.getBoolean(getBaseContext(), "has_vibrate")) {// 如果已经设置了振动
                vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(effectDuration);
            }
        } else {
            //默认是设置振动
            vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(effectDuration);
        }
        //        显示文字
        mHandler.sendEmptyMessage(CATCH_FISH);
        cashFishRing();
        if (SharedPrefrenceUtils.containsKey(getBaseContext(), "has_flash")) {
            if (SharedPrefrenceUtils.getBoolean(getBaseContext(), "has_flash")) {// 如果已经设置了闪光
                flashlight(null);
            }
        } else {
            flashlight(null);
        }


    }

    /*闪光灯*/
    public void flashlight(View view) {
        //判断设备是否支持闪光灯
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "当前设备不支持闪光灯!", Toast.LENGTH_LONG).show();
            return;
        }
        openLight();
    }

    //打开闪光灯
    protected void openLight() {
        Log.i(TAG, "openLight: ");
        try {
            camera = Camera.open();
            camera.startPreview();
            parameter = camera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameter);
        } catch (RuntimeException e) {
            e.printStackTrace();
            exceptionResetSocket();
            mHandler.sendEmptyMessage(OPEN_CAMERA_PERMISSIONS);
        }

    }

    //关闭闪光灯
    public void closeLight() {
        if (camera != null) {
            Log.i(TAG, "closeLight: camera!=null");
            parameter = camera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameter);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private void closeCashFishEffect() {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.cancel();
        }
        closeLight();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer = null;
        }
        cashList.clear();
        cashCountIndex = 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 转换IP
     *
     * @param ipAddress
     * @return
     */
    public String ipIntToString(int ipAddress) {
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

    public void exceptionResetSocket() {
        UILApplication.errerCount++;
        Log.i(TAG, "exceptionResetSocket: errerCount:"+UILApplication.errerCount+",hasShowError:"+UILApplication.hasShowError);
        if (!UILApplication.hasShowError && UILApplication.errerCount > 3) {
            UILApplication.ISLOGIN = false;
            UILApplication.hasSucess = false;
            UILApplication.responseCount = 0;
            mHandler.sendEmptyMessage(FAIL);
            UILApplication.closeSocket();
            UILApplication.resetSocket();
            //关闭中鱼闹铃资源
            closeCashFishEffect();
        }


    }

    //返回的处理
    public void Function() {
        int DataLength;
        int CommandLength;
        byte[] Command = new byte[9];
        if (EndPoint == StartPoint)
            return;

        if (EndPoint > StartPoint) {
            DataLength = EndPoint - StartPoint;
        } else {
            DataLength = 128 - StartPoint + EndPoint;
        }
        if (DataLength < 5)
            return;

        CommandLength = Total[StartPoint];
        if (CommandLength < 5 || CommandLength > 9) {
            StartPoint++;
            if (StartPoint >= 128)
                StartPoint = 0;
            return;
        }
        if (DataLength < CommandLength)
            return;
        int CounterCommand = 0;
        int CounterTotal = StartPoint;
        byte CRC8 = 0;
        while (CommandLength > 0) {
            Command[CounterCommand] = Total[CounterTotal];
            if (CommandLength > 1)
                CRC8 += Command[CounterCommand];
            CounterCommand++;
            CounterTotal++;
            if (CounterTotal >= 128)
                CounterTotal = 0;
            CommandLength--;
        }
        CRC8 ^= 0xFF;
        if (CRC8 != Command[Command[0] - 1] || Command[1] > 0x0F || Command[3] != 0x01) {
            StartPoint++;
            if (StartPoint >= 128)
                StartPoint = 0;
            return;
        }
        StartPoint += Command[0];
        if (StartPoint >= 128)
            StartPoint = StartPoint % 128;
        Log.i(TAG, "set sound Function: Command[1]:" + Command[1] + "commancount:" + UILApplication.commandCount);
        switch (Command[1]) {
            case 0x01:
                Log.i(TAG, "Function: to receive 01");
                if (UILApplication.commandCount == 0) {
                    //就绪
                    if (Command[4] == 0x01) {
                        if (!UILApplication.ISLOGIN) {
                            UILApplication.commandCount = 1;
                            UILApplication.ISLOGIN = true;
                            mHandler.sendEmptyMessage(SUCESS);
                        }
                    } else {
                        UILApplication.errerCount++;
                        UILApplication.ISLOGIN = false;
                        if (!UILApplication.hasShowError && UILApplication.errerCount > 3) {
                            mHandler.sendEmptyMessage(FAIL);
                        }
                    }
                }

                break;
            case 0x02:
//               报警器数量
                if (UILApplication.commandCount == 1) {
                    Log.i(TAG, "Function: to receive 02 count:" + Command[4]);
                    UILApplication.ALARM_COUNT = Command[4];
//                Log.i(TAG, "Function: UILApplication.ALARM_COUNT:"+UILApplication.ALARM_COUNT);
                    UILApplication.commandCount = 2;
                }

                break;
            case 0x03:
                //报警器状态
                if (UILApplication.commandCount == 2) {
                    UILApplication.commandCount = 3;
                    short Arlarm;
                    Arlarm = Command[4];
                    Arlarm += (Command[5] << 8);
                    //中鱼参数
                    List cashListTemp = new ArrayList();
                    Log.i(TAG, "Function: to receive 03");
//                if (UILApplication.show){//只是为了测试中鱼效果的
//                    cashList.clear();
//                    Arlarm |= 1 << 0;
//                    Arlarm |= 1 << 1;
//                    Arlarm |= 1 << 2;
//                    Arlarm |= 1 << 3;
//                    Arlarm |= 1 << 4;
//                    Arlarm |= 1 << 5;
//                    Arlarm |= 1 << 6;
//                    Arlarm |= 1 << 7;
//                    Log.i(TAG, "Function: fish alarm:" + Arlarm);
//                    Log.i(TAG, "Function: fish alarmold:" + AlarmOld);
//                }

                    for (int i = 0; i < UILApplication.ALARM_COUNT; i++) {  //
                        if ((0x0001 << i) == (Arlarm & (0x0001 << i))) {//中鱼
                            cashListTemp.add(i);
                        } else {
//                            att,只是测试
//                            cashList.add(i);
                        }
                    }
                    if (Arlarm > 0 && Arlarm != AlarmOld) {
                        closeCashFishEffect();
                        AlarmOld = Arlarm;
                        cashList = cashListTemp;
                        cashFish();
                        UILApplication.hasCashFish = true;
                    } else if (Arlarm == 0) {
                        AlarmOld = 0;
                        closeCashFishEffect();
                        UILApplication.hasCashFish = false;
                    }
                }

                break;
            case 0x0A:
                //夜间模式
                if (UILApplication.commandCount == 3) {
                    UILApplication.commandCount = 4;
                    Log.i(TAG, "Function: to receive 0A day:" + Command[4]);
                    Message msg = new Message();
                    msg.what = NIGHT_MODE_RESULT;
                    msg.arg1 = Command[4];
                    mHandler.sendMessage(msg);
                }

                break;
            case 0x07:
                //电源
                if (UILApplication.commandCount == 4) {
                    UILApplication.commandCount = 1;
                    UILApplication.power = Command[4] * 100 + Command[5];
                    Log.i(TAG, "Function: to receive 07 power:" + Command[4] + ",UILApplication.power:" + UILApplication.power);
                    if (UILApplication.power < UILApplication.powerMin) {
                        UILApplication.power = UILApplication.powerMin;
                    } else if (UILApplication.power > UILApplication.powerMax) {
                        UILApplication.power = UILApplication.powerMax;
                    }
                }

                break;

            case 0x08:
                Log.i(TAG, "set seness Function: to receive08 :setErrorCount: " + UILApplication.setErrorCount + ",Command[5]:" + Command[5] + ",commandCount:" + UILApplication.commandCount);
                if (UILApplication.commandCount == 8) {
                    Intent intent = new Intent(SetUpActivity.SETUP_RECEIVE);
                    intent.putExtra("command", 8);
                    intent.putExtra("code", Integer.parseInt(StringUtils.Byte2HexString(Command[5]), 16));
                    intent.putExtra("sn", Integer.parseInt(StringUtils.Byte2HexString(Command[4]), 16));
                    sendBroadcast(intent);
                }
                break;
            case 0x09:
                Log.i(TAG, "set sound Function: to receive09 : UILApplication.setErrorCount: " + UILApplication.setErrorCount + ",Command[5]:" + Command[5]);
//                    设定报警器声音大小和类型
                if (UILApplication.commandCount == 9) {
                    Intent intent = new Intent(SetUpActivity.SETUP_RECEIVE);
                    intent.putExtra("command", 9);
                    //            成功
                    intent.putExtra("code", Integer.parseInt(StringUtils.Byte2HexString(Command[5]), 16));
                    intent.putExtra("sn", Integer.parseInt(StringUtils.Byte2HexString(Command[4]), 16));
                    sendBroadcast(intent);
                }
                break;
            case 0x0b:
                if (UILApplication.commandCount == 11) {
                    Log.i(TAG, "Function: to receive 11 readalarm Command[4]:" + Command[4] + ",Command[5]:" + Command[5] + ",Command[6]:" + Command[6] + ",Command[7]:" + Command[7]);
                    Intent intent1 = new Intent(SetUpActivity.SETUP_RECEIVE);
                    intent1.putExtra("command", 11);
                    intent1.putExtra("sn", Integer.parseInt(StringUtils.Byte2HexString(Command[4]), 16));
                    intent1.putExtra("sensitive", Integer.parseInt(StringUtils.Byte2HexString(Command[5]), 16));
                    intent1.putExtra("alarm_voume", Integer.parseInt(StringUtils.Byte2HexString(Command[6]), 16));
                    intent1.putExtra("alarm_ring", Integer.parseInt(StringUtils.Byte2HexString(Command[7]), 16));
                    sendBroadcast(intent1);
                }
                break;
            case 0x06:
                if (UILApplication.commandCount == 6) {
                    Intent intent = new Intent(SetUpActivity.SETUP_RECEIVE);
                    intent.putExtra("command", 6);
                    intent.putExtra("sn", Integer.parseInt(StringUtils.Byte2HexString(Command[4]), 16));
                    intent.putExtra("code", Integer.parseInt(StringUtils.Byte2HexString(Command[5]), 16));
                    sendBroadcast(intent);
                    Log.i(TAG, "set Function: to 06 receive setErrorCount:" + UILApplication.setErrorCount + ",Function: reset code:" + Command[5]);
                }
                break;
        }
    }

}
