package com.fusi.fishingalarm.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.UILApplication;
import com.fusi.fishingalarm.ui.ToastShow;
import com.fusi.fishingalarm.utils.RepeatClickUtils;
import com.fusi.fishingalarm.utils.SharedPrefrenceUtils;
import com.fusi.fishingalarm.utils.StringUtils;
import com.lsd.atcmd.Device;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import support.PercentRelativeLayout;


/**
 * Created by user90 on 2016/5/27.
 */
public class SetUpActivity extends BaseActivity {

    private String TAG = "SetUpActivity";

    //    跳转到报警器设置
    static final int REQUEST_TO_ALARM = 1;
    //    跳转到ID设置
    static final int REQUEST_ID = 2;
    //    跳转到报警铃声
    static final int REQUEST_ALARM_RING = 3;
    //    跳转到系统铃声
    static final int REQUEST_SYS_RING = 4;
    //    闪光灯闪烁
    static final int FLASH_LIGHT = 5;
    //    显示电池
    static final int SHOW_POWER = 6;
    //    设置失败
    static final int FAIL_SET = 7;
    //    查询失败
    static final int FAIL_READ = 8;
    static final int FAIL = 9;
    //    设置成功
    static final int SUCESS_SET = 10;
    //    查询成功
    static final int SUCESS_READ = 11;
    //    设置WLAN开关
    static final int WLAN_SWITCH = 12;
    //    恢复顺序号出厂设置
    static final int SUCESS_RESET = 13;
    //    报警器名字
    private TextView mAlarm;
    //    电源布局
    private LinearLayout powerLayout;
    //    从报警器id布局
    private LinearLayout idLayout;
    //    振动
    private boolean hasVibrate;
    private ImageView im_vibrate;
    private TextView tv_vibrate;
    //    闪光
    private boolean hasFlashlight;
    private ImageView im_flashlight;
    private TextView tv_flashlight;
    //    模式
    private boolean hasDayMode;
    private ImageView im_mode;
    private TextView tv_night;
    //    系统音量
    private SeekBar sys_volume;
    //    系统铃声
    private TextView sys_ring;
    //    wlan开启情况
    private TextView switch_wlan;
    //    灵敏度
    private SeekBar sensitive;
    //    报警音量
    private SeekBar alarm_volume;
    //    报警铃声
    private TextView alarm_ring;
    //     报警器类型
    private TextView alarm;
    //显示id
    private TextView id;
    //直达WiFi修改信息
    private TextView alterWifi;
    //    电池量文字表示
    public TextView powerText;
    NumberFormat numberFormat;
    //    音量控制
    private AudioManager mAudioManager;
    private int maxVolume, currentVolume;
    private static final int SYS_VOLUME = 21;
    //    闪光灯
    Parameters parameter;
    private Camera camera;
    //wifi
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    DhcpInfo infoD;
    //    闪光
    private boolean hasFlash = true;
    //电池进度条
    public ProgressBar progress_bar;
    //报警灵敏度
    byte[] bufSensitive = new byte[7];
    //灵敏度
    int sensitiveProgress = 0;
    //报警灵敏度
    byte[] bufAlarmSound = new byte[8];
    //读取报警器设置
    byte[] bufReadAlarm = new byte[6];
    //重启服务器
    byte[] commandReset = new byte[5];
    //清除序列号，暂时理解成恢复出厂设置
    byte[] bufClearSn = new byte[6];
    //声音等级
    int alarmSoundProgress = 0;
    RelativeLayout bg;
    PercentRelativeLayout shell;
    //报警铃声
    int alarmRing = 0;
    //读取报警器设置顺序号
    int sn = 0;
    public static final String SETUP_RECEIVE = "com.fusi.fishingalarm.setupAction";
    ReceivedDealBroadcastReceiver receivedSetup = new ReceivedDealBroadcastReceiver();
    //设置命令
    Timer timerSetup;
    TimerTask timerTaskSetup;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SYS_VOLUME:
                    setVolum();
                    break;
                case FLASH_LIGHT:
                    closeLight();
                    break;
                case SHOW_POWER:
                    showPower();
                    break;
                case SUCESS_SET:
                    UILApplication.commandCount = 1;
                    UILApplication.setErrorCount = 0;
                    Log.i(TAG, "set Function: to 06  SUCESS_SET commandcount:" + UILApplication.commandCount + ",setErrorCount:" + UILApplication.setErrorCount);
//                    ToastShow.showToastCenter(getBaseContext(), "设置成功");
                    break;
                case FAIL_SET:
                    UILApplication.setErrorCount = 0;
                    UILApplication.commandCount = 1;
                    ToastShow.showToastCenter(getBaseContext(), "设置失败");
                    break;
                case SUCESS_READ:
                    UILApplication.setErrorCount = 0;
                    UILApplication.commandCount = 1;
                    AlarmSetupInfo alarmSetupInfo = (AlarmSetupInfo) msg.obj;
                    if (alarmSetupInfo.getSn() == 0) {
                        //主报警器
                        powerLayout.setVisibility(View.VISIBLE);
                        idLayout.setVisibility(View.GONE);
                        mAlarm.setText(getString(R.string.alarm));
                    } else {
                        powerLayout.setVisibility(View.GONE);
                        idLayout.setVisibility(View.VISIBLE);
                        mAlarm.setText(getString(R.string.alarm_sub) + String.valueOf(sn));
                    }
                    sensitive.setProgress(alarmSetupInfo.getSensitive());
                    alarm_volume.setProgress(alarmSetupInfo.getAlarm_voume());
                    alarm_ring.setText(getString(R.string.alarm_ring) + String.valueOf(alarmSetupInfo.getAlarm_ring()));
                    SharedPrefrenceUtils.setInt(SetUpActivity.this, "sensitive", alarmSetupInfo.getSensitive());
                    SharedPrefrenceUtils.setInt(SetUpActivity.this, "alarm_volume", alarmSetupInfo.getAlarm_voume());
                    SharedPrefrenceUtils.setInt(SetUpActivity.this, "alarm_ring", alarmSetupInfo.getAlarm_ring());
                    break;
                case FAIL_READ:
                    UILApplication.setErrorCount = 0;
                    UILApplication.commandCount = 1;
//                    ToastShow.showToastCenter(getBaseContext(), "查询失败");
                    break;
                case FAIL:
                    ToastShow.showToastCenter(getBaseContext(), "抱歉，连接服务器异常");
                    UILApplication.hasShowError = true;
                    break;
                case SUCESS_RESET:
                    UILApplication.commandCount = 1;
                    UILApplication.setErrorCount = 0;
                    sys_volume.setProgress(maxVolume / 2);
//        sys_ring.setText("系统铃声1");
                    getName(RingtoneManager.getActualDefaultRingtoneUri(SetUpActivity.this, RingtoneManager.TYPE_ALARM));
//                            switch_wlan.setText("开启");
                    if (msg.arg1==0){
                        powerLayout.setVisibility(View.VISIBLE);
                        idLayout.setVisibility(View.GONE);
                        alarm.setText("主报警器");
                    }else {
                        powerLayout.setVisibility(View.GONE);
                        idLayout.setVisibility(View.VISIBLE);
                        mAlarm.setText(getString(R.string.alarm_sub) + String.valueOf(msg.arg1));
                    }
                    sensitive.setProgress(5);
                    alarm_volume.setProgress(5);
                    alarm_ring.setText("报警铃声1");
                    vibrateOn();
                    flashlightOn();
                    nightMode();
                    resetToSharePreference();
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        initView();
        getForeData();
        initCommand();
        initData();
        initConnect();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SETUP_RECEIVE);
        registerReceiver(receivedSetup, filter);
    }


    private void initCommand() {
        //报警灵敏度
        bufSensitive[0] = (byte) 0x07;
        bufSensitive[1] = (byte) 0x08;
        bufSensitive[2] = (byte) 0x00;
        bufSensitive[3] = (byte) 0x01;

        //报警器声音大小和类型
        bufAlarmSound[0] = (byte) 0x08;
        bufAlarmSound[1] = (byte) 0x09;
        bufAlarmSound[2] = (byte) 0x00;
        bufAlarmSound[3] = (byte) 0x01;

        //读取报警器设置
        bufReadAlarm[0] = (byte) 0x06;
        bufReadAlarm[1] = (byte) 0x0b;
        bufReadAlarm[2] = (byte) 0x00;
        bufReadAlarm[3] = (byte) 0x01;

        //重启
        commandReset[0] = (byte) 0x05;
        commandReset[1] = (byte) 0x0c;
        commandReset[2] = (byte) 0x00;
        commandReset[3] = (byte) 0x01;

        //清除序列号，暂时理解成恢复出厂设置
        bufClearSn[0] = (byte) 0x06;
        bufClearSn[1] = (byte) 0x06;
        bufClearSn[2] = (byte) 0x00;
        bufClearSn[3] = (byte) 0x01;
    }

    private void initConnect() {
//        电源心跳
        mHandler.sendEmptyMessage(SHOW_POWER);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UILApplication.setErrorCount = 0;
        unregisterReceiver(receivedSetup);
        stopTimer();
        mHandler.removeCallbacksAndMessages(null);
    }


    private void getForeData() {
        if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "sensitive")) {
            sensitive.setProgress(SharedPrefrenceUtils.getInt(SetUpActivity.this, "sensitive"));
        }
        if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "alarm_volume")) {
            alarm_volume.setProgress(SharedPrefrenceUtils.getInt(SetUpActivity.this, "alarm_volume"));
        }
        if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "sys_ring")) {
            if (TextUtils.isEmpty(SharedPrefrenceUtils.getString(SetUpActivity.this, "sys_ring"))) {
                sys_ring.setText("无");
            } else {
                getName(Uri.parse(SharedPrefrenceUtils.getString(SetUpActivity.this, "sys_ring")));
            }
        } else {
            getName(RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM));
        }

        if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "alarm_name")) {
            if (SharedPrefrenceUtils.getInt(SetUpActivity.this, "alarm_name") == 0) {
                alarm.setText(getString(R.string.alarm));
                powerLayout.setVisibility(View.VISIBLE);
                idLayout.setVisibility(View.GONE);
            } else {
                powerLayout.setVisibility(View.GONE);
                idLayout.setVisibility(View.VISIBLE);
                alarm.setText(getString(R.string.alarm_sub) + String.valueOf(SharedPrefrenceUtils.getInt(SetUpActivity.this, "alarm_name")));
            }
        }
        if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "alarm_ring")) {
            alarm_ring.setText(getString(R.string.alarm_ring) + String.valueOf(SharedPrefrenceUtils.getInt(getBaseContext(), "alarm_ring", 0)));
        }
        if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "id_name")) {
            id.setText(getString(R.string.id_name) + String.valueOf(SharedPrefrenceUtils.getInt(getBaseContext(), "id_name", 0) + 1));
        }

        if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "has_vibrate")) {
            hasVibrate = SharedPrefrenceUtils.getBoolean(SetUpActivity.this, "has_vibrate");
            if (hasVibrate) {
                vibrateOn();
            } else {
                vibrateOff();
            }
        } else {
            hasVibrate = true;
            vibrateOn();
        }

        if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "has_flash")) {
            hasFlash = SharedPrefrenceUtils.getBoolean(SetUpActivity.this, "has_flash");
            if (hasFlash) {
                flashlightOn();
            } else {
                flashlightOff();
            }
        } else {
            hasFlash = true;
            flashlightOn();
        }

        if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "day_mode")) {
            hasDayMode = SharedPrefrenceUtils.getBoolean(SetUpActivity.this, "day_mode");
            if (hasDayMode) {
                dayMode();
            } else {
                nightMode();
            }
        } else {
            hasDayMode = true;
            nightMode();
        }
        if (UILApplication.ISLOGIN) {
            shell.setVisibility(View.GONE);
        } else {
            shell.setVisibility(View.VISIBLE);
        }

        if (UILApplication.hasWifiAlter) {
            alterWifi.setVisibility(View.VISIBLE);
        } else {
            alterWifi.setVisibility(View.GONE);
        }
    }

    private void initData() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        setVolum();
        sys_volume.setOnSeekBarChangeListener(seekBarChangeListener);
        sensitive.setOnSeekBarChangeListener(sensitiveChangeListener);
        alarm_volume.setOnSeekBarChangeListener(alarm_volumeChangeListener);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        wifiConnectedState();
        // 创建一个数值格式化对象
        numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(0);
        startTimerSetup();
        //监听音量键
        mHandler.sendEmptyMessage(SYS_VOLUME);

    }

    SeekBar.OnSeekBarChangeListener sensitiveChangeListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            if (progress <= 0) {
                sensitiveProgress = 1;
            } else {
                sensitiveProgress = progress;
            }

        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            if (RepeatClickUtils.isFastDoubleClick(200)) {
                return;
            }
            //序号
            if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "alarm_name")) {
                bufSensitive[4] = (byte) Integer.parseInt(Integer.toHexString(SharedPrefrenceUtils.getInt(SetUpActivity.this, "alarm_name")));
            } else {
//                如果有，则说明已经设置，如果没有，则没有设置，默认为0，是主报警器的顺序号
                bufSensitive[4] = (byte) 0x00;
            }
