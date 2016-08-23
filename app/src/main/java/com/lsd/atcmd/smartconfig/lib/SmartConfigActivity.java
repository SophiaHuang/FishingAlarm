package com.lsd.atcmd.smartconfig.lib;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.fusi.fishingalarm.R;
import com.lsd.atcmd.smartconfig.lib.SmartConfigTool.ConfigCallback;


public abstract class SmartConfigActivity extends Activity {
	private SmartConfigActivity me=this;
	private WifiAdmin mWifiAdmin;
	protected Button connectBtn;
	protected TextView ssidEt;
	protected EditText pwdEt;
	protected CheckBox showPwd;
	private ConfigUdpBroadcast mConfigBroadUdp;
	private String mSsid;
	private String mPwd;
	
	private byte[] SUCCESS_FLAGS = new byte[]{(byte)0xA0, (byte)0xB0};
	
	private Handler mHandler;
	private String broadcastIp;
	
	protected static final int WHAT_CONFIG_COMPLETE = 0x1000;
	protected static final int WHAT_CONFIG_SUCCESS = 0x1001;
	protected static final int WHAT_CONFIG_TIMEOUT = 0x1002;
	protected static final int CONFIG_RECEIVE_UDP_TIMEOUT = 30*1000;
	
	private String TAG = SmartConfigActivity.class.getSimpleName();
	//protected String TIP_TIMEOUT;
	protected String TIP_WIFI_NOT_CONNECTED;
	protected String TIP_CONFIGURING_DEVICE;
	protected String TIP_DEVICE_CONFIG_SUCCESS;
	
	protected boolean RECEIVE_UDP_FLAG = true;
	
	protected boolean CONFIGURING = false;
	
	protected ConfigStatus configStatus;
	
	//private AtomicBoolean connectting = new AtomicBoolean(false);
	
	private Set<String> successMacSet = new HashSet<String>();
	
	protected abstract void onShowWifiNotConnectedMsg();
	
	protected abstract void onConfigResult(ConfigRetObj obj);
	
	InputMethodManager imm;
	
