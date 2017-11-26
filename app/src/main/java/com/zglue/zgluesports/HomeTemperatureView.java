package com.zglue.zgluesports;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by yuancui on 11/26/17.
 */

public class HomeTemperatureView extends FrameLayout {
    private Context mContext;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_temperature_view,null);
        addView(view);
    }
}
