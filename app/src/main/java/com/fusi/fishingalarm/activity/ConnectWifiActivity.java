package com.fusi.fishingalarm.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.UILApplication;
import com.fusi.fishingalarm.bean.WifiAdmin;
import com.fusi.fishingalarm.ui.ToastShow;
import com.fusi.fishingalarm.utils.UIUtils;

import solid.ren.skinlibrary.base.SkinBaseActivity;


/**
 * Created by user90 on 2016/7/26.
 */
public class ConnectWifiActivity extends BaseActivity {

    String TAG = "ConnectWifiActivity";

    EditText mInputUser, mInputPwd;
    String sucessUser, sucessPwd;
    String inputUser, inputPwd;
    private WifiAdmin mWifiAdmin;
    NetworkInfo wifiNetworkInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //重置服务器
//        UILApplication.commandCount=12;
        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.activity_alter_user_and_pwd_contact);
        mInputUser = (EditText) findViewById(R.id.sucessUser);
        mInputPwd = (EditText) findViewById(R.id.sucessPwd);
        Intent intent = getIntent();
        if (intent != null) {
            sucessUser = intent.getExtras().getString("sucessUser");
            sucessPwd = intent.getExtras().getString("sucessPwd");
            Log.i(TAG, "onCreate: sucessUser:" + sucessUser + ",sucessPwd:" + sucessPwd);
        }
        mWifiAdmin = new WifiAdmin(ConnectWifiActivity.this);
        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    //取消连接
    public void cancelContact(View view) {
        finish();
    }

    //取消连接
    public void contact(View view) {
//        do sth
        inputUser = mInputUser.getText().toString().trim();
        inputPwd = mInputPwd.getText().toString().trim();
        if (sucessUser.equals(inputUser) && sucessPwd.equals(inputPwd)) {
            Log.i(TAG, "contact: ");
             WifiConfiguration wifiConfiguration = mWifiAdmin.IsExsits(inputUser);
            if (null == wifiConfiguration) {
                Log.i(TAG, "contact: wifiConfiguration==null");
                boolean flag = mWifiAdmin.Connect(inputUser, inputPwd, WifiAdmin.WifiCipherType.WIFICIPHER_WPA);
                Log.i(TAG, "contact: equsl" + ",flag:" + flag + ",inputUser:" + inputUser + ",inputPwd:" + inputPwd);
                if (flag) {
                    Log.i(TAG, "contact: flag");
                    ToastShow.showToastCenter(getApplicationContext(), "正在连接，请稍后");
                    Intent intent=new Intent();
                    setResult(RESULT_OK,intent);
                    finish();

                } else {
//                    showLog("链接错误");
                    UIUtils.showAlterDialog(ConnectWifiActivity.this, "提示", "连接错误");
                    int netId = mWifiAdmin.getConnNetId();
                    mWifiAdmin.disConnectionWifi(netId);

                }
            } else {
                Log.i(TAG, "contact: wifiConfiguration!=null");
                int netId = mWifiAdmin.getConnNetId();
                mWifiAdmin.disConnectionWifi(netId);
            }

        } else {
            UIUtils.showAlterDialog(ConnectWifiActivity.this, "提示", "用户名或密码出错");
        }

    }

    public void finish(View view) {
        finish();
    }
}
