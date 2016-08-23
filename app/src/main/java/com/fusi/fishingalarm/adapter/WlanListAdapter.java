package com.fusi.fishingalarm.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.UILApplication;
import com.fusi.fishingalarm.activity.AlterUserAndPwdActivity;
import com.fusi.fishingalarm.ui.ToastShow;
import com.fusi.fishingalarm.ui.WifiSignal;
import com.fusi.fishingalarm.utils.StringUtils;
import com.fusi.fishingalarm.wifi.WifiElement;
import com.lsd.atcmd.AtControlActivity;
import com.lsd.atcmd.Device;
import com.lsd.atcmd.DeviceListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user90 on 2016/5/30.
 */
public class WlanListAdapter extends BaseAdapter {

    private String TAG = "WlanListAdapter";

    private Context mContext;
    private List<WifiElement> mLists = new ArrayList<>();
    private WlanHolder mHolder;

    WifiManager wifiManager;
    WifiInfo wifiInfo;
    DhcpInfo infoD;

    public WlanListAdapter(Context mContext, List<WifiElement> mLists) {
        this.mContext = mContext;
        this.mLists = mLists;
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        infoD = wifiManager.getDhcpInfo();
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos=position;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_wlan_list, null);
            mHolder = new WlanHolder();
            mHolder.name = (TextView) convertView.findViewById(R.id.name);
            mHolder.contact = (TextView) convertView.findViewById(R.id.contact);
            mHolder.right = (ImageView) convertView.findViewById(R.id.img_right);
//            mHolder.signal = (WifiSignal) convertView.findViewById(R.id.signal);
            convertView.setTag(mHolder);
        } else {
            mHolder = (WlanHolder) convertView.getTag();
        }
//        信号强弱没有设置，控件弄好记得设置哦
        mHolder.name.setText(mLists.get(position).getSsid());
        if (mLists.get(position).isConnact()){
            mHolder.contact.setVisibility(View.VISIBLE);
        }else {
            mHolder.contact.setVisibility(View.INVISIBLE);
        }

        mHolder.right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: getSsid:"+mLists.get(pos).getSsid());
                Log.i(TAG, "onClick: getSsid:"+mLists.get(pos));
                Log.i(TAG, "onClick: infoD.ipAddress:"+StringUtils.ipIntToString(infoD.ipAddress));
                Log.i(TAG, "onClick: serverAddress:"+StringUtils.ipIntToString(infoD.serverAddress));
                if (mLists.get(pos).isConnact()){
                    if (StringUtils.ipIntToString(infoD.serverAddress).equals(UILApplication.HOST)) {

                        Device device = new Device(UILApplication.HOST, mLists.get(pos).getBssid(), mLists.get(pos).getSsid(), 8800);
                        Intent intent = new Intent(mContext, AlterUserAndPwdActivity.class);
//                    Intent intent = new Intent(mContext, DeviceListActivity.class);
                        intent.putExtra("DEVICE", device);
                        mContext.startActivity(intent);
                    } else {
                        ToastShow.showToastCenter(mContext, "该WiFi不是钓鱼报警器的WiFi，不能修改WiFi信息");

                    }
                }else {
                    ToastShow.showToastCenter(mContext, "该WiFi未连接，不能修改WiFi信息");
                }


            }
        });
//        mHolder.signal.setSignal(Math.abs(mLists.get(position).getLevel()));
        return convertView;
    }

    class WlanHolder {
        TextView name;
        //        已连接则显示文字
        TextView contact;
        ImageView right;
//        WifiSignal signal;
    }
}
