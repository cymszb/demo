package com.zglue.zgluesports;

import com.zglue.zgluesports.bluetooth.BluetoothDataManager;
import com.zglue.zgluesports.bluetooth.ConnectionListener;
import com.zglue.zgluesports.bluetooth.DataChangedListener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Micki on 2017/11/5.
 */

public class HomeFragment extends Fragment implements ConnectionListener,DataChangedListener{
    private static final String TAG = HomeFragment.class.getName();

    private View mFragmentView;
    private ImageButton mPairBtn;


    private TextView textModelName;
    private TextView textBatteryPercent;


    private GridView mContent;
    private HomeHearBeatView heartBeatView;
    private HomeStepView stepView;
    private HomeTemperatureView tempView;

    private BluetoothDataManager bdManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bdManager = BluetoothDataManager.getInstance(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.from(this.getContext()).inflate(R.layout.home,null);
        mPairBtn = (ImageButton)mFragmentView.findViewById(R.id.pair_button);
        textModelName = (TextView) mFragmentView.findViewById(R.id.text_model_name);
        textBatteryPercent = (TextView) mFragmentView.findViewById(R.id.text_battery_percent);
        textModelName.setText(bdManager.getModelName()!=null?bdManager.getModelName():"No Device");
        textBatteryPercent.setText(bdManager.getBatteryPercent()!=null?bdManager.getBatteryPercent():"0");
        initPairButton(mPairBtn);
        mContent = (GridView) mFragmentView.findViewById(R.id.content_grid);
        HomeAdapter adapter = new HomeAdapter(this.getContext());
        mContent.setAdapter(adapter);
        /*
        heartBeatView = (HomeHearBeatView) content.getChildAt(0);
        stepView = (HomeStepView) content.getChildAt(2);
        tempView = (HomeTemperatureView) content.getChildAt(3);
        */

        return mFragmentView;
    }

    @Override
    public void onResume(){
        super.onResume();
        bdManager.addConnectionListener(this);
        bdManager.addDataChangedListener(this);

        if(bdManager.isDeviceAvailable()){
            textModelName.setText(bdManager.getModelName());
            mPairBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_bluetooth_connected_white_24px));
            mPairBtn.setBackgroundResource(R.drawable.background_pair_btn);
        }else{
            textModelName.setText("No Device");
            mPairBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_bluetooth_disabled_white_24px));
            mPairBtn.setBackgroundResource(R.drawable.background_unpair_btn);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        bdManager.removeConnectionListener(this);
        bdManager.removeDataChangedListener(this);
    }


    private void initPairButton(final ImageButton btn){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == btn){
                    Intent intent = new Intent(getContext(),BluetoothScanActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void OnConnectStatusChanged(final BluetoothDevice device, final int state){
        Log.e(TAG,"OnConnectStatusChanged,state:" + state);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(state == BluetoothProfile.STATE_CONNECTED){
                    textModelName.setText(bdManager.getModelName());
                }
            }
        });
    }
    @Override
    public void OnHeartBeatChanged(final String rate){
        Log.e(TAG,"OnHeartBeatChanged,rate:" + rate);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((HomeHearBeatView)mContent.getChildAt(0)).OnHeartBeatChanged(rate); //setHeartBeat(rate);
            }
        });
    }
    public void OnStepsChanged(final String steps){
        Log.e(TAG,"OnStepsChanged,steps:" + steps);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((HomeStepView)mContent.getChildAt(2)).OnStepChanged(steps); //setSteps(steps);
            }
        });
    }
    public void OnTemperatureChanged(final String temperature){
        Log.e(TAG,"OnTemperatureChanged,temperature:" + temperature);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((HomeTemperatureView)mContent.getChildAt(3)).OnTempChanged(temperature); //setTempereature(temperature);
            }
        });
    }
    public void OnBatteryChanged(final String percent){
        Log.e(TAG,"OnBatteryChanged,percent:" + percent);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textBatteryPercent.setText(percent);
            }
        });
    }


}
