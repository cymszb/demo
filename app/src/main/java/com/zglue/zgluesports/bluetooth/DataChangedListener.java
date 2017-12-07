package com.zglue.zgluesports.bluetooth;

/**
 * Created by yuancui on 12/5/17.
 */

public interface DataChangedListener {
    void OnHeartBeatChanged(String rate);
    void OnStepsChanged(String steps);
    void OnTemperatureChanged(String temperature);
    void OnBatteryChanged(String percent);

}
