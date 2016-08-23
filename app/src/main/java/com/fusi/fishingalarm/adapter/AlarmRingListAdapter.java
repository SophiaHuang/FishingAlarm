package com.fusi.fishingalarm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fusi.fishingalarm.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user90 on 2016/5/30.
 */
public class AlarmRingListAdapter extends BaseAdapter {

    private String TAG = "AlarmRingListAdapter";

    private Context mContext;
    private List<AlarmRingList> mLists = new ArrayList<>();
    private AlarmRingHolder mHolder;

    public AlarmRingListAdapter(Context mContext, List<AlarmRingList> mLists) {
        this.mContext = mContext;
        this.mLists = mLists;
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.adapter_alarm_list,null);
            mHolder=new AlarmRingHolder();
            mHolder.name= (TextView) convertView.findViewById(R.id.name);
            mHolder.checked= (ImageView) convertView.findViewById(R.id.checked);
            convertView.setTag(mHolder);
        }else {
            mHolder= (AlarmRingHolder) convertView.getTag();
        }
//        信号强弱没有设置，控件弄好记得设置哦
        mHolder.name.setText(mLists.get(position).name);
        if (mLists.get(position).checked){
            mHolder.checked.setImageResource(R.drawable.img_dot_blue);
        }else {
            mHolder.checked.setImageResource(R.drawable.img_dot_grey);
        }

        return convertView;
    }

    class AlarmRingHolder {
        TextView name;
//        选中
        ImageView checked;
    }

    static public class AlarmRingList {
        /*报警器名*/
        public String name;
        /*是否选中，默认不选中*/
        public boolean checked;

    }

}
