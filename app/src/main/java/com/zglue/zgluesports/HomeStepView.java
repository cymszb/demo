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

public class HomeStepView extends FrameLayout {
    private Context mContext;
    private TextView textStepValue;
    private ImageButton btnStepStart;
    private ImageView statusDot;
    private ImageView statusFeature;
    private BluetoothDataManager bdManager;
    private boolean isStarted = false;
    public HomeStepView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public HomeStepView(Context context, AttributeSet attrs) {
        super(context,attrs);
        mContext = context;
        init(attrs);
    }
    public HomeStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        mContext = context;
        init(attrs);
    }


    private void init(AttributeSet attrs){
        bdManager = BluetoothDataManager.getInstance(mContext);
        isStarted = bdManager.isStepStarted();
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_step_view,null);
        btnStepStart = (ImageButton)view.findViewById(R.id.btn_step_start);
        textStepValue = (TextView)view.findViewById(R.id.text_step_value);
        statusDot = (ImageView)view.findViewById(R.id.status_dot);
        statusFeature = (ImageView)view.findViewById(R.id.status_feature);
        btnStepStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDataManager.getInstance(getContext()).startSteps(isStarted = !isStarted/*!bdManager.isStepStarted()*/);
                updateData();
            }
        });
        updateData();
        updateStatus(bdManager.getStepConnStatus());
        addView(view);
    }

    private void updateData(){
        if(isStarted/*bdManager.isStepStarted()*/){
            btnStepStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_24px));
            textStepValue.setText(String.valueOf(bdManager.getDailySteps()));
        }else{
            btnStepStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
            textStepValue.setText("0");
        }

        if(bdManager.getStepFeature() == BluetoothDataManager.STEP_STAND){
            statusFeature.setImageDrawable(getResources().getDrawable(R.drawable.ic_man_stand));
        }else if(bdManager.getStepFeature() == BluetoothDataManager.STEP_FEATURE_WALK){
            statusFeature.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_walk_white_24px));

        }else if(bdManager.getStepFeature() == BluetoothDataManager.STEP_FEATURE_RUN){
            statusFeature.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_run_white_24px));

        }
    }

    private void updateStatus(int progress){

        if(bdManager.getConnectionState() != BluetoothDataManager.STATE_CONNECTED){
            btnStepStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
            statusDot.setImageDrawable(getResources().getDrawable(R.drawable.circle_grey));
            setButtonEnable(false);
            return;
        }

        switch (progress){
            case BluetoothDataManager.SENSOR_CONN_IN_PROGRESS:
                setButtonEnable(false);
                break;
            case BluetoothDataManager.SENSOR_CONN_ON:
                btnStepStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_24px));
                statusDot.setImageDrawable(getResources().getDrawable(R.drawable.circle_green));
                setButtonEnable(true);
                break;
            case BluetoothDataManager.SENSOR_CONN_OFF:
                btnStepStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
                statusDot.setImageDrawable(getResources().getDrawable(R.drawable.circle_grey));
                setButtonEnable(true);
                break;
            default:
                break;
        }
    }

    public void OnStepChanged(int steps){
        if(bdManager.isStepStatusChanging())
            return;
        isStarted = true;
        updateData();
    }
    public void OnStepsSensorChanged(final int progress) {
        updateStatus(progress);
    }
    public void OnDeviceConnectStatusChanged(final int state){
        //isStarted = false;
        if(state == BluetoothProfile.STATE_CONNECTED){

            setButtonEnable(true);
        }else{
            setButtonEnable(false);
        }
        updateData();

    }

    public void setButtonEnable(boolean isEnable){
        btnStepStart.setAlpha(isEnable?1f:0.5f);
        btnStepStart.setEnabled(isEnable);
    }



    public void setSteps(String steps){
        textStepValue.setText(steps);
    }

}
