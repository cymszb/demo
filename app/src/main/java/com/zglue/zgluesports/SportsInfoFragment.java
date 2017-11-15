package com.zglue.zgluesports;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Micki on 2017/11/5.
 */

public class SportsInfoFragment extends Fragment {
    private static final String Tag = SportsInfoFragment.class.getName();

    private View mFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.from(this.getContext()).inflate(R.layout.sports_info,null);
        return mFragmentView;
    }
}
