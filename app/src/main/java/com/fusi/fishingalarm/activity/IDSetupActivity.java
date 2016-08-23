package com.fusi.fishingalarm.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.adapter.AlarmRingListAdapter;
import com.fusi.fishingalarm.adapter.IDListAdapter;
import com.fusi.fishingalarm.utils.SharedPrefrenceUtils;

import java.util.ArrayList;
import java.util.List;

import solid.ren.skinlibrary.base.SkinBaseActivity;


/**
 * Created by user90 on 2016/5/31.
 */
public class IDSetupActivity extends BaseActivity {
    private String TAG = "IDSetupActivity";
    private ListView mListView;
    private IDListAdapter mAdapter;
    private List<IDListAdapter.IDList> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_setup);
        mListView = (ListView) findViewById(R.id.listview);
        initListView();
        initData();
    }

    private void initListView() {
        mAdapter = new IDListAdapter(IDSetupActivity.this, mList);
        mListView.setAdapter(mAdapter);
    }

    private void initData() {
        if (SharedPrefrenceUtils.containsKey(IDSetupActivity.this, "id_name")) {
            initListViewData(SharedPrefrenceUtils.getInt(getBaseContext(), "id_name"), 16);
        } else {
            //        写死
            initListViewData(0, 16);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView name = (TextView) view.findViewById(R.id.name);
//                写死
                for (int i = 0; i < mList.size(); i++) {
                    if (i != position) {
                        mList.get(i).checked = false;
                    } else {
                        mList.get(i).checked = true;
                        Log.i(TAG, "onItemClick: name:" + name.getText().toString());
                        SharedPrefrenceUtils.setInt(IDSetupActivity.this, "id_name", position);
                    }
                }
                mAdapter.notifyDataSetChanged();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void initListViewData(int id_name, int length) {
        for (int i = 0; i < length; i++) {
            IDListAdapter.IDList item = new IDListAdapter.IDList();
            if (i==id_name){
                item.checked = true;
            }
            item.name = getString(R.string.id_name) + String.valueOf(i+1);

            mList.add(item);
        }
    }

    public void finish(View view){
        finish();
    }
}
