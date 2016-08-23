package com.lsd.atcmd;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.util.Log;

public class UdpUnicast implements INetworkTransmission{

	private static final boolean DEBUG = true;
	private static final int BUFFER_SIZE = 512;
	private int TIMEOUT = 5000;

	private String ip;
	private int port = 8800; //= 48899;
	private DatagramSocket socket;
	private DatagramPacket packetToSend;
	private InetAddress inetAddress;
	private ReceiveData receiveData;
	private UdpUnicastListener listener;
	private byte[] buffer = new byte[BUFFER_SIZE];
	private UdpUnicast me = this;
	private boolean autoReceive;
	private String TAG = UdpUnicast.class.getSimpleName();
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(UdpUnicastListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the listener
	 */
	public UdpUnicastListener getListener() {
		return listener;
	}
	public UdpUnicast(String ip,boolean autoReceive){
		super();
		this.ip = ip;
		this.autoReceive = autoReceive;
	}
	public UdpUnicast(String ip, int port,boolean autoReceive) {
		super();
		this.ip = ip;
		this.port = port;
		this.autoReceive = autoReceive;
	}

	/**
	 * Open udp socket
	 */
	public synchronized boolean open() {

		try {
			inetAddress = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}

		try {
			socket = new DatagramSocket(null);
			socket.setReuseAddress(true);
			socket.setSoTimeout(TIMEOUT);
			socket.bind(new InetSocketAddress(port));
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}
		/*try {
			socket = new DatagramSocket();
			socket.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}*/
		//receive response
		if(autoReceive){
			receiveData = new ReceiveData();
			receiveData.start();
		}
		return true;
	}

	/**
	 * Close udp socket
	 */
	public synchronized void close() {
		stopReceive();
		if (socket != null) {
			socket.close();
		}
	}

	public synchronized boolean sendHexBytes(byte[] bytes){
		return send(bytes);
	}
	public synchronized boolean sendAsciiBytes(byte[] bytes){
		Log.i(TAG, "send: "+new String(bytes));
		return send(bytes);
	}
	public synchronized boolean send(byte[] bytes){
		if (socket == null) {
			return false;
		}

		Log.i(TAG, "packetToSend........... ip: "+ip+", port:"+port);
		packetToSend = new DatagramPacket(
				bytes, bytes.length, inetAddress, port);

		//send data
		for (int i = 0; i < 3; i++) {
			try {
				socket.send(packetToSend);
				return true;
			} catch (BindException e) {
				socket.close();
				try {
					socket = new DatagramSocket(port);
				} catch (SocketException e1) {
					e1.printStackTrace();
					return false;
				}

				if (i == 1) {
					return false;
				}
			}catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		return false;
	}
	/**
	 * send message
	 * @param text
	 * 			the message to broadcast
	 */
	public  boolean send(String text) {
		log("send:" + text);
		if (text == null) {
			return true;
		}
		return this.send(text.getBytes());
	}
	

	/**
	 * Stop to receive
	 */
	public void stopReceive() {

		if (receiveData!=null && !receiveData.isStoped()) {
			receiveData.stop();
		}
	}

	public interface UdpUnicastListener {
		public void onReceived(byte[] data, int length);
	}

	private class ReceiveData implements Runnable {

		private boolean stop;
		private Thread thread;

		private ReceiveData() {
			thread = new Thread(this);
		}

		@Override
		public void run() {

			while (!stop) {
				try {

					DatagramPacket packetToReceive = new DatagramPacket(buffer, BUFFER_SIZE);
					socket.receive(packetToReceive);
					byte[] data = new byte[packetToReceive.getLength()];
					System.arraycopy(buffer, 0, data, 0, data.length);
					//onReceive(buffer, packetToReceive.getLength());
					onReceive(data, data.length);
				} catch (SocketTimeoutException e) {
					Log.i(TAG,"UdpUnicast SocketTimeoutException....");
				}catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		void start() {
			thread.start();
		}

		void stop() {
			stop = true;
		}

		boolean isStoped() {
			return stop;
		}
	}

	@Override
	public void setParameters(String ip, int port) {
		this.ip = ip;
		this.port = port;
		try {
			inetAddress = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceive(byte[] buffer, int length) {
		Log.i(TAG, "onReceive: "+ new String(buffer));
		if (listener != null) {
			listener.onReceived(buffer, length);
		}
	}

	/**
	 * 监听器有效.
	 * 返回接收数据.
	 * @return
	 */
	public byte[] onReceive(){
		try {
			while(true){
				DatagramPacket packetToReceive = new DatagramPacket(buffer, BUFFER_SIZE);
				socket.receive(packetToReceive);
				onReceive(buffer, packetToReceive.getLength());
				byte[] data = new byte[packetToReceive.getLength()];
				System.arraycopy(buffer, 0, data, 0, data.length);
				return data;
			}
		} catch (SocketTimeoutException e) {
			Log.i(TAG,"UdpUnicast SocketTimeoutException....");
		}catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	private void log(String log) {
		Log.i(TAG, log);
	}

 
}
