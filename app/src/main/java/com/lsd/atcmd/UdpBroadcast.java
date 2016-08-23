package com.lsd.atcmd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class UdpBroadcast{
		private InetAddress inetAddress;
		private DatagramSocket socket;
		private DatagramPacket packetToSend;
		private DatagramPacket dataPacket;
		private int port = 8800;
		private int localPort = 8802;
		private byte receiveByte[]=new byte[512];
		private boolean receiveFlag = true;
		DataListener dataListener;
		String TAG = UdpBroadcast.class.getSimpleName();
		
		
		public UdpBroadcast(String broadcastIp,DataListener dataListener) {
			try {
				// 255.255.255.255
				Log.i(TAG, "broadcastIp:"+broadcastIp);
				inetAddress = InetAddress.getByName(broadcastIp);
				this.dataListener = dataListener;
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
				socket.setSoTimeout(3000);
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
				for(int i=0;i<5;i++){
					packetToSend = new DatagramPacket(
							data, data.length, inetAddress, port);
					try {
						socket.send(packetToSend);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		public void receive(){
			dataPacket = new DatagramPacket(receiveByte, receiveByte.length);
			new Thread(){
				boolean receiving =true;
				public void run(){
					Log.i(TAG, "config udp receive thread start ....");
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							receiving = false;
						}
					}, 3000);
					
					final Set<String> strSet = new HashSet<String>();
					while(receiving){
						try {
							if(socket!=null){
								socket.receive(dataPacket);
								int len = dataPacket.getLength();
								if(len>0){
									byte[] data = new byte[len];
									System.arraycopy(receiveByte, 0, data,0, len);
									String str = new String(data);
									str+=","+dataPacket.getAddress().getHostAddress()+","+dataPacket.getPort();
									Log.i(TAG,"recevie: "+str);
									strSet.add(str);
								}
								/*try {
									Thread.sleep(30);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}*/
							}
						}catch (SocketTimeoutException e) {
							Log.i(TAG,"UdpUnicast SocketTimeoutException....");
							receiving = false;
						}catch (IOException e) {
							e.printStackTrace();
							receiving = false;
						}
					}
					if(dataListener!=null){
						dataListener.onReceive(strSet);
					}
					Log.i(TAG, "config udp receive thread stop ....");
				}
			}.start();
			
		}
		
		public static interface DataListener{
			public void onReceive(Set<String> set);
		}
	}