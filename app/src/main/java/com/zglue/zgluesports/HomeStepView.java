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

public class HomeStepView extends FrameLayout {
    private Context mContext;
    private TextView textStepValue;
    private ImageButton btnStepStart;
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

        btnStepStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDataManager.getInstance(getContext()).startSteps(isStarted = !isStarted/*!bdManager.isStepStarted()*/);
                updateView();
            }
        });
        updateView();
        addView(view);
    }

    private void updateView(){
        if(isStarted/*bdManager.isStepStarted()*/){
            btnStepStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_24px));
            textStepValue.setText(bdManager.getDailySteps());
        }else{
            btnStepStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
            textStepValue.setText("0");
        }
    }

    public void OnStepChanged(String steps){
        isStarted = true;
        updateView();
    }

    public void setSteps(String steps){
        textStepValue.setText(steps);
    }

}
