package com.zglue.zgluesports;

import com.zglue.zgluesports.bluetooth.BluetoothDataManager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by yuancui on 11/26/17.
 */

public class HomeHearBeatView extends FrameLayout {
    private Context mContext;
    private TextView textHbValue;
    private ImageButton btnHbStart;
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


        btnHbStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDataManager.getInstance(getContext()).startHeartbeat(isStarted = !isStarted/*bdManager.isHearBeatStarted()*/);
                updateView();
            }
        });
        updateView();
        addView(view);
    }

    private void updateView(){
        if(isStarted/*bdManager.isHearBeatStarted()*/){
            btnHbStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_24px));
            textHbValue.setText(bdManager.getHeartBeat());
        }else{
            btnHbStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
            textHbValue.setText("0");
        }
    }
    public void OnHeartBeatChanged(String rate){
        isStarted = true;
        updateView();
    }
    public void setHeartBeat(String rate){
        textHbValue.setText(rate);
    }

}
