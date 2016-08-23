package com.lsd.atcmd;

import java.io.Serializable;

public class Device implements Serializable{
		private static final long serialVersionUID = 1L;
		public String ip;
		public String mac;
		public String name;
		public int port;
		public Device(){
			
		}
		public Device(String ip,String mac,String name,int port){
			this.ip = ip;
			this.mac = mac;
			this.name = name;
			this.port = port;
		}
	}