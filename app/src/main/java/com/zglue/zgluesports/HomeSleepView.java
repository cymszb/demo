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

public class HomeSleepView extends FrameLayout {
    private Context mContext;
    //private TextView textTempValue;
    private ImageButton btnFindStart;
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

        btnFindStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDataManager.getInstance(getContext()).startFindeMe(isStarted = !isStarted/*!bdManager.isTempStarted()*/);
                updateView();
            }
        });
        updateView();

        addView(view);
    }

    private void updateView(){
        if(isStarted/*bdManager.isTempStarted()*/){
            btnFindStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_24px));
        }else{
            btnFindStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_24px));
        }
    }
}
