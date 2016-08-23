package com.fusi.fishingalarm.wifi;

import java.net.Inet4Address;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

public class WifiAdmin {

	private WifiManager wifiManager;

	/**
	 * �����������
	 */
	private WifiInfo wifiInfo;

	/**
	 * Wifi��Ϣ
	 */
	private List<ScanResult> scanResultList;

	/**
	 * ɨ����������������б�
	 */
	private List<WifiConfiguration> wifiConfigList;

	/**
	 * ���������б�
	 */
	private WifiLock wifiLock;

	/**
	 * ��������
	 * 
	 * @author Administrator
	 * 
	 */
	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}

	/**
	 * �����ü�¼���ӷ�ʽ
	 * 
	 * @param SSID
	 * @param Password
	 * @param Type
	 * @return true or false
	 */
	public boolean Connect(String SSID, String Password, WifiCipherType Type) {
		if (!this.OpenWifi()) {
			return false;
		}
		// ״̬���WIFI_STATE_ENABLED��ʱ�����ִ����������
		while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			try {
				// Ϊ�˱������һֱwhileѭ��������˯��100�����ڼ�⡭��
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException ie) {
			}
		}

		WifiConfiguration wifiConfig = this.CreateWifiInfo(SSID, Password, Type);
		//
		if (wifiConfig == null) {
			return false;
		}
		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
			wifiManager.removeNetwork(tempConfig.networkId);
		}
		int netID = wifiManager.addNetwork(wifiConfig);
		System.out.println(netID);
		wifiManager.startScan();

		for (WifiConfiguration c0 : wifiManager.getConfiguredNetworks()) {
			if (c0.networkId == netID) {
				boolean bRet = wifiManager.enableNetwork(c0.networkId, true);
			} else {
				wifiManager.enableNetwork(c0.networkId, false);
			}
		}
		boolean bRet = wifiManager.enableNetwork(netID, true);
		wifiManager.saveConfiguration();
		return bRet;
	}

	/**
	 * ������������
	 * 
	 * @param wf
	 * @return
	 */
	public boolean Connect(WifiConfiguration wf) {
		if (!this.OpenWifi()) {
			return false;
		}
		// ״̬���WIFI_STATE_ENABLED��ʱ�����ִ����������
		while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			try {
				// Ϊ�˱������һֱwhileѭ��������˯��100�����ڼ�⡭��
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException ie) {
			}
		}

		WifiConfiguration wifiConfig = wf;
		//
		if (wifiConfig == null) {
			return false;
		}
		WifiConfiguration tempConfig = this.IsExsits(wifiConfig.SSID);
		if (tempConfig != null) {
			wifiManager.removeNetwork(tempConfig.networkId);
		}
		int netID = wifiManager.addNetwork(wifiConfig);
		System.out.println(netID);
		wifiManager.startScan();

		for (WifiConfiguration c0 : wifiManager.getConfiguredNetworks()) {
			if (c0.networkId == netID) {
				boolean bRet = wifiManager.enableNetwork(c0.networkId, true);
			} else {
				wifiManager.enableNetwork(c0.networkId, false);
			}
		}
		boolean bRet = wifiManager.enableNetwork(netID, true);
		wifiManager.saveConfiguration();
		return bRet;
	}

	/**
	 * ��wifi����
	 * 
	 * @return true or false
	 */
	public boolean OpenWifi() {
		boolean bRet = true;
		if (!wifiManager.isWifiEnabled()) {
			bRet = wifiManager.setWifiEnabled(true);
		}
		return bRet;
	}

	/**
	 * �鿴��ǰ�Ƿ�Ҳ���ù��������
	 * 
	 * @param SSID
	 * @return WifiConfiguration
	 */
	public WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	/**
	 * ����
	 * 
	 * @param SSID
	 * @param Password
	 * @param Type
	 * @return WifiConfiguration
	 */
	private WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCipherType Type) {
		WifiConfiguration wc = new WifiConfiguration();
		wc.allowedAuthAlgorithms.clear();
		wc.allowedGroupCiphers.clear();
		wc.allowedKeyManagement.clear();
		wc.allowedPairwiseCiphers.clear();
		wc.allowedProtocols.clear();
		wc.SSID = "\"" + SSID + "\"";
		if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
			wc.wepKeys[0] = "";
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			wc.wepTxKeyIndex = 0;
		} else if (Type == WifiCipherType.WIFICIPHER_WEP) {
			wc.wepKeys[0] = "\"" + Password + "\"";
			wc.hiddenSSID = true;
			System.out.println("111111111111111111111111");
			wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			wc.wepTxKeyIndex = 0;
			// System.out.println(wc.preSharedKey);
			System.out.println(wc);
		} else if (Type == WifiCipherType.WIFICIPHER_WPA) {
			wc.preSharedKey = "\"" + Password + "\"";
			wc.hiddenSSID = true;
			// �����жϼ��ܷ�����
			// ��ѡ������LEAPֻ����leap,
			// OPEN ��wpa/wpa2��Ҫ,
			// SHARED��Ҫһ����̬��wep key
			wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			// �����жϼ��ܷ�������ѡ������CCMP,TKIP,WEP104,WEP40
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			// WifiConfiguration.KeyMgmt ��������ƣ�keymanagerment����ʹ��KeyMgmt ���С�
			// ��ѡ����IEEE8021X,NONE,WPA_EAP,WPA_PSK
			wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			// WifiConfiguration.PairwiseCipher ���ü��ܷ�ʽ��
			// ��ѡ���� CCMP,NONE,TKIP
			wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			// WifiConfiguration.Protocol ����һ��Э����м��ܡ�
			// ��ѡ���� RSN,WPA,
			wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA); // for WPA
			wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN); // for WPA2
			// wifiConfiguration.Status ��ȡ��ǰ�����״̬��
		} else {
			return null;
		}
		return wc;
	}

	/**
	 * Wifi��
	 * 
	 * @param context
	 */
	public WifiAdmin(Context context) {
		this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// ��ȡWifi����
		// �õ�Wifi��Ϣ
		this.wifiInfo = wifiManager.getConnectionInfo();
		// �õ�������Ϣ
	}

	/**
	 * 
	 * @return
	 */
	public boolean getWifiStatus() {
		return wifiManager.isWifiEnabled();
	}

	/**
	 * ��ȡ��ǰ����״̬
	 * 
	 * @return
	 */
	public int getWifiState() {
		return wifiManager.getWifiState();
	}

	/**
	 * �ر�wifi
	 * 
	 * @return
	 */
	public boolean closeWifi() {
		if (!wifiManager.isWifiEnabled()) {
			return true;
		} else {
			return wifiManager.setWifiEnabled(false);
		}
	}

	// ����/����wifi
	// ��ʵ����WiFI�����ж�wifi�Ƿ����ɹ���������ʹ�õ���held�����ֵ���˼acquire �õ���

	public void lockWifi() {
		wifiLock.acquire();
	}

	public void unLockWifi() {
		if (!wifiLock.isHeld()) {
			wifiLock.release();
			// �ͷ���Դ
		}
	}

	// �ұ�����д�ڹ��캯�����ˣ����ǿ��ǵ�����ÿ�ζ���ʹ��Wifi�������Ըɴ��Լ�����һ����������Ҫʱ���ã�������OK

	public void createWifiLock() {
		wifiLock = wifiManager.createWifiLock("flyfly");
		// ����һ�����ı�־
	}

	/**
	 * ɨ������
	 */
	public void startScan() {
		wifiManager.startScan();
		scanResultList = wifiManager.getScanResults();
		// ɨ�践�ؽ���б�
		wifiConfigList = wifiManager.getConfiguredNetworks();
		// ɨ�������б�
	}

	/**
	 * ɨ�赽��AP����
	 * 
	 * @return
	 */
	public List<ScanResult> getWifiList() {
		return scanResultList;
	}

	/**
	 * �����õ�AP����
	 * 
	 * @return
	 */
	public List<WifiConfiguration> getWifiConfigList() {
		return wifiConfigList;
	}

	/**
	 * ��ȡɨ���б�
	 * 
	 * @return
	 */
	public StringBuilder lookUpscan() {
		StringBuilder scanBuilder = new StringBuilder();
		for (int i = 0; i < scanResultList.size(); i++) {
			scanBuilder.append("��ţ�" + (i + 1));
			scanBuilder.append(scanResultList.get(i).toString());
			// ������Ϣ
			scanBuilder.append("\n");
		}
		return scanBuilder;
	}

	/**
	 * ��ȡָ���źŵ�ǿ��
	 * 
	 * @param NetId
	 * @return
	 */
	public int getLevel(int NetId) {
		return scanResultList.get(NetId).level;
	}

	/**
	 * ��ȡ����Mac��ַ
	 * 
	 * @return
	 */
	public String getMac() {
		return (wifiInfo == null) ? "" : wifiInfo.getMacAddress();
	}

	/**
	 * ��ȡ��������BSSID��ַ
	 * 
	 * @return
	 */
	public String getBSSID() {
		return (wifiInfo == null) ? null : wifiInfo.getBSSID();
	}

	/**
	 * ��ȡ����SSID
	 * 
	 * @return
	 */
	public String getSSID() {
		return (wifiInfo == null) ? null : wifiInfo.getSSID();
	}

	/**
	 * ���ص�ǰ���ӵ������ID
	 * 
	 * @return
	 */
	public int getCurrentNetId() {
		return (wifiInfo == null) ? null : wifiInfo.getNetworkId();
	}

	/**
	 * ����������Ϣ
	 * 
	 * @return
	 */
	public String getwifiInfo() {
		return (wifiInfo == null) ? null : wifiInfo.toString();
	}

	/**
	 * ��ȡIP��ַ
	 * 
	 * @return
	 */
	public int getIP() {
		return (wifiInfo == null) ? null : wifiInfo.getIpAddress();
	}

	/**
	 * ���һ������
	 * 
	 * @param config
	 * @return
	 */
	public boolean addNetWordLink(WifiConfiguration config) {
		int NetId = wifiManager.addNetwork(config);
		return wifiManager.enableNetwork(NetId, true);
	}

	/**
	 * ����һ������
	 * 
	 * @param NetId
	 * @return
	 */
	public boolean disableNetWordLick(int NetId) {
		wifiManager.disableNetwork(NetId);
		return wifiManager.disconnect();
	}

	/**
	 * �Ƴ�һ������
	 * 
	 * @param NetId
	 * @return
	 */
	public boolean removeNetworkLink(int NetId) {
		return wifiManager.removeNetwork(NetId);
	}

	/**
	 * ����ʾSSID
	 * 
	 * @param NetId
	 */
	public void hiddenSSID(int NetId) {
		wifiConfigList.get(NetId).hiddenSSID = true;
	}

	/**
	 * ��ʾSSID
	 * 
	 * @param NetId
	 */
	public void displaySSID(int NetId) {
		wifiConfigList.get(NetId).hiddenSSID = false;
	}

	/**
	 * ת��IP
	 * 
	 * @param ip
	 * @return
	 */
	public String ipIntToString(int ip) {
		try {
			byte[] bytes = new byte[4];
			bytes[0] = (byte) (0xff & ip);
			bytes[1] = (byte) ((0xff00 & ip) >> 8);
			bytes[2] = (byte) ((0xff0000 & ip) >> 16);
			bytes[3] = (byte) ((0xff000000 & ip) >> 24);
			return Inet4Address.getByAddress(bytes).getHostAddress();
		} catch (Exception e) {
			return "";
		}
	}
}
