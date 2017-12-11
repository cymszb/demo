package com.zglue.zgluesports;

import com.zglue.zgluesports.bluetooth.BluetoothDataManager;
import com.zglue.zgluesports.bluetooth.DataChangedListener;
import com.zglue.zgluesports.bluetooth.PersonalDataManager;
import com.zglue.zgluesports.widget.ArcProgress;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Micki on 2017/11/5.
 */

public class ActivityFragment extends Fragment implements DataChangedListener{
    private static final String TAG = ActivityFragment.class.getName();

    private View mFragmentView;

    private TextView mTargetSteps;
    private TextView mCurSteps;
    private ArcProgress mArcProgress;

    private TextView mCalories;
    private TextView mFat;
    private TextView mDistance;

    private PersonalDataManager pdManager;
    private BluetoothDataManager bdManager;

    private int mTargetStepsValue = 0;
    private int mCurStepValue = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pdManager = PersonalDataManager.getInstance(getContext());
        bdManager = BluetoothDataManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.from(this.getContext()).inflate(R.layout.activity, null);
        mTargetSteps = (TextView)mFragmentView.findViewById(R.id.progress_bar_target);
        mCurSteps = (TextView) mFragmentView.findViewById(R.id.progress_bar_steps);
        mArcProgress = (ArcProgress)mFragmentView.findViewById(R.id.arc_progress);

        mCalories = (TextView)mFragmentView.findViewById(R.id.activity_calories);
        mFat = (TextView)mFragmentView.findViewById(R.id.activity_fat);
        mDistance = (TextView)mFragmentView.findViewById(R.id.activity_distance);
        return mFragmentView;
    }

    @Override
    public void onResume(){
        super.onResume();
        bdManager.addDataChangedListener(this);
        mTargetStepsValue = pdManager.getTargetSteps();
        mCurStepValue = bdManager.getDailySteps();
        mTargetSteps.setText(String.valueOf(mTargetStepsValue));

        updateView();
    }

    @Override
    public void onStop(){
        super.onStop();
        bdManager.removeDataChangedListener(this);
    }



    public void OnHeartBeatChanged(int rate){}
    public void OnStepsChanged(final int steps){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                mCurStepValue = steps;
                updateView();
            }
        });
    }
    public void OnTemperatureChanged(int temperature){}
    public void OnBatteryChanged(int percent){}

    private void updateView(){
        mCurSteps.setText(String.valueOf(mCurStepValue));
        mCalories.setText(String.format("%.1f",getCalories(mCurStepValue)));
        mFat.setText(String.format("%.1f",getFatBurn(mCurStepValue)));
        mDistance.setText(String.format("%.1f",getDistance(mCurStepValue)));


        if(mCurStepValue >= mTargetStepsValue){
            mArcProgress.setProgress(100);
        }else {
            mArcProgress.setProgress((float) mCurStepValue * 100 / (float) mTargetStepsValue);
        }
    }

    private float getDistance(int steps){
        return 0.55f*(float)steps;
    }

    private float getCalories(int steps){
        return getDistance(steps) * ((float)PersonalDataManager.getInstance(getContext()).getWeight()) * 1.036f / 1000;
    }

    private float getFatBurn(int steps){
        return getCalories(steps) * 0.13f;
    }
}
