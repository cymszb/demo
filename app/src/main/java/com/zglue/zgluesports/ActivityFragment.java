package com.zglue.zgluesports;

import com.github.mikephil.charting.data.Entry;
import com.zglue.zgluesports.bluetooth.BluetoothDataManager;
import com.zglue.zgluesports.bluetooth.DataChangedListener;
import com.zglue.zgluesports.bluetooth.PersonalDataManager;
import com.zglue.zgluesports.bluetooth.RecordItem;
import com.zglue.zgluesports.bluetooth.RecordManager;
import com.zglue.zgluesports.widget.ArcProgress;
import com.zglue.zgluesports.widget.ChartView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Micki on 2017/11/5.
 */

public class ActivityFragment extends Fragment implements DataChangedListener{
    private static final String TAG = ActivityFragment.class.getName();

    private View mFragmentView;

    private TextView mTargetSteps;
    private TextView mCurSteps;
    private ArcProgress mArcProgress;
    private ChartView mChartView;

    private TextView mCalories;
    private TextView mFat;
    private TextView mDistance;

    private PersonalDataManager pdManager;
    private BluetoothDataManager bdManager;
    private RecordManager mRecordManager;

    private int mTargetStepsValue = 0;
    private int mCurStepValue = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pdManager = PersonalDataManager.getInstance(getContext());
        bdManager = BluetoothDataManager.getInstance(getContext());
        mRecordManager = RecordManager.getInstance(getContext());

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.from(this.getContext()).inflate(R.layout.activity, null);
        mTargetSteps = (TextView)mFragmentView.findViewById(R.id.progress_bar_target);
        mCurSteps = (TextView) mFragmentView.findViewById(R.id.progress_bar_steps);
        mArcProgress = (ArcProgress)mFragmentView.findViewById(R.id.arc_progress);
        mChartView = (ChartView)mFragmentView.findViewById(R.id.chat_view);

        mCalories = (TextView)mFragmentView.findViewById(R.id.activity_calories);
        mFat = (TextView)mFragmentView.findViewById(R.id.activity_fat);
        mDistance = (TextView)mFragmentView.findViewById(R.id.activity_distance);

        mRecordManager.queryAllSteps(new RecordManager.QueryCallback() {
            @Override
            public void onDone(final ArrayList<RecordItem> items) {
                Log.e("Demo","Query steps done:" + items.size());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ArrayList<Entry> entries = new ArrayList<>(7);
                        for(int i = 0; i < 6; i++){
                            if(i+1 <= items.size()) {
                                entries.add(new Entry(i, items.get(i)._value));
                            }else{
                                entries.add(new Entry(i,0));
                            }
                        }
                        mChartView.setData(entries);
                    }
                });
            }
        });

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
    public void OnTemperatureChanged(float temperature){}
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

    /*体重60公斤的人，长跑8公里，那么消耗的热量＝60×8×1.036＝497.28 kcal(千卡)*/

    private float getCalories(int steps){
        return getDistance(steps) * ((float)PersonalDataManager.getInstance(getContext()).getWeight()) * 1.036f / 1000;
    }

    private float getFatBurn(int steps){
        return getCalories(steps) * 0.13f;
    }
}
