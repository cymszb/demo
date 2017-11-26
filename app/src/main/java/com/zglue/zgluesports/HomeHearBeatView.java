package com.zglue.zgluesports;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by yuancui on 11/26/17.
 */

public class HomeHearBeatView extends FrameLayout {
    private Context mContext;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_hear_beat_view,null);
        addView(view);
    }
}
