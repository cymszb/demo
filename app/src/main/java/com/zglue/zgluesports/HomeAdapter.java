package com.zglue.zgluesports;

import com.zglue.zgluesports.bluetooth.BluetoothDataManager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Micki on 2017/11/25.
 */

public class HomeAdapter extends BaseAdapter {
    private Context mContext;
    private BluetoothDataManager bdManager;

    private ArrayList<HomeItemData> mDataArray;

    private final int COUNT = 4;

    HomeTemperatureView mHomeTemperatureView;
    HomeStepView mHomeStepView;
    HomeHearBeatView mHomeHearBeatView;
    HomeSleepView mHomeSleepView;

    public HomeAdapter(Context context){
        mContext = context;
        bdManager = BluetoothDataManager.getInstance(mContext);
        mHomeTemperatureView = new HomeTemperatureView(mContext);
        mHomeStepView = new HomeStepView(mContext);
        mHomeHearBeatView = new HomeHearBeatView(mContext);
        mHomeSleepView = new HomeSleepView(mContext);

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
        Log.e("Demo","getView");
        if(convertView == null){
            //HomeItemData data = mDataArray.get(position);
            switch (position){
                case 0:
                    convertView = mHomeTemperatureView;;
                    break;
                case 1:
                    convertView = mHomeStepView;
                    break;
                case 2:
                    convertView = mHomeHearBeatView;
                    break;
                case 3:
                    convertView = mHomeSleepView;
                    break;
                default:
                    //convertView = new HomeStepView(mContext);
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
