package com.zglue.zgluesports.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by yuancui on 12/5/17.
 */

public interface ConnectionListener {
    void OnConnectStatusChanged(BluetoothDevice device,int state);
}
