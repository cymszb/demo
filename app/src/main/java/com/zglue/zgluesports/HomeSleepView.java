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

public class HomeSleepView extends FrameLayout {
    private Context mContext;
    //private TextView textTempValue;
    private ImageButton btnFindStart;
    private ImageView statusDot;
    private BluetoothDataManager bdManager;
    private boolean isStarted = false;

    public HomeSleepView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public HomeSleepView(Context context, AttributeSet attrs) {
        super(context,attrs);
        mContext = context;
        init(attrs);
    }
    public HomeSleepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        bdManager = BluetoothDataManager.getInstance(mContext);
        isStarted = bdManager.isFindMeStarted();

        View view = LayoutInflater.from(mContext).inflate(R.layout.home_sleep_view,null);
        btnFindStart = (ImageButton)view.findViewById(R.id.btn_find_me);
        statusDot = (ImageView)view.findViewById(R.id.status_dot);

        btnFindStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDataManager.getInstance(getContext()).startFindeMe(isStarted = !isStarted/*!bdManager.isTempStarted()*/);
                updateData();
            }
        });
        updateData();
        updateStatus(bdManager.getLED1ConnStatus());
        addView(view);
    }

    private void updateData(){
        if(isStarted/*bdManager.isTempStarted()*/){
            btnFindStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_24px));
        }else{
            btnFindStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
        }
    }

    private void updateStatus(int progress){

        if(bdManager.getConnectionState() != BluetoothDataManager.STATE_CONNECTED){
            btnFindStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
            statusDot.setImageDrawable(getResources().getDrawable(R.drawable.circle_grey));
            setButtonEnable(false);
            return;
        }

        switch (progress){
            case BluetoothDataManager.SENSOR_CONN_IN_PROGRESS:
                setButtonEnable(false);
                break;
            case BluetoothDataManager.SENSOR_CONN_ON:
                btnFindStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_24px));
                statusDot.setImageDrawable(getResources().getDrawable(R.drawable.circle_green));
                setButtonEnable(true);
                break;
            case BluetoothDataManager.SENSOR_CONN_OFF:
                btnFindStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
                statusDot.setImageDrawable(getResources().getDrawable(R.drawable.circle_grey));
                setButtonEnable(true);
                break;
            default:
                break;
        }
    }

    public void setButtonEnable(boolean isEnable){
        btnFindStart.setAlpha(isEnable?1f:0.5f);
        btnFindStart.setEnabled(isEnable);
    }

    public void OnLED1Changed(int progress){
        updateStatus(progress);
    }

    public void OnDeviceConnectStatusChanged( int state){
        if(state == BluetoothProfile.STATE_CONNECTED){
            setButtonEnable(true);
            //isStarted = true;
        }else{
            setButtonEnable(false);
            //isStarted = false;
        }
        updateData();
    }

}
