/**
 * �ͻ��˺ͷ������˱���ͨ�ŵ��߳�
 * ���ϵض�ȡ����������������
 */
package com.fusi.fishingalarm.client.model;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;


import android.content.Context;
import android.content.Intent;

import com.fusi.fishingalarm.client.common.SocketMessage;
import com.fusi.fishingalarm.client.common.SocketMessageType;

public class ClientConServerThread extends Thread {
	private Context context;
	private  Socket s;
	public Socket getS() {return s;}
	public ClientConServerThread(Context context,Socket s){
		this.context=context;
		this.s=s;
	}

	@Override
	public void run() {
		while(true){
			ObjectInputStream ois = null;
			SocketMessage m;
			try {
				ois = new ObjectInputStream(s.getInputStream());
				m=(SocketMessage) ois.readObject();
				if(m.getType().equals(SocketMessageType.COM_MES)){//如果是聊天内容
					//把从服务器获得的消息通过广播发送
					Intent intent = new Intent("org.yhn.yq.mes");
					String[] message=new String[]{
							m.getSender()+"",
							m.getSenderNick(),
							m.getSenderAvatar()+"",
							m.getContent(),
							m.getSendTime()};
					intent.putExtra("message", message);
					context.sendBroadcast(intent);
				}else if(m.getType().equals(SocketMessageType.RET_ONLINE_FRIENDS)){//如果是好友列表
				}
			} catch (Exception e) {
				//e.printStackTrace();
				try {
					if(s!=null){
						s.close();
					}
				} catch (IOException e1) {
					//e1.printStackTrace();
				}
			}
		}
	}
	
}
