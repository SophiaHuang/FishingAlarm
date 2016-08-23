package com.fusi.fishingalarm.client.model;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;


import android.content.Context;

import com.fusi.fishingalarm.client.common.SocketMessage;
import com.fusi.fishingalarm.client.common.SocketMessageType;

public class Client {
	private Context context;
	public Socket s;
	public Client(Context context){
		this.context=context;
	}
	public boolean sendLoginInfo(Object obj){
		boolean b=false;
		try {
			s=new Socket();
			try{
				s.connect(new InetSocketAddress("10.0.2.2",5469),2000);
			}catch(SocketTimeoutException e){
				//连接服务器超时
				return false;
			}
			ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(obj);
			ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
			SocketMessage ms=(SocketMessage)ois.readObject();
			if(ms.getType().equals(SocketMessageType.SUCCESS)){
				//个人信息
//				MainActivity.myInfo=ms.getContent();
				//创建一个该账号和服务器保持连接的线程
				ClientConServerThread ccst=new ClientConServerThread(context,s);
				//启动该通信线程
				ccst.start();
				//加入到管理类中
//				ManageClientConServer.addClientConServerThread(((User)obj).getAccount(), ccst);
				b=true;
			}else if(ms.getType().equals(SocketMessageType.FAIL)){
				b=false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return b;
	}

	public boolean sendRegisterInfo(Object obj){
		boolean b=false;
		try {
			s=new Socket();
			try{
				s.connect(new InetSocketAddress("10.0.2.2",5469),2000);
			}catch(SocketTimeoutException e){
				//连接服务器超时
				return false;
			}
			ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(obj);
			ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
			SocketMessage ms=(SocketMessage)ois.readObject();
			if(ms.getType().equals(SocketMessageType.SUCCESS)){
				b=true;
			}else if(ms.getType().equals(SocketMessageType.FAIL)){
				b=false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return b;
	}
}
