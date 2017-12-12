package com.zglue.zgluesports;

import com.zglue.zgluesports.bluetooth.BluetoothDataManager;

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


        btnTempStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDataManager.getInstance(getContext()).startTemperature(isStarted = !isStarted/*!bdManager.isTempStarted()*/);
                updateView();
            }
        });
        updateView();
        addView(view);
    }

    private void updateView(){
        if(isStarted/*bdManager.isTempStarted()*/){
            btnTempStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_24px));
            textTempValue.setText(String.valueOf(bdManager.getTemperature()));
        }else{
            btnTempStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
            textTempValue.setText("0");
        }
    }
    public void OnTempChanged(float temp){
        if(bdManager.isTempStatusChanging())
            return;
        isStarted = true;
        updateView();
    }


    public void setTempereature(String temp){
        textTempValue.setText(temp);
    }
}
