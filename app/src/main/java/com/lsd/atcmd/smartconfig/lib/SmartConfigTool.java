package com.lsd.atcmd.smartconfig.lib;

import com.lsd.atcmd.smartconfig.lib.SmartConfigActivity.ConfigUdpBroadcast;

/**
 * @ClassName:  SmartConfigTool.java 
 * @Author jinchao
 * @Date 2015-1-23 下午4:32:33
 * @Description:
 */
public class SmartConfigTool {
	
	public static int START_CODE = 44;
	public static int CONTENT_START_CODE = 68;//48;
	public static int CONTENT_END_CODE = 120;//52;
	public static int INTERVAL_CODE = 96;//76;//68;
	
	//public static int FINISH_CODE = 100;//56;
	//public static int CONNECT_ENQ_CODE = 60;
	//public static int CONNECT_ACK_CODE = 64;
	
	public static int START_CODE_TIMES = 10;
	public static int START_CONTENT_CODE_TIMES = 10;
	public static int FIRST_BELL_TIMES = 10;
	public static int OTHER_BELL_TIMES = 1; 
	public static int END_CONTENT_CODE_TIMES = 10;
	//public static int CODE_TIMES = 10;
	public static int CONTENT_BYTE_TIMES = 3;
	public static int INTERVAL_TIMES = 8;
	
	public static int INTERVAL_MILLITIME = 20;
	
	public static int PERIOD_INTERVAL_MILLITIME = 50;
	
	
	public static int[] passwords;
	public static int checksum;
	
	private static boolean sendFlag = true;
	
	public static void doConfig(final ConfigUdpBroadcast broadUdp,final String password,final ConfigCallback callback){
		sendFlag = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				int j =0;
				while(j < INTERVAL_TIMES && sendFlag){
					sendCode(broadUdp, START_CODE,START_CODE_TIMES);
					sendCode(broadUdp, CONTENT_START_CODE,START_CONTENT_CODE_TIMES);
					int len = password.length();
					passwords = new int[len];
					sendContent(broadUdp,passwords.length);
					if(len > 0){
						for (int i = 0; i < password.length(); i++) {
							if(i==0){
								sendCode(broadUdp, INTERVAL_CODE,FIRST_BELL_TIMES);
							}else{
								sendCode(broadUdp, INTERVAL_CODE,OTHER_BELL_TIMES);
							}
							passwords[i] = password.charAt(i);
							sendContent(broadUdp,passwords[i],i%2==0);
						}
						sendCode(broadUdp,CONTENT_END_CODE,END_CONTENT_CODE_TIMES);
						int sum=0;
						for(int i=0; i<passwords.length;i++){
							sum+=passwords[i];
						}
						checksum = sum%256;
						sendContent(broadUdp,checksum);
						//sendCode(broadUdp, FINISH_CODE);
					} 
					try {
						Thread.sleep(PERIOD_INTERVAL_MILLITIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					j++;
				}
				if(callback!=null){
					callback.complete();
				}
			}
		}).start();
	}
	
	public static void sendCode(ConfigUdpBroadcast broadUdp,int code,int times){
		for(int i=0;i<times;i++){
			if(sendFlag){
				broadUdp.send(getBytes(code));
				try {
					Thread.sleep(INTERVAL_MILLITIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*public static void sendCode(ConfigUdpBroadcast broadUdp,int code){
		//sendCode(broadUdp, code,CODE_TIMES);
	}*/
	public static void sendContent(ConfigUdpBroadcast broadUdp,int content){
		sendContent(broadUdp, content, false);
	}
	public static void sendContent(ConfigUdpBroadcast broadUdp,int content,boolean isEven){
		int capacity = 40+content*4;
		if(isEven){
			capacity+=512;
		} 
		byte[] datas = getBytes(capacity);
		for(int i=0;i<CONTENT_BYTE_TIMES;i++){
			if(sendFlag){
				broadUdp.send(datas);
				try {
					Thread.sleep(INTERVAL_MILLITIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	
	}
	
	public static void stopSend(){
		sendFlag = false;
	}
	
	private static byte[] getBytes(int capacity){
		byte[] data = new byte[capacity];
		for(int i=0;i<capacity;i++){
			data[i]=5;
		}
		return data;
	} 
	
	public static interface ConfigCallback{
		public void complete();
	}
}
