package com.fusi.fishingalarm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.UILApplication;
import com.fusi.fishingalarm.adapter.AlarmListAdapter;
import com.fusi.fishingalarm.utils.SharedPrefrenceUtils;

import java.util.ArrayList;
import java.util.List;

import solid.ren.skinlibrary.base.SkinBaseActivity;


/**
 * Created by user90 on 2016/5/31.
 */
public class AlarmSetupActivity extends BaseActivity {
    private String TAG = "AlarmSetupActivity";
    private ListView mListView;
    private AlarmListAdapter mAdapter;
    private List<AlarmListAdapter.AlarmList> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setup);
        mListView = (ListView) findViewById(R.id.listview);
        initListView();
        initData();
    }

    private void initListView() {
        mAdapter = new AlarmListAdapter(AlarmSetupActivity.this, mList);
        mListView.setAdapter(mAdapter);
    }

    private void initData() {
        if (SharedPrefrenceUtils.containsKey(AlarmSetupActivity.this, "alarm_name")) {
            initListViewData(SharedPrefrenceUtils.getInt(getBaseContext(), "alarm_name"), UILApplication.ALARM_COUNT);
        } else {
            //        写死
            Log.i(TAG, "initData: UILApplication.ALARM_COUNT:"+UILApplication.ALARM_COUNT);
            initListViewData(0, UILApplication.ALARM_COUNT);
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView name = (TextView) view.findViewById(R.id.name);
//                写死
                Log.i(TAG, "onItemClick: ");
                Log.i(TAG, "onItemClick: position:" + position + ",mList.size():" + mList.size());

                for (int i = 0; i < mList.size(); i++) {
                    if (i != position) {
                        mList.get(i).checked = false;
                    } else {
                        mList.get(i).checked = true;
                        Log.i(TAG, "onActivityResult qian onItemClick: name:" + name.getText().toString() + ",position:" + position);
//                        SharedPrefrenceUtils.setInt(AlarmSetupActivity.this, "alarm_name", position);
                        Intent intent=new Intent();
                        intent.putExtra("alarm_name",position);

//                        if (i==0){
//                            SharedPrefrenceUtils.setBoolean(AlarmSetupActivity.this, "hasSubAlarm", false);
//                        }else {
//                            SharedPrefrenceUtils.setBoolean(AlarmSetupActivity.this, "hasSubAlarm", true);
//                        }
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initListViewData(int index, int length) {
        Log.i(TAG, "initListViewData: onActivityResult: index:" + index);
        for (int i = 0; i < length; i++) {
            AlarmListAdapter.AlarmList item = new AlarmListAdapter.AlarmList();
            if (i == 0) {
                item.name = getString(R.string.alarm);
            } else {
                item.name = getString(R.string.alarm_sub) + String.valueOf(i);
            }
            if (i == index) {
                item.checked = true;
            }

            mList.add(item);
        }
    }

    public void finish(View view){
        finish();
    }
}
