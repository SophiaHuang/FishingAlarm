package com.lsd.atcmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.ui.WifiSignal;
import com.lsd.atcmd.UdpBroadcast.DataListener;
import com.lsd.atcmd.smartconfig.lib.WifiAdmin;

/**
 * @ClassName: DeviceListActivity.java
 * @Description:
 */
public class DeviceListActivity extends Activity {

    String TAG="DeviceListActivity";

    UdpBroadcast searcher;

    ListView listView;
    List<Device> devices = new ArrayList<Device>();
    DeviceAdapter mAdapter;
    DeviceListActivity me = this;

    private int WHAT_FIND_DEVICES = 0x1000;

    UdpBroadDataListener dataListener;

    private boolean SEARCHING = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.device_list_layout);

        mAdapter = new DeviceAdapter(devices, this);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(mAdapter);

		/*Device dev1 = new Device();
        dev1.ip = "192.168.2.108";
		dev1.mac = "AABBCCDDEEFF";
		dev1.name = "F205";
		
		Device dev2 = new Device();
		dev2.ip = "192.168.2.101";
		dev2.mac = "AABBCCDDEEFD";
		dev2.name = "F205";
		devices.add(dev1);
		devices.add(dev2);*/

        mAdapter.notifyDataSetChanged();
        dataListener = new UdpBroadDataListener();
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = devices.get(position);
                Intent intent = new Intent(me, AtControlActivity.class);
                intent.putExtra("DEVICE", device);
                me.startActivity(intent);
            }
        });
        TextView versionName = (TextView) findViewById(R.id.versionName);
        versionName.setText("V" + getAppVersionName(this));

        WifiAdmin wifiAdmin = new WifiAdmin(me);
        Log.i(TAG, "onCreate: wifiAdmin.getBroadcastIp():"+wifiAdmin.getBroadcastIp());
        final UdpBroadcast testUdp = new UdpBroadcast(wifiAdmin.getBroadcastIp(), new DataListener() {

            @Override
            public void onReceive(Set<String> set) {

            }
        });
        testUdp.open();

        Button testBtn = (Button) findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(me, "OnClickListener", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        testUdp.send("LSD_WIFI_123".getBytes());
                    }
                }).start();
            }
        });
    }

    private String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    @Override
    protected void onStart() {
        super.onStart();
        devices.clear();
        mAdapter.notifyDataSetChanged();
        openSeracher();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeSearcher();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_FIND_DEVICES) {
                Set<String> set = (Set<String>) msg.obj;
                if (set.size() == 0) {
                    Toast.makeText(me, "没有找到设备...", Toast.LENGTH_SHORT).show();
                } else {
                    for (String str : set) {
                        String[] parts = str.split(",");
                        if (parts.length == 4) {
                            String name = parts[1];
                            name = name.replace("\r\n", "");
                            devices.add(new Device(parts[2], parts[0], name, Integer.parseInt(parts[3])));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    };

    public void openSeracher() {
        final WifiAdmin wifiAdmin = new WifiAdmin(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                searcher = new UdpBroadcast(wifiAdmin.getBroadcastIp(), dataListener);
                Log.i(TAG, "run: wifiAdmin.getBroadcastIp():"+wifiAdmin.getBroadcastIp());
                searcher.open();
                doSearch();
            }
        }).start();
    }

    public void closeSearcher() {
        if (searcher != null) {
            searcher.close();
        }
        searcher = null;
    }


    public void searchDevice(View view) {
        devices.clear();
        mAdapter.notifyDataSetChanged();
        doSearch();
    }

    public void doSearch() {
        if (!SEARCHING) {
            SEARCHING = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    searcher.send("LSD_WIFI".getBytes());
                    searcher.receive();
                }
            }).start();
        }
        me.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(me, "正在搜索...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class UdpBroadDataListener implements DataListener {
        @Override
        public void onReceive(Set<String> set) {
            SEARCHING = false;
            mHandler.sendMessage(mHandler.obtainMessage(WHAT_FIND_DEVICES, set));
        }
    }


    class DeviceAdapter extends BaseAdapter {
        private List<Device> devices;
        private Activity act;

        private WlanHolder mHolder;

        public DeviceAdapter(List<Device> devices, Activity act) {
            this.devices = devices;
            this.act = act;
        }

        @Override
        public int getCount() {
            return this.devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
		/*	DeviceItemView itemView = new DeviceItemView(act,devices.get(position));
			convertView = itemView;
			return convertView;*/

            if (convertView == null) {
                convertView = LayoutInflater.from(act).inflate(R.layout.adapter_wlan_list, null);
                mHolder=new WlanHolder();
                mHolder.name= (TextView) convertView.findViewById(R.id.name);
                mHolder.contact= (TextView) convertView.findViewById(R.id.contact);
                mHolder.right= (ImageView) convertView.findViewById(R.id.img_right);
//                mHolder.signal= (WifiSignal) convertView.findViewById(R.id.signal);
                convertView.setTag(mHolder);
            }else {
                mHolder= (WlanHolder) convertView.getTag();
            }


            return convertView;
        }

        class WlanHolder {
            TextView name;
            //        已连接则显示文字
            TextView contact;
            ImageView right;
            WifiSignal signal;
        }

    }

    class DeviceItemView extends LinearLayout {

        public DeviceItemView(Context context, Device device) {
            super(context);
            float text_sp = context.getResources().getDimension(R.dimen.text_sp);
            setOrientation(LinearLayout.HORIZONTAL);
            AbsListView.LayoutParams d_params = new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, 100);
            this.setLayoutParams(d_params);
            this.setGravity(Gravity.CENTER_VERTICAL);
            TextView lineView = new TextView(context);
            LayoutParams m_params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            m_params.leftMargin = 20;
            lineView.setLayoutParams(m_params);
            lineView.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_sp);
            lineView.setText(device.mac + ", " + device.ip + ", " + device.name + ", " + device.port);
            lineView.setGravity(Gravity.CENTER_VERTICAL);
            lineView.setTextColor(Color.BLACK);

            addView(lineView);
			/*
			TextView macView = new TextView(context);
			LinearLayout.LayoutParams m_params =new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			m_params.leftMargin = 20;
			macView.setLayoutParams(m_params);
			macView.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_sp);
			macView.setText(device.mac);
			macView.setGravity(Gravity.CENTER_VERTICAL);
			macView.setTextColor(Color.BLACK);
			TextView ipView = new TextView(context);
			LinearLayout.LayoutParams i_params =new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			ipView.setLayoutParams(i_params);
			ipView.setText(device.ip);
			ipView.setGravity(Gravity.CENTER_VERTICAL);
			ipView.setTextColor(Color.BLACK);
			i_params.leftMargin = 20;
			ipView.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_sp);
			
			TextView nameView = new TextView(context);
			LinearLayout.LayoutParams n_params =new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			nameView.setLayoutParams(n_params);
			nameView.setText(device.name);
			nameView.setGravity(Gravity.BOTTOM);
			nameView.setTextColor(Color.BLACK);
			n_params.leftMargin = 20;
			nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_sp);
			
			TextView portView = new TextView(context);
			LinearLayout.LayoutParams p_params =new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			portView.setLayoutParams(p_params);
			portView.setText(String.valueOf(device.port));
			portView.setGravity(Gravity.CENTER_VERTICAL);
			portView.setTextColor(Color.BLACK);
			p_params.leftMargin = 20;
			portView.setTextSize(TypedValue.COMPLEX_UNIT_PX, text_sp);
			addView(macView);
			addView(ipView);
			addView(nameView);
			addView(portView);*/
        }

    }


}
