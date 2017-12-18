package com.zglue.zgluesports;

import com.zglue.zgluesports.bluetooth.BluetoothDataManager;

import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yuancui on 11/26/17.
 */

public class HomeTemperatureView extends FrameLayout {
    private Context mContext;

    private TextView textTempValue;
    private ImageButton btnTempStart;
    private ImageView statusDot;
    private BluetoothDataManager bdManager;
    private boolean isStarted = false;
    public HomeTemperatureView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public HomeTemperatureView(Context context, AttributeSet attrs) {
        super(context,attrs);
        mContext = context;
        init(attrs);
    }
    public HomeTemperatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        bdManager = BluetoothDataManager.getInstance(mContext);
        isStarted = bdManager.isTempStarted();
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_temperature_view,null);
        btnTempStart = (ImageButton)view.findViewById(R.id.btn_temp_start);
        textTempValue = (TextView)view.findViewById(R.id.text_temp_value);
        statusDot = (ImageView)view.findViewById(R.id.status_dot);


        btnTempStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDataManager.getInstance(getContext()).startTemperature(isStarted = !isStarted/*!bdManager.isTempStarted()*/);
                updateData();
            }
        });
        updateStatus(bdManager.getTemperatureConnStatus());
        updateData();
        addView(view);
    }

    private void updateData(){
        if(isStarted/*bdManager.isTempStarted()*/){
            btnTempStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_24px));
            textTempValue.setText(String.valueOf(bdManager.getTemperature()));
        }else{
            btnTempStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
            textTempValue.setText("0");
        }
    }

    private void updateStatus(int progress){

        if(bdManager.getConnectionState() != BluetoothDataManager.STATE_CONNECTED){
            btnTempStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
            statusDot.setImageDrawable(getResources().getDrawable(R.drawable.circle_grey));
            setButtonEnable(false);
            return;
        }

        switch (progress){
            case BluetoothDataManager.SENSOR_CONN_IN_PROGRESS:
                setButtonEnable(false);
                break;
            case BluetoothDataManager.SENSOR_CONN_ON:
                btnTempStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_24px));
                statusDot.setImageDrawable(getResources().getDrawable(R.drawable.circle_green));
                setButtonEnable(true);
                break;
            case BluetoothDataManager.SENSOR_CONN_OFF:
                btnTempStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
                statusDot.setImageDrawable(getResources().getDrawable(R.drawable.circle_grey));
                setButtonEnable(true);
                break;
            default:
                break;
        }
    }


    public void OnTempChanged(float temp){
        if(bdManager.isTempStatusChanging())
            return;
        isStarted = true;
        updateData();
    }

    public void OnTemperatureSensorChanged(int progress){
        updateStatus(progress);
    }

    public void OnDeviceConnectStatusChanged(final int state){
        if(state == BluetoothProfile.STATE_CONNECTED){
            setButtonEnable(true);
            //isStarted = true;
        }else{
            setButtonEnable(false);
            //isStarted = false;
        }
        updateData();

    }

    public void setButtonEnable(boolean isEnable){
        btnTempStart.setAlpha(isEnable?1f:0.5f);
        btnTempStart.setEnabled(isEnable);
    }


    public void setTempereature(String temp){
        textTempValue.setText(temp);
    }
}
