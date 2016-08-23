package com.fusi.fishingalarm.wifi;

import java.util.ArrayList;
import java.util.List;

import com.fusi.fishingalarm.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WifiConnActivity extends Activity implements OnItemClickListener, OnClickListener {

	private ListView wifiList;
	private Button wifi_switch_btn;
	private Button wifi_scan_btn;
	private Button wifi_cancle_btn;
	private List<ScanResult> list;
	private ScanResult mScanResult;
	private WifiAdmin mWifiAdmin;
	private WifiConnListAdapter mConnList;
	private TextView showConn;
	private ArrayList<WifiElement> wifiElement = new ArrayList<WifiElement>();
	private boolean isOpen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wificonn);
		mWifiAdmin = new WifiAdmin(WifiConnActivity.this);
		initView();
	}

	private void initView() {
		wifiList = (ListView) this.findViewById(R.id.wifi_conn_lv);
		wifi_switch_btn = (Button) this.findViewById(R.id.wifi_conn_switch_btn);
		wifi_scan_btn = (Button) this.findViewById(R.id.wifi_conn_scan_btn);
		wifi_cancle_btn = (Button) this.findViewById(R.id.wifi_conn_cancle_btn);
		showConn = (TextView) this.findViewById(R.id.wifi_show_conn);
		if (mWifiAdmin.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
			wifi_scan_btn.setText("��wifi");
		} else {
			wifi_scan_btn.setText("�ر�wifi");
			isOpen = true;
		}
		showConn.setText("�����ӣ�   " + initShowConn());
		wifi_cancle_btn.setOnClickListener(this);
		wifi_switch_btn.setOnClickListener(this);
		wifiList.setOnItemClickListener(this);
		wifi_scan_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.wifi_conn_cancle_btn:
			finish();
			break;
		case R.id.wifi_conn_switch_btn:
					mConnList = new WifiConnListAdapter(getApplicationContext(), getAllNetWorkList());
					wifiList.setAdapter(mConnList);

			break;
		case R.id.wifi_conn_scan_btn:

			if (isOpen) {
				Toast.makeText(getApplicationContext(), "���ڹر�wifi", Toast.LENGTH_SHORT).show();
				if (mWifiAdmin.closeWifi()) {
					Toast.makeText(getApplicationContext(), "wifi�رճɹ�", Toast.LENGTH_SHORT).show();
					wifi_scan_btn.setText("��wifi");
					isOpen = false;
				} else {
					Toast.makeText(getApplicationContext(), "wifi�ر�ʧ��", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "���ڴ�wifi", Toast.LENGTH_SHORT).show();
				if (mWifiAdmin.OpenWifi()) {
					Toast.makeText(getApplicationContext(), "wifi�򿪳ɹ�", Toast.LENGTH_SHORT).show();
					wifi_scan_btn.setText("�ر�wifi");
					isOpen = true;
				} else {
					Toast.makeText(getApplicationContext(), "wifi��ʧ��", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		default:
			break;
		}
	}

	private String initShowConn() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String s = wifiInfo.getSSID() + "    IP��ַ:" + mWifiAdmin.ipIntToString(wifiInfo.getIpAddress()) + "    Mac��ַ��" + wifiInfo.getMacAddress();
		return s;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		// TODO Auto-generated method stub
		final String ssid = wifiElement.get(position).getSsid();
		Builder dialog = new Builder(WifiConnActivity.this);
		final WifiConfiguration wifiConfiguration = mWifiAdmin.IsExsits(ssid);
		dialog.setTitle("�Ƿ�����");
		dialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (null == wifiConfiguration) {
					setMessage(ssid);
				} else {
					mWifiAdmin.Connect(wifiConfiguration);
				}
			}
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		}).setNeutralButton("�Ƴ�", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (null != wifiConfiguration) {
					int id = wifiConfiguration.networkId;
					System.out.println("id>>>>>>>>>>" + id);
					mWifiAdmin.removeNetworkLink(id);
				}
			}
		}).create();
		dialog.show();
	}

	private void setMessage(final String ssid) {
		Builder dialog = new Builder(WifiConnActivity.this);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout lay = (LinearLayout) inflater.inflate(R.layout.widget_wifi_pwd, null);
		dialog.setView(lay);
		final EditText pwd = (EditText) lay.findViewById(R.id.wifi_pwd_edit);
		dialog.setTitle(ssid);
		dialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				String pwdStr = pwd.getText().toString();
				boolean flag = mWifiAdmin.Connect(ssid, pwdStr, WifiAdmin.WifiCipherType.WIFICIPHER_WPA);
				if (flag) {
					Toast.makeText(getApplicationContext(), "�������ӣ����Ժ�", Toast.LENGTH_SHORT).show();
				} else {
					showLog("���Ӵ���");
				}
			}
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		}).create();
		dialog.show();
	}

	private ArrayList<WifiElement> getAllNetWorkList() {
		// ÿ�ε��ɨ��֮ǰ�����һ�ε�ɨ����
		wifiElement.clear();
		// ��ʼɨ������
		mWifiAdmin.startScan();
		list = mWifiAdmin.getWifiList();
		WifiElement element;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				// �õ�ɨ����
				mScanResult = list.get(i);
				element = new WifiElement();
				element.setSsid(mScanResult.SSID);
				element.setBssid(mScanResult.BSSID);
				element.setCapabilities(mScanResult.capabilities);
				element.setFrequency(mScanResult.frequency);
				element.setLevel(mScanResult.level);
				wifiElement.add(element);
			}
		}
		return wifiElement;
	}

	/**
	 * ��ʾ��Ϣ�Ի���
	 * 
	 * @param msg
	 */
	private void showLog(final String msg) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				Dialog dialog = new Builder(WifiConnActivity.this).setTitle("��ʾ").setMessage(msg).setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();// ����
				// ��ʾ�Ի���
				dialog.show();
			}

		}.execute();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter ins = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(netConnReceiver, ins);
	}

	private BroadcastReceiver netConnReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {

				if (checknet()) {
					Log.d("111111>>>>>>>>>>", "�ɹ�");
					showConn.setText("�����ӣ�   " + initShowConn());
				} else {
					Log.d("22222222>>>>>>>>>>", "ʧ��");
					showConn.setText("���ڳ������ӣ�     " + initShowConn());

				}
			}
		}

	};

	/**
	 * ��ȡ����
	 */
	private NetworkInfo networkInfo;

	/**
	 * �����������
	 * 
	 * @return true �������� false ���ӶϿ�
	 */
	private boolean checknet() {
		ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
		// ��ȡ��������״̬��NetWorkInfo����
		networkInfo = connManager.getActiveNetworkInfo();
		if (null != networkInfo) {
			return networkInfo.isAvailable();
		}
		return false;
	}

}
