package com.zglue.zgluesports;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * Created by Micki on 2017/11/5.
 */

public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

    public CustomFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int positon){
        return null;
    }

    @Override
    public int getCount(){return 0;}


    @Override
    public Object instantiateItem(ViewGroup container, int position){
        return null;
    }



}


