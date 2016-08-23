package com.fusi.fishingalarm.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.UILApplication;
import com.fusi.fishingalarm.adapter.WlanListAdapter;
import com.fusi.fishingalarm.bean.WifiAdmin;
import com.fusi.fishingalarm.ui.CheckSwitchButton;
import com.fusi.fishingalarm.ui.ToastShow;
import com.fusi.fishingalarm.utils.UIUtils;
import com.fusi.fishingalarm.wifi.WifiElement;

import java.util.ArrayList;
import java.util.List;

import solid.ren.skinlibrary.base.SkinBaseActivity;


/**
 * Created by user90 on 2016/5/30.
 */
public class WlanListActivity extends BaseActivity {

    private String TAG = "WlanListActivity";

    private ListView mListView;
    List<ScanResult> list;
    private ScanResult mScanResult;
    private WifiAdmin mWifiAdmin;
    private WlanListAdapter mAdapter;
    private ArrayList<WifiElement> wifiElement = new ArrayList<WifiElement>();
    private boolean isOpen = false;
    //    用户选择listview的position
    int pos = 0;

    //wifi开关
    CheckSwitchButton switch_wifi;

    private UIUtils.AlertConfirm mAlertConfirm = new UIUtils.AlertConfirm() {
        @Override
        public void onConfirm(int tag, String content) {
            if (TextUtils.isEmpty(content)) {
                UIUtils.showAlterDialog(WlanListActivity.this, "提示", "密码不能为空");
                Log.i(TAG, "onConfirm: pos:" + pos);
                mAdapter.notifyDataSetChanged();
            } else {//写死
                Log.i(TAG, "onConfirm: pos:" + pos + ",content:" + content);
                boolean flag = mWifiAdmin.Connect(list.get(pos).SSID, content, WifiAdmin.WifiCipherType.WIFICIPHER_WPA);
                if (flag) {
                    ToastShow.showToastCenter(getApplicationContext(), "正在连接，请稍后");
                } else {
//                    showLog("链接错误");
                    UIUtils.showAlterDialog(WlanListActivity.this, "提示", "连接错误");
                    Log.i(TAG, "onConfirm: pos:" + pos);
                    mAdapter.notifyDataSetChanged();
                }
                mAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onCancel() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wlan_list);
        mWifiAdmin = new WifiAdmin(WlanListActivity.this);
        initView();
        initData();

    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.listview);
        switch_wifi = (CheckSwitchButton) findViewById(R.id.switch_wifi);
    }

    private void initListView() {
        Log.i(TAG, "initListView: ");
        mAdapter = new WlanListAdapter(WlanListActivity.this, getAllNetWorkList());
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

//    private void initListView() {
//        mAdapter = new WlanListAdapter(WlanListActivity.this, mList);
//        mListView.setAdapter(mAdapter);
//    }

    private void initData() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                 String ssid = list.get(pos).SSID;
                Log.i(TAG, "onItemClick: list.get(pos).SSID:"+list.get(pos).SSID);
                 WifiConfiguration wifiConfiguration = mWifiAdmin.IsExsits(ssid);
                if (null == wifiConfiguration) {
                    Log.i(TAG, "onItemClick: null == wifiConfiguration");
                    //                写死
                    UIUtils.createInputMoreInfoDialog(WlanListActivity.this, ssid, getString(R.string.scan_strength), getString(R.string.wlan_security), mAlertConfirm, 1);
                } else {
                    Log.i(TAG, "onItemClick: null != wifiConfiguration");
                    boolean flag = mWifiAdmin.Connect(wifiConfiguration);
                    if (flag) {
                        ToastShow.showToastCenter(getApplicationContext(), "正在连接，请稍后");
                    } else {
//                    showLog("链接错误");
                        UIUtils.showAlterDialog(WlanListActivity.this, "提示", "连接错误");
                        Log.i(TAG, "onConfirm: pos:wifiConfiguration!=null " + pos);
//                        mAdapter.notifyDataSetChanged();

                    }
                }
            }
        });


        Log.i(TAG, "initData:isChecked: " + switch_wifi.isChecked());
        if (mWifiAdmin.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
//            打开wifi
            switch_wifi.setChecked(true);
        } else {
//            关闭wifi
            switch_wifi.setChecked(false);
            isOpen = true;
            initListView();
        }
        switch_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: isChecked:" + isChecked);
