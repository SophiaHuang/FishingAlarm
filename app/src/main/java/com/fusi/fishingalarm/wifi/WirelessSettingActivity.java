//package com.fusi.fishingalarm.wifi;
//
//import java.util.List;
//
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.wifi.WifiManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.Parcelable;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.hooper.java.base.widget.CustomerToast;
//import com.hooper.java.ui.dialog.DialogEditPwd;
//import com.hooper.java.ui.dialog.LoadingDialog;
//import com.hooper.java.utils.AndroidTools;
//import com.hooper.java.utils.Globals;
//import com.hooper.java.utils.PrefrencesUtils;
//import com.hooper.java.utils.WifiUtils;
//
//import edu.whu.app.hcs_bed_phone.R;
//
//
//public class WirelessSettingActivity extends Activity {
//
//
//	private TextView txt;
//	private ListView listview;
//	private TextView versionName;
//	WifiUtils wifiUtils ;
//	WifiListAdapter adapter;
//	List<String> results;
//	LoadingDialog dialog ;
//	String SSID = "";
//	String pwd = "";
//	Button btn_restart;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_third);
//		txt = (TextView) findViewById(R.id.show_state);
//		Button txt_back = (Button) findViewById(R.id.third_back);
//		listview = (ListView) findViewById(R.id.third_mylistView);
//		Button btn_scan = (Button) findViewById(R.id.third_scan);
//		versionName = (TextView) findViewById(R.id.versionName);
//		btn_restart = (Button) findViewById(R.id.restart_wifi);
//		try {
//			String versionStr = AndroidTools.getVerName(WirelessSettingActivity.this);
//			versionName.setText("版本号:"+versionStr);
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		wifiUtils = new WifiUtils(getApplicationContext());
//		SSID = PrefrencesUtils.getLocalSSID(getApplicationContext());
////		if(SSID.equals("")){
////			SSID = Globals.DEFAULT_SSID;
////		}
//		pwd = PrefrencesUtils.getLocalSSIDPWD(getApplicationContext());
//		if(pwd.equals("")){
//			pwd = Globals.DEFAULT_PWD;
//		}
//		registBroadcast();
//		btn_scan.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(!wifiUtils.isWifiOpen()){
//					openWifi();
//					return;
//				}
//				mHandler.sendEmptyMessage(1);
//			}
//		});
//		txt_back.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
//		btn_restart.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View view) {
//				boolean flag = ((WifiManager)getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
//				if(flag){
//					((WifiManager)getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(false);
//				}else{
//					mHandler.sendEmptyMessage(1);
//				}
//
//			}
//		});
//		openWifi();
////		wifiUtils.openWifi();
////		if(wifiUtils.isWifiOpen()){
////			mHandler.sendEmptyMessage(1);
////		}else{
////			wifiUtils.openWifi();
////		}
//	}
//
//	private void showDilog(String str) {
//		if(dialog != null){
//			dialog.dimissDialog();
//			dialog = null;
//		}
//		dialog = new LoadingDialog();
//		dialog.showDialog(WirelessSettingActivity.this,str);
//	}
//
//
//	private void dimissDialog() {
//		if(dialog != null){
//			dialog.dimissDialog();
//			dialog = null;
//		}
//	}
//
//
//	private Handler mHandler = new Handler(){
//		public void handleMessage(Message msg) {
//			if(msg.what == 1){
//				wifiUtils.startScan();
//				sendEmptyMessageDelayed(2, 3000);
//				showDilog("正在扫描");
//			}else if(msg.what == 2){
//				results = wifiUtils.getScanResult(wifiUtils.isConnectedSSID(SSID));
//				if(adapter == null){
//					adapter = new WifiListAdapter(WirelessSettingActivity.this, results);
//					listview.setAdapter(adapter);
//				}else{
//					adapter.update(results);
//				}
//				dimissDialog();
//				if(SSID == null || SSID.equals("")){
//					return;
//				}
//				new Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						if(!wifiUtils.isConnectedSSID(SSID)){
//							connectWifi();
//						}
//					}
//				}, 1000);
//
//			}
//		};
//	};
//
//
//
//	private void registBroadcast() {
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//		//		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//		registerReceiver(wifiBroadcastReceiver, filter);
//	}
//
//
//	@Override
//	protected void onDestroy() {
//		unregisterReceiver(wifiBroadcastReceiver);
//		super.onDestroy();
//	}
//
//	private void notifyList() {
//		results = wifiUtils.getScanResult(wifiUtils.isConnectedSSID(SSID));
//		if(adapter == null){
//			adapter = new WifiListAdapter(WirelessSettingActivity.this, results);
//			listview.setAdapter(adapter);
//		}else{
//			adapter.update(results);
//		}
//	}
//
//
//	private BroadcastReceiver wifiBroadcastReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关
//				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
//				Log.e("H3c", "wifiState" + wifiState);
//				switch (wifiState) {
//				case WifiManager.WIFI_STATE_DISABLED:  //"Wi-Fi已关闭"
//					Log.e("wifiBroadcastReceiver", "Wi-Fi已关闭");
//					txt.setText("Wi-Fi已关闭");
////					notifyList();
//					openWifi();
//					break;
//				case WifiManager.WIFI_STATE_DISABLING://"Wi-Fi正在关闭"
//					Log.e("wifiBroadcastReceiver", "Wi-Fi正在关闭");
//					txt.setText("正在关闭Wi-Fi");
//					break;
//				case WifiManager.WIFI_STATE_ENABLED://Wi-Fi已打开
//					txt.setText("Wi-Fi已打开");
//					Log.e("wifiBroadcastReceiver", "Wi-Fi已打开");
//					if(openFlag){
//						dimissDialog();
//						mHandler.sendEmptyMessageDelayed(1, 1000);
//					}
//					notifyList();
//					break;
//				case WifiManager.WIFI_STATE_ENABLING://Wi-Fi正在打开
//					txt.setText("正在打开Wi-Fi");
//					Log.e("wifiBroadcastReceiver", "Wi-Fi正在打开");
//					break;
//					//
//				}
//			}
//			// 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
//			// 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，当然刚打开wifi肯定还没有连接到有效的无线
//			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
//				Parcelable parcelableExtra = intent
//						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//				if (null != parcelableExtra) {
//					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
//					NetworkInfo.State state = networkInfo.getState();
//					if(state == NetworkInfo.State.CONNECTED){
//						Log.e("wifiBroadcastReceiver", "Wi-Fi已连接");
//						txt.setText("Wi-Fi已连接");
//						if(adapter != null){
//							results = wifiUtils.getScanResult(wifiUtils.isConnectedSSID(SSID));
//							adapter.update(results);
//						}
//						dimissDialog();
//						PrefrencesUtils.setLocalSSID(getApplicationContext(), wifiUtils.getConnectedSSID());
//						PrefrencesUtils.setLocalSSIDPWD(context, pwd);
//						wifiUtils.clearWifiConfig(getApplicationContext());
//					}else if(state == NetworkInfo.State.DISCONNECTED){
//						Log.e("wifiBroadcastReceiver", "Wi-Fi未连接");
//						txt.setText("Wi-Fi未连接");
//					}
//				}
//			}
//
//		}
//
//
//	};
//
//
//	boolean openFlag = false;
//	private void openWifi() {
//		openFlag = true;
//		((WifiManager)getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(true);
//		showDilog("正在打开Wi-Fi");
////		wifiUtils.openWifi();
//
//	}
//
//
//
//
//	private void connectWifi() {
//		if(SSID.equals("") || SSID == null)
//			return;
//		boolean flag = wifiUtils.connectWiFI(SSID, pwd);
//		if(flag){
//			txt.setText("正在连接Wi-Fi");
//			showDilog("正在连接"+SSID);
//		}else{
//			txt.setText("Wi-Fi参数错误");
//		}
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				int Timeout = 15 * 10;
//				int current = 0;
//				while(true){
//					if(wifiUtils.isConnectedSSID(SSID)){
//						break;
//					}else if(current >= Timeout){
//						runOnUiThread(new Runnable() {
//							public void run() {
//								Toast.makeText(WirelessSettingActivity.this, "连接超时！", Toast.LENGTH_SHORT).show();
//								dimissDialog();
//							}
//						});
//						break;
//					}
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					current++;
//				}
//			}
//		}).start();
//	}
//
//
//	private class WifiListAdapter extends BaseAdapter{
//		Activity mContext;
//		List<String> dataList;
//		public WifiListAdapter(Activity mContext,List<String> dataList) {
//			this.mContext = mContext;
//			this.dataList = dataList;
//		}
//
//		@Override
//		public int getCount() {
//			if(dataList == null)
//				return 0;
//			return dataList.size();
//		}
//
//
//		public void update(List<String> dataList) {
//			this.dataList = dataList;
//			notifyDataSetChanged();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return position;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View view = View.inflate(mContext, R.layout.activity_third_list_item, null);
//			TextView wifi_isconneted = (TextView) view.findViewById(R.id.text_showInfo);
//			TextView wifi_ssid = (TextView) view.findViewById(R.id.mytext);
//			final String ssid = dataList.get(position);
//			final String connectedSSID = wifiUtils.getConnectedSSID();
//			if(connectedSSID != null &&
//					!connectedSSID.equals("") && wifiUtils.isConnectedSSID(ssid)){
//				wifi_isconneted.setText("当前已连接");
//			}else{
//				wifi_isconneted.setText("");
//			}
//			wifi_ssid.setText(ssid);
//			view.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if(connectedSSID.equals(ssid)){
//						CustomerToast.showToast(mContext, "已经连接上该Wi-Fi");
//						return;
//					}
//					DialogEditPwd dialogEdit = new DialogEditPwd(mContext);
//					dialogEdit.showDialog("请输入Wi-Fi密码：",pwd,new DialogEditPwd.OnDialogButtonClickListener() {
//						@Override
//						public void onPositiveButtonClick(String txt) {
//							showDilog("正在连接到"+ssid);
//							boolean flag = wifiUtils.connectWiFI(ssid, txt);
//							if(flag){
//								Log.e("flag", "true");
//								time = 0;
//								Message msg = new Message();
//								msg.what = 1;
//								Bundle bd = new Bundle();
//								bd.putString("ssid", ssid);
//								bd.putString("pwd", txt);
//								msg.setData(bd);
//								adaphandler.sendMessageDelayed(msg, 1000);
//							}else{
////								CustomerToast.showToast(mContext, "连接网络失败！");
//								Log.e("flag", "false");
//								dimissDialog();
//							}
//
//						}
//
//						@Override
//						public void onNegativeButtonClick() {
//
//						}
//					});
//				}
//			});
//			return view;
//		}
//
//		volatile int time = 0;
//		Handler adaphandler = new Handler(){
//			public void handleMessage(Message msg) {
//				Bundle bd = msg.getData();
//				String ssid = bd.getString("ssid");
//				String pwd = bd.getString("pwd");
//				if(msg.what == 1){
//					if(wifiUtils.isConnectedSSID(ssid)){
//						PrefrencesUtils.setLocalSSID(getApplicationContext(), wifiUtils.getConnectedSSID());
//						PrefrencesUtils.setLocalSSIDPWD(getApplicationContext(), pwd);
//						dimissDialog();
//						sendEmptyMessageDelayed(3, 1000);
//					}else{
//						if(time < 8){
//							time++;
//							Message newmsg = new Message();
//							newmsg.what = 1;
//							newmsg.setData(bd);
//							sendMessageDelayed(newmsg, 1000);
//						}else{
//							dimissDialog();
//							CustomerToast.showToast(mContext, "连接网络失败！");
//						}
//					}
//					time++;
//				}else if(msg.what == 3){
//					CustomerToast.showToast(mContext, "连接网络成功!");
//					notifyDataSetChanged();
//				}
//			};
//		};
//	}
//
//}
