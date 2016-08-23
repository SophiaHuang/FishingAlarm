package com.fusi.fishingalarm.wifi;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiUtils {

	Context mContext;
	WifiManager mWifiManger;

	public WifiUtils(Context mContext) {
		this.mContext = mContext;
	}

	public synchronized WifiManager getWifiManager() {
		if(mWifiManger == null){
			mWifiManger = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);   
		}
		return mWifiManger;
	}

	
	public void startScan() {  
		getWifiManager().startScan();  
		// 得到扫描结果   
	} 

	/**
	 * 打开WIFI   
	 */
	public void openWifi() {  
		if (!getWifiManager().isWifiEnabled()) {  
			getWifiManager().setWifiEnabled(true);  
		}
	} 

	/**
	 * 关闭WIFI   
	 */
	public void closeWifi() {  
		if (getWifiManager().isWifiEnabled()) {  
			getWifiManager().setWifiEnabled(false);  
		}
	} 


	/**
	 * WIFI是否打开
	 * @return
	 */
	public boolean isWifiOpen() {
		return getWifiManager().isWifiEnabled();
	}

	/**
	 * Wi-Fi是否连接
	 * @return
	 */
	public boolean isWifiConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager)mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(wifiNetworkInfo.isConnected()){
			return true ;
		}
		return false ;

	}

	
	// 得到连接的IP地址
		public String getConnectedIPAddr() {
			return intToIp(getWifiManager().getConnectionInfo().getIpAddress());
		}

		/**
		 * 将获取的int转为真正的ip地址
		 * 
		 * @param i
		 * @return
		 */
		private String intToIp(int i) {
			return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
					+ "." + ((i >> 24) & 0xFF);
		}
	

	/**
	 * 获取本地ip地址
	 */
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.isLinkLocalAddress()) {
						System.out.println(inetAddress.getHostAddress()
								.toString());
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return "";
	}



	/**
	 * 然后是一个实际应用方法，只验证过没有密码的情况： 
	 * 创建一个Wi-Fi连接的配置文件,如果已存在，则现移除以前的
	 * @param SSID
	 * @param Password
	 * @param Type
	 * @return
	 */
	public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)  
	{  
		WifiConfiguration config = new WifiConfiguration();    
		config.allowedAuthAlgorithms.clear();  
		config.allowedGroupCiphers.clear();  
		config.allowedKeyManagement.clear();  
		config.allowedPairwiseCiphers.clear();  
		config.allowedProtocols.clear();  
		config.SSID = "\"" + SSID + "\"";    

		WifiConfiguration tempConfig = this.isExsits(SSID);            
		if(tempConfig != null) {   
			mWifiManger.removeNetwork(tempConfig.networkId);   
		} 
		if(Type == 1){  //WIFICIPHER_NOPASS 

			config.wepKeys[0] = "";  
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
			config.wepTxKeyIndex = 0;  
		}  
		if(Type == 2){  //WIFICIPHER_WEP 
			config.hiddenSSID = true; 
			config.wepKeys[0]= "\""+Password+"\"";  
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);  
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);  
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
			config.wepTxKeyIndex = 0;  
		}  
		if(Type == 3){   //WIFICIPHER_WPA 
			config.preSharedKey = "\""+Password+"\"";  
			config.hiddenSSID = true;    
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);    
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                          
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                          
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                     
			//config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);   
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP); 
			config.status = WifiConfiguration.Status.ENABLED;    
		} 
		return config;  
	}  

	/**
	 * 是否存在指定ssid的Wi-Fi
	 * @param SSID
	 * @return
	 */
	public WifiConfiguration isExsits(String SSID){
		List<WifiConfiguration> existingConfigs = getWifiManager().getConfiguredNetworks();   
		if(existingConfigs == null || existingConfigs.size() == 0)
			return null;
		for (WifiConfiguration existingConfig : existingConfigs){   
			if (existingConfig.SSID.equals("\""+SSID+"\"")){   
				return existingConfig;   
			}
		}   
		return null;    
	}


	/**
	 * 连接指定ssid的Wi-Fi
	 * @param SSID
	 * @param pwd
	 * @return
	 */
	public boolean connectWiFI(String SSID,String pwd) {
		WifiConfiguration config= isExsits(SSID);
		if(config != null){
			getWifiManager().removeNetwork(config.networkId);
		}
		config = CreateWifiInfo(SSID, pwd, 3);
		int netWorkId = getWifiManager().addNetwork(config);
		if(netWorkId != -1){
			getWifiManager().enableNetwork(netWorkId, true);
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 判断当前连接的Wi-Fi是否和传入进来的ssid相等
	 * @param ssid
	 * @return
	 */
	public boolean isConnectedSSID(String ssid){
		if(isWifiOpen() && isWifiConnected()){
			String localSSID = getConnectedSSID();
			if(localSSID.equals(ssid)){
				return true;
			}else{
				return false;
			}
		}
		return false;

	}


	/**
	 * 获取当前Wi-Fi列表
	 * @param isConnected 是否连接到指定Wi-Fi
	 * @return
	 */
	public List<String> getScanResult(boolean isConnected) {
		mWifiManger = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		List<String> wifilist = new ArrayList<String>();
		if(!isConnected){
			List<ScanResult> results = getWifiManager().getScanResults();
			if(results != null && results.size() != 0){
				for(int i=0;i<results.size();i++){
					wifilist.add(results.get(i).SSID);
				}
				return wifilist;
			}
			return null;
		}
		String connectSSID = getWifiManager().getConnectionInfo().getSSID().replace("\"", "");
		List<ScanResult> results = getWifiManager().getScanResults();
		wifilist.add(connectSSID);
		if(results != null && results.size() != 0){
			for(int i=0;i<results.size();i++){
				String SSID = results.get(i).SSID.replace("\"", "");
				if(!SSID.equals(connectSSID)){
					wifilist.add(results.get(i).SSID);
				}
			}
		}
		return wifilist;
	}
	
	
	public void clearWifiConfig(Context context) {
		List<WifiConfiguration> wifiConfigList = getWifiManager().getConfiguredNetworks();// 得到配置好的网络信息
		int nowid = getWifiManager().getConnectionInfo().getNetworkId();
		for (int i = 0; i < wifiConfigList.size(); i++) {
			WifiConfiguration wif = wifiConfigList.get(i);
			int sid = wif.networkId;
			if (sid != nowid) {	// 清楚该Wifi之外的所有配置信息
				getWifiManager().removeNetwork(wif.networkId);
			}
		}
		getWifiManager().saveConfiguration();
	}
	
	
	public String getLocalMac() {
		return getWifiManager().getConnectionInfo().getMacAddress();
	}


	public String getConnectedSSID(){
		if(!isWifiOpen()){
			return "";
		}
		if(!isWifiConnected()){
			return "";
		}
		return getWifiManager().getConnectionInfo().getSSID().replace("\"", "");
		
	}
	
	


}
