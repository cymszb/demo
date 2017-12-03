package com.zglue.zgluesports.bluetooth;

/**
 * Created by yuancui on 12/2/17.
 */


/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class ZglueBluetoothAttributes {

    /*Common Attributes*/
    public final static String ATTR_CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    public final static String ATTR_MANUFACTURE_NAME = "00002a29-0000-1000-8000-00805f9b34fb";

    /* Temperature */
    public final static String SERVICE_TEMP = "EDFEC62E-9910-0BAC-5241-D8BDA6932B00";
    //Enable/Disable the Temperature feature :1 - Enable,0 - Disable
    public final static String ATTR_TEMP_ENABLE  = "EDFEC62E-9910-0BAC-5241-D8BDA6932B01";
    public final static String ATTR_TEMP_VALUE   = "EDFEC62E-9910-0BAC-5241-D8BDA6932B02";
}