//                isChecked=false时，为青色
                if (isChecked) {//关闭wifi,
                    ToastShow.showToastCenter(getApplicationContext(), "正在关闭wifi");
                    if (mWifiAdmin.closeWifi()) {
                        ToastShow.showToastCenter(getBaseContext(), "wifi关闭成功");
                        isOpen = false;
                        wifiElement.clear();
                        mAdapter.notifyDataSetChanged();
                    } else {
                        ToastShow.showToastCenter(getBaseContext(), "wifi关闭失败");
                    }
                } else {
                    ToastShow.showToastCenter(getBaseContext(), "正在打开wifi");
                    if (mWifiAdmin.OpenWifi()) {
                        ToastShow.showToastCenter(getBaseContext(), "wifi打开成功");
                        isOpen = true;
                        scan(null);
                    } else {
                        ToastShow.showToastCenter(getBaseContext(), "wifi打开失败");
                    }
                }
            }
        });
    }

    /*扫描*/
    public void scan(View view) {
        if (mWifiAdmin.isWifiOpen()){
            ToastShow.showToastCenter(getBaseContext(), "扫描");
            initListView();
        }else {
            ToastShow.showToastCenter(getBaseContext(), "WiFi未打开");
        }

    }

    /*扫描*/
    public void addNetworking(View view) {
        ToastShow.showToastCenter(getBaseContext(), "添加网络");
    }

    private ArrayList<WifiElement> getAllNetWorkList() {
        // 每次点击扫描之前清空上一次的扫描结果
        wifiElement.clear();
        // 开始扫描网络
        mWifiAdmin.startScan();
        list = mWifiAdmin.getWifiList();
        WifiElement element;

        ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager wifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                // 得到扫描结果
                mScanResult = list.get(i);
                element = new WifiElement();
                element.setSsid(mScanResult.SSID);
                element.setBssid(mScanResult.BSSID);
                element.setCapabilities(mScanResult.capabilities);
                element.setFrequency(mScanResult.frequency);
                element.setLevel(mScanResult.level);
//                Log.i(TAG, "getAllNetWorkList: wifiInfo.getSSID():" + wifiInfo.getSSID() + ",mScanResult.SSID:" + mScanResult.SSID + ",mScanResult.BSSID:" + mScanResult.BSSID + ",wifiInfo.getBSSID():" + wifiInfo.getBSSID());
                if (wifiNetworkInfo.isConnected()&&mScanResult.BSSID.equals(wifiInfo.getBSSID())) {
                    element.setConnact(true);
                }
                wifiElement.add(element);
            }
        }
        return wifiElement;
    }


    private String initShowConn() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String s = wifiInfo.getSSID() + "    IP地址:" + mWifiAdmin.ipIntToString(wifiInfo.getIpAddress()) + "    Mac地址：" + wifiInfo.getMacAddress();
        return s;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter ins = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(netConnReceiver, ins);
//        hasTop=true;
        if (mWifiAdmin.isWifiOpen()){
            initListView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netConnReceiver);
//        hasTop=false;
    }

    private BroadcastReceiver netConnReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()) && !switch_wifi.isChecked()) {
//                if (hasTop){
                    initListView();
                    if (checknet()) {
                        Log.d("111111>>>>>>>>>>", "成功");
//                    showConn.setText("已连接：   " + initShowConn());
//                    ToastShow.showToastCenter(getBaseContext(), "已连接：   " + initShowConn()
                    } else {
                        Log.d("22222222>>>>>>>>>>", "失败");
//                    showConn.setText("正在尝试连接：     " + initShowConn());
                        Log.i(TAG, "onReceive: chang");
//                    ToastShow.showToastCenter(getBaseContext(), "正在尝试连接：     " + initShowConn()

                    }
//                }

            }
        }

    };

    /**
     * 获取网络
     */
    private NetworkInfo networkInfo;

    /**
     * 监测网络链接
     *
     * @return true 链接正常 false 链接断开
     */
    private boolean checknet() {
        ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        // 获取代表联网状态的NetWorkInfo对象
        networkInfo = connManager.getActiveNetworkInfo();
        if (null != networkInfo) {
            return networkInfo.isAvailable();
        }
        return false;
    }
    public void finish(View view){
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}