//            报警灵敏度
            bufSensitive[5] = (byte) Integer.parseInt(Integer.toHexString(sensitiveProgress), 16);
            //发送请求
            UILApplication.commandCount = 8;
        }

    };

    SeekBar.OnSeekBarChangeListener alarm_volumeChangeListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            if (alarmSoundProgress <= 0) {
                alarmSoundProgress = 1;
            } else {
                alarmSoundProgress = progress;
            }


        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            if (RepeatClickUtils.isFastDoubleClick(800)) {
                return;
            }
            //顺序号
            if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "alarm_name")) {
                bufAlarmSound[4] = (byte) Integer.parseInt(Integer.toHexString(SharedPrefrenceUtils.getInt(SetUpActivity.this, "alarm_name")), 16);
            } else {
//                如果有，则说明已经设置，如果没有，则没有设置，默认为0，是主报警器的顺序号
                bufAlarmSound[4] = (byte) 0x00;
            }
//            声音等级
            bufAlarmSound[5] = (byte) Integer.parseInt(Integer.toHexString(alarmSoundProgress), 16);
//            音调
            if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "alarm_ring")) {
                bufAlarmSound[6] = Byte.parseByte(Integer.toHexString(SharedPrefrenceUtils.getInt(getBaseContext(), "alarm_ring", 1)), 16);
                alarmRing = SharedPrefrenceUtils.getInt(getBaseContext(), "alarm_ring", 1);
            } else {
                bufAlarmSound[6] = (byte) 0x01;//默认报警铃声从序号1开始
                alarmRing = 1;
            }
            //发送请求
            UILApplication.commandCount = 9;
            Log.i(TAG, "set sound  clicke");

        }

    };

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            SharedPrefrenceUtils.setInt(SetUpActivity.this, "sys_volume", progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

    };

    private void initView() {
        mAlarm = (TextView) findViewById(R.id.alarm);
        powerLayout = (LinearLayout) findViewById(R.id.powerLayout);
        idLayout = (LinearLayout) findViewById(R.id.idLayout);
        im_vibrate = (ImageView) findViewById(R.id.im_vibrate);
        tv_vibrate = (TextView) findViewById(R.id.tv_vibrate);
        im_flashlight = (ImageView) findViewById(R.id.im_flashlight);
        tv_flashlight = (TextView) findViewById(R.id.tv_flashlight);
        im_mode = (ImageView) findViewById(R.id.im_mode);
        tv_night = (TextView) findViewById(R.id.tv_night);
        sys_volume = (SeekBar) findViewById(R.id.sys_volume);
        sys_ring = (TextView) findViewById(R.id.sys_ring);
        switch_wlan = (TextView) findViewById(R.id.switch_wlan);
        sensitive = (SeekBar) findViewById(R.id.setup_sensitive);
        alarm_volume = (SeekBar) findViewById(R.id.alarm_volume);
        alarm_ring = (TextView) findViewById(R.id.alarm_ring);
        alarm = (TextView) findViewById(R.id.alarm);
        id = (TextView) findViewById(R.id.id);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        powerText = (TextView) findViewById(R.id.powerText);
        bg = (RelativeLayout) findViewById(R.id.bg);
        shell = (PercentRelativeLayout) findViewById(R.id.shell);
        alterWifi = (TextView) findViewById(R.id.alterWifi);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /*系统铃声*/
    public void sysRing(View view) {
//        startActivity(new Intent(getApplicationContext(), SysRingActivity.class));
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
                "设置中鱼提醒铃声");
//        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,false);
//        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_INCLUDE_DRM, true);
        //弹出列表选中当前铃声
        if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "sys_ring")) {
            if (!TextUtils.isEmpty(SharedPrefrenceUtils.getString(SetUpActivity.this, "sys_ring"))) {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(SharedPrefrenceUtils.getString(SetUpActivity.this, "sys_ring")));
            }
        } else {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM));
        }
        startActivityForResult(intent, REQUEST_SYS_RING);
    }


    /*wlan列表*/
    public void wlanList(View view) {
//        startActivity(new Intent(getApplicationContext(), WlanListActivity.class));
//        startActivity(new Intent(getApplicationContext(), WifiConnActivity.class));
        Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        startActivityForResult(intent, WLAN_SWITCH);
    }

    //修改WiFi名和密码
    public void alterWifiInfo(View view) {
        infoD = wifiManager.getDhcpInfo();
        if (StringUtils.ipIntToString(infoD.serverAddress).equals(UILApplication.HOST)) {
            Log.i(TAG, "alterWifiInfo:  checke wifiInfo.getSSID()" + wifiInfo.getSSID());
            Device device = new Device(UILApplication.HOST, wifiInfo.getBSSID(), wifiInfo.getSSID(), 8800);
            Intent intent = new Intent(SetUpActivity.this, AlterUserAndPwdActivity.class);
//                    Intent intent = new Intent(mContext, DeviceListActivity.class);
            intent.putExtra("DEVICE", device);
            startActivity(intent);
        } else {
            ToastShow.showToastCenter(getBaseContext(), "该WiFi不是钓鱼报警器的WiFi，不能修改WiFi信息");

        }
    }

    //是否连接WIFI
    public void wifiConnectedState() {
        if (wifiManager.isWifiEnabled()) {
            switch_wlan.setText("开启");
            alterWifi.setVisibility(View.VISIBLE);
        } else {
            switch_wlan.setText("关闭");
            alterWifi.setVisibility(View.GONE);
        }
    }

    /*报警铃声*/
    public void alarmRing(View view) {
        startActivityForResult(new Intent(getApplicationContext(), AlarmRingActivity.class), REQUEST_ALARM_RING);
    }

    /*从报警器id*/
    public void idSetup(View view) {
        startActivityForResult(new Intent(getApplicationContext(), IDSetupActivity.class), REQUEST_ID);
    }

    /*振动*/
    public void vibrate(View view) {
        if (RepeatClickUtils.isFastDoubleClick(800)) {
            return;
        }
        if (hasVibrate) {
            vibrateOff();
        } else {
            Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{0, 50, 50, 100, 50}, -1);
            vibrateOn();
        }
    }

    /*闪光灯*/
    public void flashlight(View view) {
        //判断设备是否支持闪光灯
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "当前设备不支持闪光灯!", Toast.LENGTH_LONG).show();
            return;
        }
        if (RepeatClickUtils.isFastDoubleClick(800)) {//如果2秒内点击多次
            return;
        }
        if (hasFlashlight) {
            flashlightOff();
        } else {
            flashlightOn();
            openLight();
        }
    }

    //打开闪光灯
    protected void openLight() {
        if (UILApplication.hasCashFish) {
            return;
        }
        try {
            camera = Camera.open();
            camera.startPreview();
            parameter = camera.getParameters();
            parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameter);
            mHandler.sendEmptyMessageDelayed(FLASH_LIGHT, 500);
        } catch (RuntimeException e) {
            ToastShow.showToastCenter(SetUpActivity.this, "闪光灯设置为开启，请打开调用摄像头权限");
        }

    }

    //关闭闪光灯
    public void closeLight() {
        if (camera != null) {
            parameter = camera.getParameters();
            parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameter);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    /*夜间模式*/
    public void mode(View view) {
        if (RepeatClickUtils.isFastDoubleClick(800)) {
            return;
        }
        if (hasDayMode) {
            nightMode();
        } else {
            dayMode();
        }
    }

    /*恢复出厂设置*/
    public void reset(View view) {
        Log.i(TAG, "set Function: to 06 click: " + UILApplication.setErrorCount);
        //序号
        if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "alarm_name")) {
            bufClearSn[4] = (byte) Integer.parseInt(Integer.toHexString(SharedPrefrenceUtils.getInt(SetUpActivity.this, "alarm_name")));
        } else {
//                如果有，则说明已经设置，如果没有，则没有设置，默认为0，是主报警器的顺序号
            bufSensitive[4] = (byte) 0x00;
        }
        UILApplication.commandCount = 6;
    }

    private void resetToSharePreference() {
        //系统音量
        SharedPrefrenceUtils.setInt(SetUpActivity.this, "sys_volume", maxVolume / 2);
        //0代表系统铃声1
        if (null==RingtoneManager.getActualDefaultRingtoneUri(SetUpActivity.this, RingtoneManager.TYPE_ALARM)){
            SharedPrefrenceUtils.setString(SetUpActivity.this, "sys_ring","" );
        }else {
            SharedPrefrenceUtils.setString(SetUpActivity.this, "sys_ring",RingtoneManager.getActualDefaultRingtoneUri(SetUpActivity.this, RingtoneManager.TYPE_ALARM).toString() );
        }

//        SharedPrefrenceUtils.setBoolean(SetUpActivity.this, "switch_wlan", true);
        //0代表报警器
        SharedPrefrenceUtils.setInt(SetUpActivity.this, "alarm_name",Integer.parseInt(StringUtils.Byte2HexString(bufClearSn[4]), 16) );
        SharedPrefrenceUtils.setInt(SetUpActivity.this, "setup_sensitive", 5);
        SharedPrefrenceUtils.setInt(SetUpActivity.this, "alarm_volume", 5);
        //0代表报警铃声
        SharedPrefrenceUtils.setInt(SetUpActivity.this, "alarm_ring", 1);
        //0代表id
        SharedPrefrenceUtils.setInt(SetUpActivity.this, "id_name", 0);
        //设置振动
        SharedPrefrenceUtils.setBoolean(SetUpActivity.this, "has_vibrate", true);
        //设置闪光灯
        SharedPrefrenceUtils.setBoolean(SetUpActivity.this, "has_flash", true);
        //设置夜间模式
        SharedPrefrenceUtils.setBoolean(SetUpActivity.this, "day_mode", false);
    }

    /*主报警器*/
    public void mainAlarm(View view) {
        if (RepeatClickUtils.isFastDoubleClick(800)) {
            return;
        }
        if (!UILApplication.ISLOGIN) {
            ToastShow.showToastCenter(getBaseContext(), "未能连接服务器，不能设置");
            return;
        }
        startActivityForResult(new Intent(getApplicationContext(), AlarmSetupActivity.class), REQUEST_TO_ALARM);
//        startActivity(new Intent(getApplicationContext(), AlarmSetupActivity.class));
    }

    private void vibrateOff() {
        if (UILApplication.hasDayMode) {
            im_vibrate.setImageDrawable(getResources().getDrawable(R.drawable.selector_day_vibrate_close));
        } else {
            im_vibrate.setImageDrawable(getResources().getDrawable(R.drawable.selector_night_vibrate_close));
        }

        tv_vibrate.setText(getString(R.string.vibrate_off));
        SharedPrefrenceUtils.setBoolean(SetUpActivity.this, "has_vibrate", false);
        hasVibrate = false;
    }

    private void vibrateOn() {
        Log.i(TAG, "vibrateOn: UILApplication.hasDayMode:" + UILApplication.hasDayMode);
        if (UILApplication.hasDayMode) {
            im_vibrate.setImageDrawable(getResources().getDrawable(R.drawable.selector_day_vibrate_on));
        } else {
            im_vibrate.setImageDrawable(getResources().getDrawable(R.drawable.selector_night_vibrate_on));
        }

        tv_vibrate.setText(getString(R.string.vibrate_on));
        SharedPrefrenceUtils.setBoolean(SetUpActivity.this, "has_vibrate", true);
        hasVibrate = true;
    }

    private void flashlightOff() {
        if (UILApplication.hasDayMode) {
            im_flashlight.setImageDrawable(getResources().getDrawable(R.drawable.selector_day_flashlight_close));
        } else {
            im_flashlight.setImageDrawable(getResources().getDrawable(R.drawable.selector_night_flashlight_close));
        }
        tv_flashlight.setText(getString(R.string.flashlight_off));
        hasFlashlight = false;
        SharedPrefrenceUtils.setBoolean(SetUpActivity.this, "has_flash", false);
    }

    private void flashlightOn() {
        if (UILApplication.hasDayMode) {
            im_flashlight.setImageDrawable(getResources().getDrawable(R.drawable.selector_day_flashlight_on));
        } else {
            im_flashlight.setImageDrawable(getResources().getDrawable(R.drawable.selector_night_flashlight_on));
        }

        tv_flashlight.setText(getString(R.string.flashlight_on));
        hasFlashlight = true;
        SharedPrefrenceUtils.setBoolean(SetUpActivity.this, "has_flash", true);
    }

    private void nightMode() {
        if (UILApplication.hasDayMode) {
            im_mode.setImageDrawable(getResources().getDrawable(R.drawable.selector_day_night_mode));
        } else {
            im_mode.setImageDrawable(getResources().getDrawable(R.drawable.selector_night_night_mode));
        }

        tv_night.setText(getString(R.string.night_mode));
        hasDayMode = false;
        SharedPrefrenceUtils.setBoolean(SetUpActivity.this, "day_mode", false);
    }

    private void dayMode() {
        if (UILApplication.hasDayMode) {
            im_mode.setImageDrawable(getResources().getDrawable(R.drawable.selector_day_day_mode));
        } else {
            im_mode.setImageDrawable(getResources().getDrawable(R.drawable.selector_night_day_mode));
        }

        tv_night.setText(getString(R.string.day_mode));
        hasDayMode = true;
        SharedPrefrenceUtils.setBoolean(SetUpActivity.this, "day_mode", true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TO_ALARM) {
                if (data.getExtras().containsKey("alarm_name")) {
                    sn = data.getExtras().getInt("alarm_name");
                    Log.i(TAG, "onActivityResult: readalarm sn:" + sn);
                    bufReadAlarm[4] = Byte.parseByte(Integer.toHexString(sn), 16);
                    UILApplication.commandCount = 11;
                }

            } else if (requestCode == REQUEST_ID) {
                if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "id_name")) {
                    id.setText(getString(R.string.id_name) + String.valueOf(SharedPrefrenceUtils.getInt(getBaseContext(), "id_name", 0) + 1));
                }
            } else if (requestCode == REQUEST_ALARM_RING) {
//                    alarm_ring.setText(getString(R.string.alarm_ring) + String.valueOf(data.getExtras().getInt("alarm_ring")));
                //顺序号
                if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "alarm_name")) {
                    bufAlarmSound[4] = (byte) Integer.parseInt(Integer.toHexString(SharedPrefrenceUtils.getInt(SetUpActivity.this, "alarm_name")));
                } else {
//                如果有，则说明已经设置，如果没有，则没有设置，默认为0，是主报警器的顺序号
                    bufAlarmSound[4] = 0x00;
                }
                //            声音等级
                if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "alarm_voume")) {
//                        bufAlarmSound[5] = (byte) Integer.parseInt(Integer.toHexString(SharedPrefrenceUtils.getInt(SetUpActivity.this, "alarm_voume")));
                    bufAlarmSound[5] = Byte.parseByte(Integer.toHexString(SharedPrefrenceUtils.getInt(SetUpActivity.this, "alarm_voume")), 16);
                    alarmSoundProgress = SharedPrefrenceUtils.getInt(SetUpActivity.this, "alarm_voume");
                } else {
                    bufAlarmSound[5] = 0x05;
                    alarmSoundProgress = 5;
                }
                //            音调
                alarmRing = data.getExtras().getInt("alarm_ring");

                bufAlarmSound[6] = Byte.parseByte(Integer.toHexString(alarmRing), 16);
                UILApplication.commandCount = 9;
            }
            if (requestCode == REQUEST_SYS_RING) {
                Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                RingtoneManager.setActualDefaultRingtoneUri(this,
                        RingtoneManager.TYPE_ALARM, pickedUri);
                getName(pickedUri);
                if (pickedUri != null) {
                    SharedPrefrenceUtils.setString(SetUpActivity.this, "sys_ring", pickedUri.toString());
                } else {
//                    不设置闹铃，或无或静音
                    SharedPrefrenceUtils.setString(SetUpActivity.this, "sys_ring", "");
                }
            }
        }


        if (requestCode == WLAN_SWITCH) {
            wifiConnectedState();
        }
    }

    private void setVolum() {
        sys_volume.setMax(maxVolume);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (SharedPrefrenceUtils.containsKey(SetUpActivity.this, "sys_volume")) {
            sys_volume.setProgress(currentVolume);
        } else {
//            从未设置音量
            sys_volume.setProgress(maxVolume / 2);
        }
        mHandler.sendEmptyMessageDelayed(SYS_VOLUME, 500);
    }

    public void shelling(View view) {
        if (RepeatClickUtils.isFastDoubleClick(800)) {//如果800ms内点击多次
            return;
        }
        ToastShow.showToastCenter(getBaseContext(), "未能连接服务器，不能设置");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
            if (currentVolume < maxVolume) {
                currentVolume = currentVolume + 1;
            }
            sys_volume.setProgress(currentVolume);


            return true;

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // todo
            int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
            if (currentVolume > 0) {
                currentVolume = currentVolume - 1;
            }
            sys_volume.setProgress(currentVolume);
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    private void showPower() {
        if (!UILApplication.hasConnectWifi) {
            progress_bar.setProgress(0);
            powerText.setText("0%");
            shell.setVisibility(View.VISIBLE);
        } else {
            if (UILApplication.ISLOGIN) {
                if (UILApplication.power <= UILApplication.powerMin) {
                    progress_bar.setProgress(0);
                    powerText.setText("0%");
                } else if (UILApplication.power >= UILApplication.powerMax) {
                    progress_bar.setProgress(progress_bar.getMax());
                    powerText.setText("100%");
                } else {
                    progress_bar.setProgress((int) ((UILApplication.power - UILApplication.powerMin) * 1.0 / (UILApplication.powerMax - UILApplication.powerMin) * 100));
                    powerText.setText(String.valueOf(numberFormat.format((UILApplication.power - UILApplication.powerMin) * 1.0 / (UILApplication.powerMax - UILApplication.powerMin) * 100) + "%"));
                }
                shell.setVisibility(View.GONE);
                //获取报警器设置
                if (!SharedPrefrenceUtils.containsKey(SetUpActivity.this, "alarm_name") && UILApplication.ISLOGIN) {
                    bufReadAlarm[4] = 0x00;
                    UILApplication.commandCount = 11;
                }
            } else {
                progress_bar.setProgress(0);
                powerText.setText("0%");
                shell.setVisibility(View.VISIBLE);
            }
        }
        wifiConnectedState();
        mHandler.sendEmptyMessageDelayed(SHOW_POWER, 200);
    }

    private void getName(Uri uri) {
        if (uri != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
            String title = ringtone.getTitle(this);
            sys_ring.setText(title);

        } else {
            sys_ring.setText("无");
        }

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

    public void exceptionResetSocket() {
        UILApplication.errerCount++;
        if (!UILApplication.hasShowError && UILApplication.errerCount > 3) {
            UILApplication.hasShowError = true;
            UILApplication.ISLOGIN = false;
            UILApplication.hasSucess = false;
            UILApplication.responseCount = 0;
            mHandler.sendEmptyMessage(FAIL);
            UILApplication.closeSocket();
            UILApplication.resetSocket();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void finish(View view) {
        finish();
    }

    private void startTimerSetup() {
        if (timerSetup == null) {
            timerSetup = new Timer();
        }
        if (timerTaskSetup == null) {
            timerTaskSetup = new TimerTask() {
                @Override
                public void run() {
                    if (UILApplication.hasSucess && UILApplication.in != null) {
                        switch (UILApplication.commandCount) {
                            case 6:
                                //清除顺序号X的设置
                                if (UILApplication.setErrorCount <= 2) {
                                    UILApplication.setErrorCount++;
                                    try {
                                        //发送
                                        Log.i(TAG, "set Function: to 06 send: " + UILApplication.setErrorCount);
                                        UILApplication.out.write(SumCheck(bufClearSn));
                                        UILApplication.out.flush();
                                    } catch (IOException e) {
                                        exceptionResetSocket();
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.i(TAG, "set Function: to 06 send fail: " + UILApplication.setErrorCount);
                                    mHandler.sendEmptyMessage(FAIL_SET);
                                }
                                break;
                            case 8:
                                //设置灵敏度
                                if (UILApplication.setErrorCount <= 2) {
                                    UILApplication.setErrorCount++;
                                    try {
                                        //发送
                                        UILApplication.out.write(SumCheck(bufSensitive));
                                        UILApplication.out.flush();
                                    } catch (IOException e) {
                                        exceptionResetSocket();
                                        e.printStackTrace();
                                    }
                                } else {
                                    mHandler.sendEmptyMessage(FAIL_SET);
                                }
                                break;
                            case 9:
                                //设置报警器声音大小和类型
                                if (UILApplication.setErrorCount <= 2) {
                                    UILApplication.setErrorCount++;
                                    try {
                                        //发送
                                        UILApplication.out.write(SumCheck(bufAlarmSound));
                                        UILApplication.out.flush();
                                    } catch (IOException e) {
                                        exceptionResetSocket();
                                        e.printStackTrace();
                                    }
                                } else {
                                    mHandler.sendEmptyMessage(FAIL_SET);
                                }
                                break;
                            case 11:
                                //设置报警器声音大小和类型
                                if (UILApplication.setErrorCount <= 2) {
                                    UILApplication.setErrorCount++;
                                    try {
                                        //发送
                                        UILApplication.out.write(SumCheck(bufReadAlarm));
                                        UILApplication.out.flush();
                                    } catch (IOException e) {
                                        exceptionResetSocket();
                                        e.printStackTrace();
                                    }
                                } else {
                                    mHandler.sendEmptyMessage(FAIL_READ);
                                }
                                break;
                            case 12:
                                //重启服务器
//                        if (UILApplication.setErrorCount < 3) {
//                            UILApplication.setErrorCount++;
                                try {
                                    //发送
                                    Log.i(TAG, "Function: to send 12");
                                    UILApplication.out.write(SumCheck(commandReset));
                                    UILApplication.out.flush();
                                    UILApplication.commandCount = 0;
                                } catch (IOException e) {
                                    exceptionResetSocket();
                                    e.printStackTrace();
                                }
                                break;

                        }
                    }
                }
            };
        }
        if (timerSetup != null && timerTaskSetup != null) {
            timerSetup.schedule(timerTaskSetup, 0, UILApplication.SETUP_TIME);
        }
    }

    private void stopTimer() {
        if (timerSetup != null) {
            timerSetup.cancel();
            timerSetup = null;
        }
        if (timerTaskSetup != null) {
            timerTaskSetup.cancel();
            timerTaskSetup = null;
        }
    }


    public class ReceivedDealBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int command = bundle.getInt("command");
            int snReceive = bundle.getInt("sn");
            switch (command) {
                case 6:
                    //清除顺序号，暂时理解为恢复出厂设置
                    if (UILApplication.commandCount == 6) {
                        Log.i(TAG, "set Function: to 06 received setErrorCount:" + UILApplication.setErrorCount + ",snReceive:" + snReceive + ",clearSn:" + bufClearSn[4] + ",code:" + bundle.getInt("code") + ",commandCount:" + UILApplication.commandCount);
                        if (snReceive == Integer.parseInt(StringUtils.Byte2HexString(bufClearSn[4]), 16)) {
                            int code = bundle.getInt("code");
                            if (code == 1) {
                                Log.i(TAG, "onReceive: set Function: to 06 received: code=1 ");
                                //            成功
                                Message msg=new Message();
                                msg.what=SUCESS_RESET;
                                msg.arg1=snReceive;
                                mHandler.sendMessage(msg);
                                Log.i(TAG, "2set Function: to 06 received setErrorCount:" + UILApplication.setErrorCount + ",snReceive:" + snReceive + ",clearSn:" + bufClearSn[4] + ",code:" + bundle.getInt("code") + ",commandCount:" + UILApplication.commandCount);
                            } else {
                                Log.i(TAG, "onReceive: set Function: to 06 received: code!=1 UILApplication.setErrorCount:" + UILApplication.setErrorCount);
                                if (UILApplication.setErrorCount >= 3) {
                                    mHandler.sendEmptyMessage(FAIL_SET);
                                }
                            }
                        } else {
                            Log.i(TAG, "onReceive: set Function: to 06 received: sn!=resn UILApplication.setErrorCount:" + UILApplication.setErrorCount);
                            if (UILApplication.setErrorCount >= 3) {
                                mHandler.sendEmptyMessage(FAIL_SET);
                            }
                        }
                    }
                    break;
                case 8:
                    if (UILApplication.commandCount==8){
                        if (snReceive == Integer.parseInt(StringUtils.Byte2HexString(bufSensitive[4]), 16)) {
                            int code = bundle.getInt("code");
                            if (code == 1) {
                                //            成功
                                mHandler.sendEmptyMessage(SUCESS_SET);
                                SharedPrefrenceUtils.setInt(SetUpActivity.this, "sensitive", sensitiveProgress);
                            } else {
                                if (UILApplication.setErrorCount >= 3) {
                                    mHandler.sendEmptyMessage(FAIL_SET);
                                }
                            }
                        } else {
                            if (UILApplication.setErrorCount >= 3) {
                                mHandler.sendEmptyMessage(FAIL_SET);
                            }
                        }
                    }

                    break;
                case 9:
                    if (UILApplication.commandCount==9){
                        if (snReceive == Integer.parseInt(StringUtils.Byte2HexString(bufAlarmSound[4]), 16)) {
                            int code9 = bundle.getInt("code");
                            if (code9 == 1) {
                                mHandler.sendEmptyMessage(SUCESS_SET);
                                SharedPrefrenceUtils.setInt(SetUpActivity.this, "alarm_voume", alarmSoundProgress);
                                SharedPrefrenceUtils.setInt(SetUpActivity.this, "alarm_ring", alarmRing);
                                alarm_ring.setText(getString(R.string.alarm_ring) + String.valueOf(alarmRing));
                            } else {
                                if (UILApplication.setErrorCount >= 2) {
                                    mHandler.sendEmptyMessage(FAIL_SET);
                                }
                            }
                        } else {
                            if (UILApplication.setErrorCount >= 3) {
                                mHandler.sendEmptyMessage(FAIL_SET);
                            }
                        }
                    }

                    break;
                case 11:
                    if (UILApplication.commandCount==11){
                        // 查询这里，code代表顺序号
                        if (snReceive == sn) {
                            Message msg = new Message();
                            msg.what = SUCESS_READ;
                            AlarmSetupInfo alarmSetupInfo = new AlarmSetupInfo(bundle.getInt("sn"), bundle.getInt("sensitive"), bundle.getInt("alarm_voume"), bundle.getInt("alarm_ring"));
                            msg.obj = alarmSetupInfo;
                            mHandler.sendMessage(msg);
                            SharedPrefrenceUtils.setInt(SetUpActivity.this, "alarm_name", sn);
                        } else {
                            if (UILApplication.setErrorCount >= 3) {
                                mHandler.sendEmptyMessage(FAIL_READ);
                            }
                        }
                    }

                    break;
            }

        }


    }

    //报警器设置
    class AlarmSetupInfo {
        //顺序号
        int sn;
        //灵敏度
        int sensitive;
        //铃声类型
        int alarm_voume;
        int alarm_ring;

        public AlarmSetupInfo(int sn, int sensitive, int alarm_voume, int alarm_ring) {
            this.sn = sn;
            this.sensitive = sensitive;
            this.alarm_voume = alarm_voume;
            this.alarm_ring = alarm_ring;
        }

        public int getSn() {
            return sn;
        }

        public void setSn(int sn) {
            this.sn = sn;
        }

        public int getSensitive() {
            return sensitive;
        }

        public void setSensitive(int sensitive) {
            this.sensitive = sensitive;
        }

        public int getAlarm_voume() {
            return alarm_voume;
        }

        public void setAlarm_voume(int alarm_voume) {
            this.alarm_voume = alarm_voume;
        }

        public int getAlarm_ring() {
            return alarm_ring;
        }

        public void setAlarm_ring(int alarm_ring) {
            this.alarm_ring = alarm_ring;
        }
    }


}
