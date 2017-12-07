package com.zglue.zgluesports;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Micki on 2017/11/5.
 */

public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

    public final static int PAGE_COUNT = 4;

    public CustomFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int positon){

        switch (positon){
            case 0:
                return new HomeFragment();
            case 1:
                return new ActivityFragment();
            case 2:
                return new HealthFragment();
            case 3:
                return new PersionalInfoFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount(){return PAGE_COUNT;}



}


