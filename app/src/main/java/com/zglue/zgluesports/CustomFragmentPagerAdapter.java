package com.zglue.zgluesports;

import com.zglue.zgluesports.bluetooth.PersonalDataManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Micki on 2017/11/5.
 */

public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

    public final static int PAGE_COUNT = 4;
    private HomeFragment mHomeFragment;
    private ActivityFragment mActivityFragment;
    private HealthFragment mHealthFragment;
    private PersionalInfoFragment mPersionalInfoFragment;

    public CustomFragmentPagerAdapter(FragmentManager fm){
        super(fm);

        mHomeFragment = new HomeFragment();
        mActivityFragment =  new ActivityFragment();
        mHealthFragment = new  HealthFragment();
        mPersionalInfoFragment =  new PersionalInfoFragment();
    }


    @Override
    public Fragment getItem(int positon){

        switch (positon){
            case 0:
                return mHomeFragment;
            case 1:
                return mActivityFragment;
            case 2:
                return mHealthFragment;
            case 3:
                return mPersionalInfoFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount(){return PAGE_COUNT;}



}