	private Runnable timeoutRun = new Runnable() {
		@Override
		public void run() {
			SmartConfigTool.stopSend();
			ConfigRetObj obj = new ConfigRetObj();
			obj.errcode = -1;
			onConfigResult(obj);
		}
	};
	
	
	protected String getSsid(){
		return mSsid;
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);//3G:TYPE_MOBILE  
	        if (wifi.isConnected()){  
	        	initConfig();
 	        }else{
 	        	stopConfig();
 	        }
		}
	};
	
	private void registerReceiver(){  
	    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);  
	    this.registerReceiver(receiver, filter);  
	}  
	private void unRegisterReceiver(){  
		this.unregisterReceiver(receiver);  
	}
	
	protected abstract void renderView(Bundle savedInstanceState);
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//this.TIP_TIMEOUT= me.getString(R.string.tip_timeout);
		
		renderView(savedInstanceState);
		
		init();
		
		showPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				pwdEt.setInputType(isChecked?EditorInfo.TYPE_CLASS_TEXT:EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
				if(isChecked) {  
					pwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);  
		        } else {  
		        	pwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);  
		        }  
				Editable etable = pwdEt.getText();  
	            Selection.setSelection(etable, etable.length());  
			}
		});
		
		
		if(RECEIVE_UDP_FLAG){
			mConfigBroadUdp = new ConfigUdpBroadcast();
			mConfigBroadUdp.open();
		}
		
		/*connectBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doConnect();
			}
		});*/
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				SmartConfigTool.stopSend();
				onConfigResult((ConfigRetObj)msg.obj);
			}
		};
		findViewById(R.id.settingBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(me,SettingActivity.class));
			}
		});
		
		TextView versionName = (TextView)findViewById(R.id.versionName);
		versionName.setText("V"+getAppVersionName(this));
		
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		
	}
	
	public void doConnect(){
		successMacSet.clear();
		mPwd = pwdEt.getText().toString().trim();
		mHandler.postDelayed(timeoutRun, CONFIG_RECEIVE_UDP_TIMEOUT);
		SmartConfigTool.doConfig(mConfigBroadUdp, mPwd, new ConfigCallback() {
			@Override
			public void complete() {
			}
		});
		imm.hideSoftInputFromWindow(pwdEt.getWindowToken(), 0);
	}
	
	public Handler getHandler(){
		return mHandler;
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	
	protected void stopConfig(){
		SmartConfigTool.stopSend();
		mConfigBroadUdp.stopReceive();
		mConfigBroadUdp.close();
	}
	protected void initConfig(){
		init();
		mConfigBroadUdp.open();
		mConfigBroadUdp.receive();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		initConfig();
		registerReceiver();
	}
	@Override
	protected void onStop() {
		super.onStop();
		stopConfig();
		unRegisterReceiver();
	}
	
	private void init(){
		configStatus = new ConfigStatus();
		mWifiAdmin = new WifiAdmin(me);
		broadcastIp = mWifiAdmin.getBroadcastIp();
		Log.i(TAG, "broadcastIp: "+broadcastIp);
		WifiInfo wifiInfo = mWifiAdmin.getWifiInfo();
		mSsid = wifiInfo.getSSID();
		boolean enabled = mWifiAdmin.getWifiManager().isWifiEnabled();
		if(!enabled||"0x".equals(mSsid)||"<none>".equals(wifiInfo.getBSSID())){
			onShowWifiNotConnectedMsg();
			mSsid = "";
		}
		ssidEt.setText(mSsid);
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
	
	/**
	 * @deprecated
	 */
	protected void stopUdpServer(){
		mConfigBroadUdp.stopReceive();
	}
	
	class ConfigUdpBroadcast{
		private InetAddress inetAddress;
		private DatagramSocket socket;
		private DatagramPacket packetToSend;
		private DatagramPacket dataPacket;
		private int port = 8800;
		private int localPort = 8801;
		private byte receiveByte[]=new byte[512];
		private boolean receiveFlag = true;
		
		public ConfigUdpBroadcast() {
			try {
				// 255.255.255.255
				inetAddress = InetAddress.getByName(broadcastIp);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		
		public void open() {
			try {
				/*
				socket = new DatagramSocket(port);
				socket.setBroadcast(true);
			   */
				socket = new DatagramSocket(null);
				socket.setBroadcast(true);
				socket.setReuseAddress(true);
				socket.bind(new InetSocketAddress(localPort));
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		
		public void close() {
			if (socket != null) {
				socket.close();
				socket=null;
			}
		}
		
		public void send(byte[] data){
			if(socket!=null){
				packetToSend = new DatagramPacket(
						data, data.length, inetAddress, port);
				try {
					socket.send(packetToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void receive(){
			receiveFlag = true;
			dataPacket = new DatagramPacket(receiveByte, receiveByte.length);
			new Thread(){
				public void run(){
					Log.i(TAG, "config udp receive thread start ....");
					while(receiveFlag){
						try {
							if(socket!=null){
								socket.receive(dataPacket);
								int len = dataPacket.getLength();
								if(len>0){
									Log.i(TAG,"recevie: "+bytesToHexStr(dataPacket.getData()));
									byte[] data = dataPacket.getData();
									int checksum = 0;
									/**
	协议名称(2bytes) ，      				mac地址(6bytes)                                             累加和%256(1bytes)
	0xA0, 0xB0                      0xAA,0xBB,0xCC,0x00,0x00,0x01                     0x32
									 */
									if(data[0] == SUCCESS_FLAGS[0] && data[1] == SUCCESS_FLAGS[1]){
										checksum = ((byte)(data[2] + data[3] + data[4] + data[5] + data[6] + data[7]))%256;
										if(checksum == data[8]){
											if(CONFIGURING){
												byte[] macs = new byte[6];
												String mac = bytesToHexStr(macs);
												if(!successMacSet.contains(mac)){
													System.arraycopy(data, 2, macs, 0, 6);
													Message msg = me.getHandler().obtainMessage();
													msg.what = WHAT_CONFIG_SUCCESS;
													ConfigRetObj obj = new ConfigRetObj();
													obj.ip = dataPacket.getAddress().getHostName();
													obj.mac = mac;
													obj.errcode = 0;
													msg.obj =  obj;
													me.getHandler().sendMessage(msg);
													me.getHandler().removeCallbacks(timeoutRun);
												}
												successMacSet.add(mac);
											}
										}
									}
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					Log.i(TAG, "config udp receive thread stop ....");
				}
			}.start();
			
		}
		
		public void stopReceive(){
			this.receiveFlag = false;
		}
	}
	
	private String bytesToHexStr(byte[] bytes){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<bytes.length;i++){
			sb.append(Integer.toHexString(bytes[i] & 0xff).toUpperCase());
		}
		return sb.toString();
	}
	
	public class ConfigRetObj implements Serializable{
		private static final long serialVersionUID = 1L;
		public int errcode; // 0: 成功  , -1: 配置超时
		public String mac;
		public String ip;
	}
}
