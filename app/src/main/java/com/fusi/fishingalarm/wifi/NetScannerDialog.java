package com.fusi.fishingalarm.wifi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fusi.fishingalarm.R;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by SYSTEM on 2016/7/19.
 */
public class NetScannerDialog extends Dialog {

    private ListView lv_wifi_list;
    private ProgressBar progress;
    private ArrayList<ScanResult> list = new ArrayList<>();
    private NetInfoAdapter mAdapter;
    private WifiManager wifiManager;

    private static final int SCANNER_RESULT = 0x1111;
    INotifyShareAgain shareAgain;
    private TextView tv_title;

    public NetScannerDialog(Context context,INotifyShareAgain shareAgain) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_net);
        WindowManager windowManager = getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 2 / 3); //设置宽度
        getWindow().setAttributes(lp);
        this.shareAgain = shareAgain;
        initViews();
        initEvents();
        scanner();
    }

    private void initViews() {
        lv_wifi_list = (ListView) findViewById(R.id.lv_wifi_list);
        tv_title =(TextView)findViewById(R.id.tv_title);
        progress = (ProgressBar) findViewById(R.id.progress);
        lv_wifi_list.setEmptyView(progress);
        mAdapter = new NetInfoAdapter(list);
        lv_wifi_list.setAdapter(mAdapter);
        wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
    }

    private void initEvents() {
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANNER_RESULT:
                    tv_title.setText("请选择WiFi");
                    ArrayList<ScanResult> results = (ArrayList<ScanResult>) msg.obj;
                    progress.setVisibility(View.GONE);
                    if (!results.isEmpty()) {
                        for (int i = 0; i < results.size(); i++) {
                            if (results.get(i).SSID.startsWith("FW")) {
                                results.remove(results.get(i));
                            }
                        }
                        if (!results.isEmpty()) {
                            list.clear();
                            list.addAll(results);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            new AlertDialog.Builder(getContext()).setMessage("附近没有可用的WiFi,是否打开流量?").setNegativeButton("取消", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }
                            }).setPositiveButton("确定", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    toggleMobileData(getContext(),true);
                                    shareAgain.tryShare();
                                    dismiss();
                                }
                            });
                        }
                    }
                    break;
            }
        }
    };

    private void toggleMobileData(Context context, boolean enabled){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Method setMobileDataEnabl;
        try {
            setMobileDataEnabl = connectivityManager.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
            setMobileDataEnabl.invoke(connectivityManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scanner() {
        new Thread() {
            @Override
            public void run() {
                ArrayList<ScanResult> list = (ArrayList<ScanResult>) wifiManager.getScanResults();
                sortByLevel(list);
                mHandler.obtainMessage(SCANNER_RESULT, list).sendToTarget();
            }
        }.start();
    }

    private void sortByLevel(ArrayList<ScanResult> list) {
        for (int i = 0; i < list.size(); i++)
            for (int j = 1; j < list.size(); j++) {
                if (list.get(i).level < list.get(j).level)    //level属性即为强度
                {
                    ScanResult temp = null;
                    temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
    }


    private class NetInfoAdapter extends BaseAdapter {
        private ArrayList<ScanResult> wifis;

        public NetInfoAdapter(ArrayList<ScanResult> list) {
            if (list == null) wifis = new ArrayList<ScanResult>();
            else wifis = list;
        }

        @Override
        public int getCount() {
            return wifis.size();
        }

        @Override
        public Object getItem(int position) {
            return wifis.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold v = null;
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.item_wifi_list, null);
                v = new ViewHold();
                v.textView = (TextView) convertView.findViewById(R.id.tv_ssid);
                v.imageView = (ImageView) convertView.findViewById(R.id.iv_level);
                convertView.setTag(v);
            } else {
                v = (ViewHold) convertView.getTag();
            }
            ScanResult wifiInfo = wifis.get(position);
            v.textView.setText(wifiInfo.SSID);
//            int level = WifiManager.calculateSignalLevel(wifiInfo.level, 5);
//            v.imageView.setImageLevel(level);
            return convertView;
        }

        private class ViewHold {
            TextView textView;
            ImageView imageView;
        }
    }

    public interface INotifyShareAgain{
        public void tryShare();
    }
}
