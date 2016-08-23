package com.fusi.fishingalarm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.UILApplication;
import com.lsd.atcmd.Device;
import com.lsd.atcmd.UdpUnicast;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import solid.ren.skinlibrary.base.SkinBaseActivity;


/**
 * Created by user90 on 2016/5/30.
 */
public class AlterUserAndPwdActivity extends BaseActivity {

    private String TAG = "AlterUserAndPwdActivity";

    static final int REQUEST_TO_CONNECTWIFI = 1;

    static int LOADING_DIMISS = 2;

    //    修改成功则显示让用户重新输入的布局界面
    String user, pwd, newUser, newPwd;
    TextView mUser;
    EditText mPwd, mNewUser, mNewPwd;
    TextView tvPwd;

    Device device;
    UdpUnicast udp;
    private AlterUserAndPwdActivity me = this;
    //命令
    StringBuilder sb = new StringBuilder();

    String wifi_mode = null;
    String ssid = null;
    String channel = null;
    String nameQueryCommand = "AT+WAP";
    String nameQuery = null;
    String pwdQueryCommand = "AT+WAKEY";
    String pwdQuery = null;

    //是否有密码
    boolean hasPwd = true;
    View loading_layout;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LOADING_DIMISS) {
                loading_layout.setVisibility(View.GONE);
                Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                finish();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_user_and_pwd);

        Intent intent = getIntent();
        if (intent != null) {
            Serializable obj = intent.getSerializableExtra("DEVICE");
            device = (Device) obj;
        }

        initView();
    }


    private void initView() {
        mUser = (TextView) findViewById(R.id.user);
        mPwd = (EditText) findViewById(R.id.pwd);
        mNewUser = (EditText) findViewById(R.id.new_user);
        mNewPwd = (EditText) findViewById(R.id.new_pwd);
        tvPwd = (TextView) findViewById(R.id.tvPwd);
        loading_layout = findViewById(R.id.loading_layout);

    }

    //取消保存
    public void cancel(View view) {
        finish();
    }

    //保存
    public void save(View view) {
//        现在随便都可以跳转
        user = mUser.getText().toString();
        pwd = mPwd.getText().toString();
        newUser = mNewUser.getText().toString();
        newPwd = mNewPwd.getText().toString();
//        alter_sucess.setVisibility(View.VISIBLE);
    }

    //取消连接
    public void cancelContact(View view) {
        finish();
    }


    public void doConnect() {
        udp = new UdpUnicast(device.ip, device.port, true);
        udp.setListener(new UdpUnicast.UdpUnicastListener() {
            @Override
            public void onReceived(byte[] data, int length) {
                if (data != null) {
                    String str = "";
                    try {
                        str = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    sb.append(str.replace("\r\n", ""));
                    Log.i(TAG, "sb.toString():" + sb.toString() + "尾");
                    Log.i(TAG, "str:" + str + "尾");
                    me.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            outEt.setText(sb.toString());
//                            Toast.makeText(me, "收到：" + sb.toString(), Toast.LENGTH_SHORT).show();
                            if (sb.toString().contains(nameQueryCommand) && !sb.toString().contains("AT+WAP=")) {
                                if (sb.toString().contains("+ok")) {
//                                    Toast.makeText(me, "查询名字成功", Toast.LENGTH_SHORT).show();
                                    String[] array = new String[3];
                                    array = sb.toString().split(",");
                                    nameQuery = array[1];//第二个是用户名
//                                    Toast.makeText(me, nameQuery, Toast.LENGTH_SHORT).show();
                                    mUser.setText(nameQuery);
                                    sb = null;
                                    sb = new StringBuilder();
                                    sb.append(pwdQueryCommand + "\n");
                                    send(pwdQueryCommand);
                                    Log.i(TAG, "run: name");
                                }
                            }

                            if (sb.toString().contains(pwdQueryCommand) && !sb.toString().contains("AT+WAKEY=")) {
                                if (sb.toString().contains("+ok")) {
//                                    Toast.makeText(me, "查询密码", Toast.LENGTH_SHORT).show();
                                    String[] array = new String[3];
                                    array = sb.toString().split(",");
                                    Log.i(TAG, "run: array[1]:" + array[1]);
                                    if (array[1].equals("NONE")) {
                                        hasPwd = false;
//                                        Toast.makeText(me, "无密码", Toast.LENGTH_SHORT).show();
                                        tvPwd.setVisibility(View.GONE);
                                        mPwd.setVisibility(View.GONE);
                                    } else {
                                        pwdQuery = array[2];//第二个是密码
//                                        Toast.makeText(me, pwdQuery, Toast.LENGTH_SHORT).show();
                                        tvPwd.setVisibility(View.VISIBLE);
                                        mPwd.setVisibility(View.VISIBLE);

                                    }
                                    sb = null;
                                    sb = new StringBuilder();
                                }
                            }

                            if (sb.toString().contains("AT+WAP=")) {//设置名字
                                if (sb.toString().contains("+ok")) {
                                    Log.i(TAG, "run: okl ");
//                                    Toast.makeText(me,"修改名字成功",Toast.LENGTH_SHORT).show();
//                                    send("AT+Z");

                                    String contentPwd = mNewPwd.getText().toString().trim();
                                    contentPwd = "AT+WAKEY=WPA2PSK,AES," + contentPwd;
                                    sb = null;
                                    sb = new StringBuilder();
                                    sb.append(contentPwd + "\n");
//            outEt.setText(sb.toString());
                                    send(contentPwd);
                                    Log.i(TAG, "run: okl send hou");

                                } else {
//                                    Toast.makeText(me, "修改名字失败", Toast.LENGTH_SHORT).show();
                                }

                            }
                            if (sb.toString().contains("AT+WAKEY=")) {//设置密码
                                Log.i(TAG, "run: sb.toString() mima:" + sb.toString());
                                if (sb.toString().contains("+ok")) {
                                    Log.i(TAG, "run: okl 修改密码成功");
                                    send("AT+Z");
                                    if (!UILApplication.hasWifiAlter) {
                                        Intent intent = new Intent(AlterUserAndPwdActivity.this, ConnectWifiActivity.class);
                                        intent.putExtra("sucessUser", mNewUser.getText().toString().trim());
                                        intent.putExtra("sucessPwd", mNewPwd.getText().toString().trim());
                                        startActivityForResult(intent, REQUEST_TO_CONNECTWIFI);
                                        finish();
                                    } else {
                                        loading_layout.setVisibility(View.VISIBLE);
                                        mHandler.sendEmptyMessageDelayed(LOADING_DIMISS,10000);//10s后消失并跳转
                                    }

                                    sb = null;
                                    sb = new StringBuilder();
                                } else {
                                    Log.i(TAG, "run: okl 修改密码失败");
//                                    Toast.makeText(me, "修改密码失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                udp.open();
            }
        }).start();
    }

    public void doClose() {
        udp.close();
        if (udp != null) {
            udp = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        doConnect();
        Log.i(TAG, "onStart: ");
        getInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getInfo() {
        //获取原用户名和原密码
        sb = null;
        sb = new StringBuilder();
        Log.i(TAG, "getInfo: nameQueryCommand:" + nameQueryCommand + ",pwdQueryCommand:" + pwdQueryCommand);
        sb.append(nameQueryCommand + "\n");
        send(nameQueryCommand);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
        doClose();
    }

    public void doClick(View view) {
        Log.i(TAG, "doClick: start sb:" + sb.toString());
        if (!UILApplication.hasConnectWifi){
            Toast.makeText(me, "请连接钓鱼报警器的WiFi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nameQuery)) {
            Toast.makeText(me, "请重启硬件", Toast.LENGTH_SHORT).show();
            getInfo();
            return;
        }

        if (!nameQuery.equals(mUser.getText().toString().trim())) {
            Toast.makeText(me, "原用户名出错", Toast.LENGTH_SHORT).show();
            return;
        }

        if (hasPwd) {
            if (!pwdQuery.equals(mPwd.getText().toString().trim())) {
                Toast.makeText(me, "密码出错", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (mNewUser.getText().toString().trim().length() == 0) {
            Toast.makeText(me, "请输入新用户名称", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "doClick: pwd:" + mNewPwd.getText().toString());
        if (mNewPwd.getText().toString().length() <= 7 || mNewPwd.getText().toString().length() >= 63) {
            Toast.makeText(me, "请输入8-64位新密码", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = mNewUser.getText().toString().trim();
        content = "AT+WAP=11BGN," + content + ",CH11";
        sb = null;
        sb = new StringBuilder();
        sb.append(content + "\n");
        Log.i(TAG, "doClick: send:" + sb.toString());
//            outEt.setText(sb.toString());
        send(content);
    }

    public void send(String content) {
        content += "\r";
        final String _content = content;
        new Thread(new Runnable() {
            @Override
            public void run() {
                udp.send("LSD_WIFI:" + _content + "\r");
            }
        }).start();
    }

    public void finish(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TO_CONNECTWIFI) {
                finish();
            }
        }
    }


}
