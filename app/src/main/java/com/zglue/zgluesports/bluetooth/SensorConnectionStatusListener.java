package com.zglue.zgluesports.bluetooth;

/**
 * Created by yuancui on 12/17/17.
 */

public interface SensorConnectionStatusListener {
    void OnBatterySensorChanged(int progress);
    void OnHeartRateSensorChanged(int progress);
    void OnStepsSensorChanged(int progress);
    void OnTemperatureSensorChanged(int progress);
}
