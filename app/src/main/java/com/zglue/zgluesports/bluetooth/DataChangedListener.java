package com.zglue.zgluesports.bluetooth;

/**
 * Created by yuancui on 12/5/17.
 */

public interface DataChangedListener {
    void OnHeartBeatChanged(int rate);
    void OnStepsChanged(int steps);
    void OnTemperatureChanged(int temperature);
    void OnBatteryChanged(int percent);

}
