package com.zglue.zgluesports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

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
            convertView = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.home_grid_item,null);
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
