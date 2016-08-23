package com.lsd.atcmd;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fusi.fishingalarm.R;
import com.lsd.atcmd.UdpUnicast.UdpUnicastListener;

/**
 * @ClassName:  AtControlActivity.java 
 * @Description:
 */
public class AtControlActivity extends Activity {
	
	Button sendBtn;
	Device device;
	
	TextView hd_title;
	InputMethodManager imm;
	UdpUnicast udp ;
	private  AtControlActivity me = this;
	
	EditText inputEt,outEt;
	private String TAG = AtControlActivity.class.getSimpleName();
	
	StringBuilder sb = new StringBuilder();
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_control_layout);
		
		Intent intent = getIntent();
		if(intent!=null){
			Serializable obj =intent.getSerializableExtra("DEVICE");
			device = (Device)obj;
		}
		hd_title = (TextView)findViewById(R.id.hd_title);
		hd_title.setText(device.mac+"--"+device.ip); 
		
		sendBtn = (Button)findViewById(R.id.sendBtn);
		inputEt = (EditText)findViewById(R.id.inputEt);
		outEt = (EditText)findViewById(R.id.outEt);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		
	}
	public void doConnect(){
		udp = new UdpUnicast(device.ip, device.port,true);
		udp.setListener(new UdpUnicastListener() {
			@Override
			public void onReceived(byte[] data, int length) {
				if(data!=null){
					String str="";
					try {
						str = new String(data,"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					sb.append(str.replace("\r\n", "\n"));
					Log.i(TAG, sb.toString());
					me.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							outEt.setText(sb.toString()); 
							//Toast.makeText(me,"收到："+str,3000).show();
						}
					});
				}
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				udp.open();
			}
		}).start();
	}
	public void doClose(){
		udp.close();
		if(udp!=null){
			udp = null;
		}
	}
	@Override
	protected void onStart() {
		super.onStart();
		doConnect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		doClose();
	}
	
	public void doClick(View view){
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		if(view.getId()==R.id.backBtn){
			finish();
		}else if(view.getId() == R.id.sendBtn){
			String content = inputEt.getText().toString().trim();
			if(content.length()==0){
				Toast.makeText(me, "请输入AT命令", Toast.LENGTH_SHORT).show();
				return;
			}
			content = "AT+"+content;
			sb.append(content+"\n");
			outEt.setText(sb.toString());
			send(content);
		}else if(view.getId() == R.id.resetBtn){
			send("AT+Z");
		}else if(view.getId() == R.id.clearBtn){
			sb = null;
			sb = new StringBuilder();
			outEt.setText(sb.toString());
		}
	}
	
	public void send(String content){
		content+="\r";
		final String _content = content;
		new Thread(new Runnable() {
			@Override
			public void run() {
				udp.send("LSD_WIFI:"+_content+"\r");
				//byte[] data = udp.onReceive();
				
			}
		}).start();
	}
	class AtControlLayout extends LinearLayout{

		public AtControlLayout(Context context) {
			super(context);
		}
		
	}
}
