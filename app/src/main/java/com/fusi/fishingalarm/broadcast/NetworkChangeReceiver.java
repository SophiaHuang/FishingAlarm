package com.fusi.fishingalarm.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.fusi.fishingalarm.UILApplication;
import com.fusi.fishingalarm.ui.ToastShow;
import com.fusi.fishingalarm.utils.StringUtils;

/**
 * Created by user90 on 2016/6/13.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
            //signal strength changed
        } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
            Log.i(TAG, "onReceive:网络状态改变 ");
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                Log.i(TAG, "onReceive: wifi网络连接断开");
                UILApplication.hasConnectWifi = false;
            } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                DhcpInfo infoD = wifiManager.getDhcpInfo();
                //判断主机地址
                if (StringUtils.ipIntToString(infoD.serverAddress).equals(UILApplication.HOST)) {
                    UILApplication.hasConnectWifi = true;
                } else {
                    ToastShow.showToastCenter(context, "未能连接服务器wifi，连接服务异常");
                    UILApplication.hasConnectWifi = false;
                }
            }
        } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {//wifi打开与否
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                Log.i(TAG, "onReceive: 系统关闭wifi");
//                Toast.makeText(context, "系统关闭wifi，连接服务异常", Toast.LENGTH_SHORT).show();
                UILApplication.hasConnectWifi = false;
            } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                Log.i(TAG, "onReceive: 系统开启wifi");
//                Toast.makeText(context, "系统开启wifi", Toast.LENGTH_SHORT).show();
//                UILApplication.hasConnectWifi = true;
           /*     WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                DhcpInfo infoD = wifiManager.getDhcpInfo();
                //判断主机地址
                if (StringUtils.ipIntToString(infoD.serverAddress).equals(UILApplication.HOST)) {
                    UILApplication.hasConnectWifi = true;
                } else {
                    Toast.makeText(context, "未能连接服务器wifi，连接服务异常", Toast.LENGTH_SHORT).show();
                    UILApplication.hasConnectWifi = false;
                }*/
            }
        }


    }


}
