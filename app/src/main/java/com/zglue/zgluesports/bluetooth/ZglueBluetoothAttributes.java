package com.zglue.zgluesports.bluetooth;

/**
 * Created by yuancui on 12/2/17.
 */


/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class ZglueBluetoothAttributes {

    /*Common Attributes*/
    public final static String ATTR_CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb".toLowerCase();
    public final static String ATTR_MANUFACTURE_NAME = "00002a29-0000-1000-8000-00805f9b34fb".toLowerCase();

    /* Temperature */
    public final static String SERVICE_TEMP = "EDFEC62E-9910-0BAC-5241-D8BDA6932B00".toLowerCase();
    //Enable/Disable the Temperature feature :1 - Enable,0 - Disable
    public final static String ATTR_TEMP_ENABLE  = "EDFEC62E-9910-0BAC-5241-D8BDA6932B01".toLowerCase();
    public final static String ATTR_TEMP_VALUE   = "EDFEC62E-9910-0BAC-5241-D8BDA6932B02".toLowerCase();

    /* HearRate*/
    public final static String ATTR_HEART_ENABLE = "EDFEC62E-9910-0BAC-5241-D8BDA6932B09".toLowerCase();
    public final static String ATTR_HEART_VALUE  = "EDFEC62E-9910-0BAC-5241-D8BDA6932B0A".toLowerCase();

    /* Step Counter*/
    public final static String ATTR_STEPS_ENABLE = "EDFEC62E-9910-0BAC-5241-D8BDA6932B06".toLowerCase();
    public final static String ATTR_STEPS_VALUE  = "EDFEC62E-9910-0BAC-5241-D8BDA6932B07".toLowerCase();
    public final static String ATTR_STEPS_FEATURE= "EDFEC62E-9910-0BAC-5241-D8BDA6932B08".toLowerCase();

    /* Battery */
    public final static String ATTR_BETTARY_VALUE = "EDFEC62E-9910-0BAC-5241-D8BDA6932B03".toLowerCase();
    public final static String ATTR_BETTARY_TIM  = "EDFEC62E-9910-0BAC-5241-D8BDA6932B04".toLowerCase();
    public final static String ATTR_BETTARY_CHARGE_RATE= "EDFEC62E-9910-0BAC-5241-D8BDA6932B05".toLowerCase();

    /*Vibrator*/
    public final static String ATTR_VIBRATOR_ENABLE = "EDFEC62E-9910-0BAC-5241-D8BDA6932A0D".toLowerCase();
    public final static String ATTR_VIBRATOR_AUTO_TURN_OFF  = "EDFEC62E-9910-0BAC-5241-D8BDA6932A0E".toLowerCase();
    public final static String ATTR_VIBRATOR_DURATION= "EDFEC62E-9910-0BAC-5241-D8BDA6932A0F".toLowerCase();


}
