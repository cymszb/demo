package com.zglue.zgluesports;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Micki on 2017/11/25.
 */

public class HomeAdapter extends BaseAdapter {
    private Context mContext;

    private ArrayList<HomeItemData> mDataArray;

    private final int COUNT = 4;

    public HomeAdapter(Context context){
        mContext = context;
    }

    public int getCount() {
        return COUNT;
        /*
        if(mDataArray != null){
            return mDataArray.size();
        }else {
            return 0;
        }*/
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            //HomeItemData data = mDataArray.get(position);
            switch (position){
                case 0:
                    convertView = new HomeStepView(mContext);
                    break;
                case 1:
                    convertView = new HomeSleepView(mContext);
                    break;
                case 2:
                    convertView = new HomeHearBeatView(mContext);
                    break;
                case 3:
                    convertView = new HomeTemperatureView(mContext);
                    break;
                default:
                    convertView = new HomeHearBeatView(mContext);
                    break;
            }
        }
        return convertView;
    }

    public class HomeItemData{
        public String value;
        public String unit;
        public boolean isPlayable;
        public String title;

    }
}
