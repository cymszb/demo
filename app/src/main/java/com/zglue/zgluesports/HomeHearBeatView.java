package com.zglue.zgluesports;

import com.zglue.zgluesports.bluetooth.BluetoothDataManager;

import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yuancui on 11/26/17.
 */

public class HomeHearBeatView extends FrameLayout {
    private Context mContext;
    private TextView textHbValue;
    private ImageButton btnHbStart;
    private ImageView statusDot;
    private ViewGroup progressBar;
    private TextView progressTime;

    private BluetoothDataManager bdManager;
    private boolean isStarted = false;

    public HomeHearBeatView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public HomeHearBeatView(Context context, AttributeSet attrs) {
        super(context,attrs);
        mContext = context;
        init(attrs);
    }
    public HomeHearBeatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        mContext = context;
        init(attrs);
    }


    private void init(AttributeSet attrs){
        bdManager = BluetoothDataManager.getInstance(mContext);
        isStarted = bdManager.isHearBeatStarted();
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_hear_beat_view,null);
        btnHbStart = (ImageButton)view.findViewById(R.id.btn_hb_start);
        textHbValue = (TextView)view.findViewById(R.id.text_hb_value);
        progressBar = (ViewGroup)view.findViewById(R.id.hb_progress_bar);
        progressTime = (TextView)view.findViewById(R.id.hb_progress_time);
        statusDot = (ImageView)view.findViewById(R.id.status_dot);

        btnHbStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDataManager.getInstance(getContext()).startHeartbeat(isStarted = !isStarted/*bdManager.isHearBeatStarted()*/);
                updateData();
            }
        });
        updateData();
        updateStatus(bdManager.getHeartRateConnStatus());
        addView(view);
        //startProgress(30);
    }

    private void updateData(){
        if(isStarted/*bdManager.isHearBeatStarted()*/){
            btnHbStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_24px));
            textHbValue.setText(String.valueOf(bdManager.getHeartBeat()));
        }else{
            btnHbStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
            textHbValue.setText(String.valueOf(bdManager.getLastRecord()));
        }
    }

    private void updateStatus(int progress){

        if(bdManager.getConnectionState() != BluetoothDataManager.STATE_CONNECTED){
            btnHbStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
            statusDot.setImageDrawable(getResources().getDrawable(R.drawable.circle_grey));
            setButtonEnable(false);
            return;
        }

        switch (progress){
            case BluetoothDataManager.SENSOR_CONN_IN_PROGRESS:
                setButtonEnable(false);
                break;
            case BluetoothDataManager.SENSOR_CONN_ON:
                btnHbStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_24px));
                statusDot.setImageDrawable(getResources().getDrawable(R.drawable.circle_green));
                startProgress(30);
                setButtonEnable(true);
                break;
            case BluetoothDataManager.SENSOR_CONN_OFF:
                btnHbStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
                statusDot.setImageDrawable(getResources().getDrawable(R.drawable.circle_grey));
                forceStopProgress();
                setButtonEnable(true);
                break;
            default:
                break;
        }
    }

    public void OnHeartBeatChanged(int rate){
        if(bdManager.isHearBeatStatusChanging())
            return;
        isStarted = true;
        updateData();
    }

    public void OnHeartRateSensorChanged(int progress){
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

    public void setButtonEnable(boolean isEnable){
        btnHbStart.setAlpha(isEnable?1f:0.5f);
        btnHbStart.setEnabled(isEnable);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what > 0) {
                progressTime.setText(String.valueOf(msg.what));
            } else {
                forceStopProgress();
                btnHbStart.performClick();
            }
        }
    };


    Timer mProgressTimer = new Timer();

    TimerTask mTimerTask = null;

    boolean isTesting = false;

    private void startProgress(final int time){
        if(!isTesting) {
            progressBar.setVisibility(View.VISIBLE);
            progressTime.setText(String.valueOf(time));

            mTimerTask = new TimerTask() {
                int second = time;

                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = second;
                    handler.sendMessage(msg);
                    second--;
                }
            };
            mProgressTimer.schedule(mTimerTask, 0, 1000);
            isTesting = true;
        }

    }

    private void forceStopProgress(){
        bdManager.recordOnce();
        if(isTesting == true) {
            progressBar.setVisibility(View.INVISIBLE);
            if (mTimerTask != null) {
                mTimerTask.cancel();
                mTimerTask = null;
            }
            isTesting = false;
        }
    }


    public void setHeartBeat(String rate){
        textHbValue.setText(rate);
    }

}
