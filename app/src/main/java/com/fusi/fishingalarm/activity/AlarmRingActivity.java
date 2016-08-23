package com.fusi.fishingalarm.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.adapter.AlarmListAdapter;
import com.fusi.fishingalarm.adapter.AlarmRingListAdapter;
import com.fusi.fishingalarm.utils.SharedPrefrenceUtils;

import java.util.ArrayList;
import java.util.List;

import solid.ren.skinlibrary.base.SkinBaseActivity;


/**
 * Created by user90 on 2016/5/31.
 */
public class AlarmRingActivity extends BaseActivity {
    private String TAG = "AlarmRingActivity";
    private ListView mListView;
    private AlarmRingListAdapter mAdapter;
    private List<AlarmRingListAdapter.AlarmRingList> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);
        mListView = (ListView) findViewById(R.id.listview);
        initListView();
        initData();
    }

    private void initListView() {
        mAdapter = new AlarmRingListAdapter(AlarmRingActivity.this, mList);
        mListView.setAdapter(mAdapter);
    }

    private void initData() {
        if (SharedPrefrenceUtils.containsKey(AlarmRingActivity.this, "alarm_ring")) {
            Log.i(TAG, "initData: contains alarm:"+SharedPrefrenceUtils.getInt(getBaseContext(), "alarm_ring"));
            initListViewData(SharedPrefrenceUtils.getInt(getBaseContext(), "alarm_ring")-1, 16);
        } else {
            //        写死
            Log.i(TAG, "initData: no contains alarm");
            initListViewData(0, 16);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView name = (TextView) view.findViewById(R.id.name);
//                写死
                for (int i = 0; i < mList.size(); i++) {
                    Log.i(TAG, "onItemClick: i:"+i+",position:"+position);
                    if (i != position) {
                        mList.get(i).checked = false;
                    } else {
                        mList.get(i).checked = true;

                    }
                }
                Log.i(TAG, "onItemClick: name:" + name.getText().toString());
//                SharedPrefrenceUtils.setInt(AlarmRingActivity.this, "alarm_ring",position);
                mAdapter.notifyDataSetChanged();
                Intent intent=new Intent();
                intent.putExtra("alarm_ring",position+1);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void initListViewData(int index, int length) {
        for (int i = 0; i < length; i++) {
            Log.i(TAG, "initListViewData:alarm_name: "+index+",i:"+i);
            AlarmRingListAdapter.AlarmRingList item = new AlarmRingListAdapter.AlarmRingList();
            if (i==index){
                item.checked = true;
            }
            item.name = "报警铃声" + String.valueOf(i+1);

            mList.add(item);
        }
    }
    public void finish(View view){
        finish();
    }

}
