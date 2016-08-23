package com.lsd.atcmd.smartconfig.lib;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.fusi.fishingalarm.R;


/**
 * @ClassName:  SettingActivity.java 
 * @Author jinchao
 * @Date 2015-1-26 下午2:46:04
 * @Description:
 */
public class SettingActivity extends Activity {
	EditText start_code_times;
	EditText start_content_code_times;
	EditText first_bell_times;
	EditText other_bell_times;
	EditText end_content_code_times;
	EditText content_times ;
	EditText interval_millitime;
	EditText interval_times;
	EditText period_interval_millitime ;
	InputMethodManager imm;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smart_config_setting);
		
		start_code_times = (EditText)findViewById(R.id.start_code_times);
		start_content_code_times = (EditText)findViewById(R.id.start_content_code_times);
		first_bell_times = (EditText)findViewById(R.id.first_bell_times);
		other_bell_times = (EditText)findViewById(R.id.other_bell_times);
		content_times = (EditText)findViewById(R.id.content_times);
		end_content_code_times = (EditText)findViewById(R.id.end_content_code_times);
		
		interval_millitime = (EditText)findViewById(R.id.interval_millitime);
		interval_times = (EditText)findViewById(R.id.interval_times);
		period_interval_millitime = (EditText)findViewById(R.id.period_interval_millitime);
		
		start_code_times.setText(String.valueOf(SmartConfigTool.START_CODE_TIMES));
		start_content_code_times.setText(String.valueOf(SmartConfigTool.START_CONTENT_CODE_TIMES));
		first_bell_times.setText(String.valueOf(SmartConfigTool.FIRST_BELL_TIMES));
		other_bell_times.setText(String.valueOf(SmartConfigTool.OTHER_BELL_TIMES));
		content_times.setText(String.valueOf(SmartConfigTool.CONTENT_BYTE_TIMES));
		end_content_code_times.setText(String.valueOf(SmartConfigTool.END_CONTENT_CODE_TIMES));
		
		interval_millitime.setText(String.valueOf(SmartConfigTool.INTERVAL_MILLITIME));
		interval_times.setText(String.valueOf(SmartConfigTool.INTERVAL_TIMES));
		period_interval_millitime.setText(String.valueOf(SmartConfigTool.PERIOD_INTERVAL_MILLITIME));
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
	}
	
	public void doClick(View view){
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		if(view.getId()==R.id.backBtn){
			finish();
		}else if(view.getId() == R.id.saveBtn){
			if(checkNumber(start_code_times.getText().toString()) 
					&& checkNumber(start_content_code_times.getText().toString()) 
					&& checkNumber(first_bell_times.getText().toString()) 
					&& checkNumber(other_bell_times.getText().toString()) 
					&& checkNumber(content_times.getText().toString()) 
					&& checkNumber(end_content_code_times.getText().toString()) 
					
					&& checkNumber(interval_millitime.getText().toString())
					&& checkNumber(interval_times.getText().toString())
					&& checkNumber(period_interval_millitime.getText().toString())){
				SmartConfigTool.START_CODE_TIMES = Integer.valueOf(start_code_times.getText().toString());
				SmartConfigTool.START_CONTENT_CODE_TIMES = Integer.valueOf(start_content_code_times.getText().toString());
				SmartConfigTool.FIRST_BELL_TIMES = Integer.valueOf(first_bell_times.getText().toString());
				SmartConfigTool.OTHER_BELL_TIMES = Integer.valueOf(other_bell_times.getText().toString());
				SmartConfigTool.CONTENT_BYTE_TIMES = Integer.valueOf(content_times.getText().toString());
				SmartConfigTool.END_CONTENT_CODE_TIMES = Integer.valueOf(end_content_code_times.getText().toString());
				
				SmartConfigTool.INTERVAL_MILLITIME = Integer.valueOf(interval_millitime.getText().toString());
				SmartConfigTool.INTERVAL_TIMES = Integer.valueOf(interval_times.getText().toString());
				SmartConfigTool.PERIOD_INTERVAL_MILLITIME = Integer.valueOf(period_interval_millitime.getText().toString());
				
				finish();
			}
		}
	}
	
	public boolean checkNumber(String text){
		try{
			Integer.valueOf(text);
		}catch(Exception e){
			Toast.makeText(this, "请检查所有设置是否正确", 3000).show();
			return false;
		}
		return true;
	}
}
