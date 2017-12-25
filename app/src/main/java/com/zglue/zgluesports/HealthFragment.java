package com.zglue.zgluesports;

import com.zglue.zgluesports.bluetooth.BluetoothDataManager;
import com.zglue.zgluesports.bluetooth.DataChangedListener;
import com.zglue.zgluesports.bluetooth.PersonalDataManager;
import com.zglue.zgluesports.bluetooth.RecordItem;
import com.zglue.zgluesports.bluetooth.RecordManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Created by Micki on 2017/11/5.
 */

public class HealthFragment extends Fragment implements DataChangedListener{
    private static final String TAG = HealthFragment.class.getName();

    private View mFragmentView;

    private TextView mTempValue;
    private TextView mHBValue;

    private ListView mTempList;
    private ListView mHBList;
    private RecordAdapter mTempAdapter;
    private RecordAdapter mHBAdapter;

    private PersonalDataManager pdManager;
    private BluetoothDataManager bdManager;

    private RecordManager mRecordManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pdManager = PersonalDataManager.getInstance(getContext());
        bdManager = BluetoothDataManager.getInstance(getContext());
        mRecordManager = RecordManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.from(this.getContext()).inflate(R.layout.health, null);
        mTempValue = (TextView)mFragmentView.findViewById(R.id.health_temp_value);
        mHBValue = (TextView) mFragmentView.findViewById(R.id.health_hb_value);
        mTempList = (ListView)mFragmentView.findViewById(R.id.health_temp_list);
        mTempAdapter = new RecordAdapter();
        mTempAdapter.addEntity(new Record("36.0","00:00"));
        mTempAdapter.addEntity(new Record("36.0","00:00"));
        mTempAdapter.addEntity(new Record("36.0","00:00"));
        mTempAdapter.addEntity(new Record("36.0","00:00"));
        mTempAdapter.addEntity(new Record("36.0","00:00"));
        mTempList.setAdapter(mTempAdapter);

        mHBList = (ListView)mFragmentView.findViewById(R.id.health_hb_list);
        mHBAdapter = new RecordAdapter();
        mRecordManager.queryAllHeartRate(new RecordManager.QueryCallback() {
            @Override
            public void onDone(ArrayList<RecordItem> items) {
                Log.e("Demo", "query heart rate done: " + items.size());
                for(RecordItem item:items){
                    mHBAdapter.addEntity(new Record(String.valueOf(item._value),item._time.toString()));
                }

            }
        });
        //mHBAdapter.addEntity(new Record("70","00:00"));
        //mHBAdapter.addEntity(new Record("70","00:00"));
        //mHBAdapter.addEntity(new Record("70","00:00"));
        //mHBAdapter.addEntity(new Record("70","00:00"));
        //mHBAdapter.addEntity(new Record("70","00:00"));
        mHBList.setAdapter(mHBAdapter);
        return mFragmentView;
    }

    @Override
    public void onResume(){
        super.onResume();
        bdManager.addDataChangedListener(this);
        mTempValue.setText(String.valueOf(bdManager.getTemperature()));
    }

    @Override
    public void onStop(){
        super.onStop();
        bdManager.removeDataChangedListener(this);
    }

    @Override
    public void OnHeartBeatChanged(final int rate){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                //mHBAdapter.addEntity(new Record(String.valueOf(rate),"00:00"));
                mHBValue.setText(String.valueOf(rate));
                //mHBAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void OnStepsChanged(int steps){
    }
    @Override
    public void OnTemperatureChanged(final float temperature) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                mTempAdapter.addEntity(new Record(String.valueOf(temperature),"00:00"));
                mTempValue.setText(String.valueOf(temperature));
                mTempAdapter.notifyDataSetChanged();
            }
        });

    }
    @Override
    public void OnBatteryChanged(int percent){

    }

    public  class Record{
        public String _value;
        public String _timestamp;
        public Record(String value, String timestamp){
            _value = value;
            _timestamp = timestamp;
        }
    }

    public  class RecordAdapter extends BaseAdapter {

        //ArrayList<TempRecord> TempRecords = new ArrayList<>(5);
        public static final int FIXED_SIZE = 5;
        LinkedList<Record> mRecords =   new LinkedList<>();

        public void addEntity(Record entity){
            if(mRecords.size() == FIXED_SIZE){
                mRecords.removeFirst();
            }
            mRecords.add(entity);
        }
        public void removeEntity(Record entity){
            mRecords.remove(entity);
        }

        @Override
        public int getCount() {
            return mRecords.size();
        }


        public Object getItem(int position) {
            return mRecords.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater
                        .from(HealthFragment.this.getContext())
                        .inflate(R.layout.health_list_item,null);
                ViewHolder holder = new ViewHolder();
                holder.value = (TextView)convertView.findViewById(R.id.item_value);
                holder.time = (TextView)convertView.findViewById(R.id.item_time);
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)convertView.getTag();
            holder.value.setText(String.valueOf(mRecords.get(position)._value));
            holder.time.setText(String.valueOf(mRecords.get(position)._timestamp));


            return convertView;

        }
    }

    public class ViewHolder{

        TextView value;
        TextView time;

    }




}
