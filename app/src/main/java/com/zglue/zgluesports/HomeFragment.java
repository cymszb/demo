package com.zglue.zgluesports;

import com.zglue.zgluesports.bluetooth.BluetoothDataManager;
import com.zglue.zgluesports.bluetooth.ConnectionListener;
import com.zglue.zgluesports.bluetooth.DataChangedListener;
import com.zglue.zgluesports.bluetooth.SensorConnectionStatusListener;

import android.bluetooth.BluetoothDevice;
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

public class HomeFragment extends Fragment implements ConnectionListener,DataChangedListener,SensorConnectionStatusListener{
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

    HomeAdapter mHomeAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bdManager = BluetoothDataManager.getInstance(getContext());
        mHomeAdapter = new HomeAdapter(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.from(this.getContext()).inflate(R.layout.home,null);
        mPairBtn = (ImageButton)mFragmentView.findViewById(R.id.pair_button);
        textModelName = (TextView) mFragmentView.findViewById(R.id.text_model_name);
        textBatteryPercent = (TextView) mFragmentView.findViewById(R.id.text_battery_percent);
        textModelName.setText(bdManager.getModelName()!=null?bdManager.getModelName():"No Device");
        textBatteryPercent.setText(String.valueOf(bdManager.getBatteryPercent())+"%");
        initPairButton(mPairBtn);
        mContent = (GridView) mFragmentView.findViewById(R.id.content_grid);
        mContent.setAdapter(mHomeAdapter);
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
        bdManager.addSensorConnectionStatusListener(this);
        updateAllViewStatus();;
        if(bdManager.isDeviceAvailable()){
            textModelName.setText(bdManager.getModelName());
            //mPairBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_bluetooth_connected_white_24px));
            //mPairBtn.setBackgroundResource(R.drawable.background_pair_btn);
        }else{
            textModelName.setText("No Device");
            //mPairBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_bluetooth_disabled_white_24px_));
            //mPairBtn.setBackgroundResource(R.drawable.background_unpair_btn);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        bdManager.removeConnectionListener(this);
        bdManager.removeDataChangedListener(this);
        bdManager.removeSensorConnectionStatusListener(this);
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

    private void updateAllViews(){
        updateAllViewStatus();
        //updateAllViewData();
    }

    private void updateAllViewStatus(){
        updateHeartRateStatus();
        updateStepsStatus();
        updateTemperatureStatus();
        updateLED1Status();
    }

    void updateHeartRateStatus(){
        if(mContent.getChildAt(2)!=null) {
            ((HomeHearBeatView) mContent.getChildAt(2)).OnDeviceConnectStatusChanged(bdManager.getConnectionState());
            ((HomeHearBeatView) mContent.getChildAt(2)).OnHeartRateSensorChanged(bdManager.getHeartRateConnStatus());
        }
    }

    void updateStepsStatus(){
        if(mContent.getChildAt(1)!=null) {
            ((HomeStepView) mContent.getChildAt(1)).OnDeviceConnectStatusChanged(bdManager.getConnectionState());
            ((HomeStepView) mContent.getChildAt(1)).OnStepsSensorChanged(bdManager.getStepConnStatus());
        }
    }

    void updateTemperatureStatus(){
        if(mContent.getChildAt(0)!=null) {
            ((HomeTemperatureView) mContent.getChildAt(0)).OnDeviceConnectStatusChanged(bdManager.getConnectionState());
            ((HomeTemperatureView)mContent.getChildAt(0)).OnTemperatureSensorChanged(bdManager.getTemperatureConnStatus());
        }
    }

    void updateLED1Status(){
        if(mContent.getChildAt(3)!=null) {
            ((HomeSleepView) mContent.getChildAt(3)).OnDeviceConnectStatusChanged(bdManager.getConnectionState());
            ((HomeSleepView)mContent.getChildAt(3)).OnLED1Changed(bdManager.getLED1ConnStatus());
        }
    }

    private void updateAllViewData(){
        updateHeartRate();
        updateSteps();
        updateTemperature();
    }

    void updateHeartRate(){
        if(mContent.getChildAt(2)!=null)
        ((HomeHearBeatView)mContent.getChildAt(2)).OnHeartBeatChanged(bdManager.getHeartBeat());
    }

    void updateSteps(){
        if(mContent.getChildAt(1)!=null)
        ((HomeStepView)mContent.getChildAt(1)).OnStepChanged(bdManager.getDailySteps());
    }

    void updateTemperature(){
        if(mContent.getChildAt(0)!=null)
        ((HomeTemperatureView)mContent.getChildAt(0)).OnTempChanged(bdManager.getTemperature());
    }

    public void OnDeviceConnectStatusChanged(final BluetoothDevice device, final int state){
        Log.e(TAG,"OnDeviceConnectStatusChanged,state:" + state);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }

                textModelName.setText(bdManager.getModelName());
                textBatteryPercent.setText(String.valueOf(bdManager.getBatteryPercent())+"%");

                updateAllViewStatus();

            }
        });
    }
    @Override
    public void OnHeartBeatChanged(final int rate){
        Log.e(TAG,"OnHeartBeatChanged,rate:" + rate);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
               updateHeartRate();
            }
        });
    }
    public void OnStepsChanged(final int steps){
        Log.e(TAG,"OnStepsChanged,steps:" + steps);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                updateSteps();
            }
        });
    }
    public void OnTemperatureChanged(final float temperature){
        Log.e(TAG,"OnTemperatureChanged,temperature:" + temperature);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                updateTemperature();
            }
        });
    }
    public void OnBatteryChanged(final int percent){
        Log.e(TAG,"OnBatteryChanged,percent:" + percent);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                textBatteryPercent.setText(String.valueOf(percent)+"%");
            }
        });
    }


    public void OnBatterySensorChanged(final int progress){
        Log.e(TAG,"OnBatterySensorChanged,progress:" + progress);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                textBatteryPercent.setText(String.valueOf(bdManager.getBatteryPercent())+ "%");
            }
        });
    }
    public void OnHeartRateSensorChanged(final int progress){
        Log.e(TAG,"OnHeartRateSensorChanged,progress:" + progress);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                ((HomeHearBeatView)mContent.getChildAt(2)).OnHeartRateSensorChanged(progress);
            }
        });
    }
    public void OnStepsSensorChanged(final int progress){
        Log.e(TAG,"OnStepsSensorChanged,progress:" + progress);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                ((HomeStepView)mContent.getChildAt(1)).OnStepsSensorChanged(progress);
            }
        });

    }
    public void OnTemperatureSensorChanged(final int progress){
        Log.e(TAG,"OnTemperatureSensorChanged,progress:" + progress);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                ((HomeTemperatureView)mContent.getChildAt(0)).OnTemperatureSensorChanged(progress);
            }
        });
    }

    public void OnLED1Changed(final int progress){
        Log.e(TAG,"OnLED1Changed,progress:" + progress);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                ((HomeSleepView)mContent.getChildAt(3)).OnLED1Changed(progress);
            }
        });
    }


}
