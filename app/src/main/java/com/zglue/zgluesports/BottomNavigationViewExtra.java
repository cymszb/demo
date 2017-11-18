package com.zglue.zgluesports;

import android.content.Context;
import android.support.design.widget.BottomNavigationView;
import android.util.AttributeSet;

/**
 * Created by Micki on 2017/11/5.
 */

public class BottomNavigationViewExtra extends BottomNavigationView {
    public BottomNavigationViewExtra(Context context) {
        super(context);
        init();
    }

    public BottomNavigationViewExtra(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }
    public BottomNavigationViewExtra(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        init();
    }




    private void init(){
        BottomNavigationViewHelper.disableShiftMode(this);
    }
}
