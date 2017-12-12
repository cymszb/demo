package com.zglue.zgluesports.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by yuancui on 12/2/17.
 */

public class BluetoothDataManager {
    final static String TAG = "BluetoothDataManager";

    private Context mContext;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    private RecordDatabase mRecordDb;

    /*Service and Chars*/
    private BluetoothGattService mServiceA = null;
    private BluetoothGattService mServiceB = null;

    private BluetoothGattCharacteristic mHearBeatEnableCharacteristic = null;
    private BluetoothGattCharacteristic mHearBeatValueCharacteristic = null;
    private BluetoothGattCharacteristic mStepsEnableCharacteristic = null;
    private BluetoothGattCharacteristic mStepsValueCharacteristic = null;
    private BluetoothGattCharacteristic mStepsFeatureCharacteristic = null;
    private BluetoothGattCharacteristic mTempEnableCharacteristic = null;
    private BluetoothGattCharacteristic mTempValueCharacteristic = null;

    private BluetoothGattCharacteristic mBatteryValueCharacteristic = null;
    private BluetoothGattCharacteristic mBatteryTimCharacteristic = null;
    private BluetoothGattCharacteristic mBatteryChargeRateCharacteristic = null;

    private BluetoothGattCharacteristic mVibratorEnableCharacteristic = null;
    private BluetoothGattCharacteristic mVibratorAutoTurnOffeCharacteristic = null;
    private BluetoothGattCharacteristic mVibratorDuaratuinCharacteristic = null;

    private BluetoothGattCharacteristic mLEDEnableCharacteristic = null;
    private BluetoothGattCharacteristic mLEDRangeCharacteristic = null;

    private String mModeName;

    private boolean isHearBeatStarted = false;
    private boolean isTempStarted = false;
    private boolean isStepStarted = false;
    private boolean isBatteryStarted = false;
    private boolean isLED1Started = false;

    private boolean isFindMeStarted = false;
/*
    private String mHeartBeat;
    private String mSteps;
    private String mTemperature ;
    private String mBatteryPercent;
*/
    private int mHeartBeat = 0;
    private int mSteps = 0;
    private float mTemperature = 0;
    private int mBatteryPercent = 0;

    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private ArrayList<DataChangedListener> mDataChangedListeners = new ArrayList<>();
    private ArrayList<ConnectionListener> mConnectionListeners = new ArrayList<>();

    private final static UUID UUID_CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR =
            UUID.fromString(ZglueBluetoothAttributes.ATTR_CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR);

    private final static UUID UUID_MANUFACTURE_NAME =
            UUID.fromString(ZglueBluetoothAttributes.ATTR_MANUFACTURE_NAME);

    private final static UUID UUID_SERVICE_B = UUID.fromString(ZglueBluetoothAttributes.SERVICE_B);

    private final static UUID UUID_TEMP_ENABLE = UUID.fromString(ZglueBluetoothAttributes.ATTR_TEMP_ENABLE);
    private final static UUID UUID_TEMP_VALUE = UUID.fromString(ZglueBluetoothAttributes.ATTR_TEMP_VALUE);

    private final static UUID UUID_HEART_ENABLE = UUID.fromString(ZglueBluetoothAttributes.ATTR_HEART_ENABLE);
    private final static UUID UUID_HEART_VALUE= UUID.fromString(ZglueBluetoothAttributes.ATTR_HEART_VALUE);

    private final static UUID UUID_STEPS_ENABLE = UUID.fromString(ZglueBluetoothAttributes.ATTR_STEPS_ENABLE);
    private final static UUID UUID_STEPS_VALUE = UUID.fromString(ZglueBluetoothAttributes.ATTR_STEPS_VALUE);
    private final static UUID UUID_STEPS_FEATURE = UUID.fromString(ZglueBluetoothAttributes.ATTR_STEPS_FEATURE);

    private final static UUID UUID_BETTARY_VALUE = UUID.fromString(ZglueBluetoothAttributes.ATTR_BETTARY_VALUE);
    private final static UUID UUID_BETTARY_TIM = UUID.fromString(ZglueBluetoothAttributes.ATTR_BETTARY_TIM);
    private final static UUID UUID_BETTARY_CHARGE_RATE = UUID.fromString(ZglueBluetoothAttributes.ATTR_BETTARY_CHARGE_RATE);

    private final static UUID UUID_SERVICE_A = UUID.fromString(ZglueBluetoothAttributes.SERVICE_A);

    private final static UUID UUID_VIBRATOR_ENABLE = UUID.fromString(ZglueBluetoothAttributes.ATTR_VIBRATOR_ENABLE);
    private final static UUID UUID_VIBRATOR_AUTO_TURN_OFF = UUID.fromString(ZglueBluetoothAttributes.ATTR_VIBRATOR_AUTO_TURN_OFF);
    private final static UUID UUID_VIBRATOR_DURATION= UUID.fromString(ZglueBluetoothAttributes.ATTR_VIBRATOR_DURATION);

    private final static UUID UUID_LED_ENABLE = UUID.fromString(ZglueBluetoothAttributes.ATTR_LED_ENABLE);
    private final static UUID UUID_LED_RANGE = UUID.fromString(ZglueBluetoothAttributes.ATTR_LED_RANGE);

    private  BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG,"onConnectionStateChange, newState:" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;

                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

                //TODO


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                //TODO
            } else if(newState == BluetoothProfile.STATE_CONNECTING){
                Log.i(TAG, "Connecting from GATT server.");
            }
            mModeName = gatt.getDevice().getName();
            notifyConnectionChanged(gatt.getDevice(),newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG,"onServicesDiscovered, status:" + status);
            if(status == BluetoothGatt.GATT_SUCCESS){
                //TODO
                initDataChannel();
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {

            int value = Integer.valueOf(getCharacteristicValue(characteristic));
            Log.d(TAG,"onCharacteristicRead, characteristic:" + characteristic.getUuid().toString()
            + ";value: " + value);

            if(characteristic.getUuid().equals(UUID_BETTARY_VALUE)){
                mBatteryPercent = value;
                notifyBatteryChanged(mBatteryPercent);
            }else if(characteristic.getUuid().equals(UUID_STEPS_VALUE)){
                mSteps = value;
                notifyStepsChanged(mSteps);
            }else if(characteristic.getUuid().equals(UUID_TEMP_VALUE)){
                mTemperature = value;
                notifyTemperatureChanged(mTemperature);
            }else if(characteristic.getUuid().equals(UUID_HEART_VALUE)){
                mHeartBeat = value;
                notifyHeartBeatChanged(mHeartBeat);
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            //int value = Integer.valueOf(getCharacteristicValue(characteristic));

            String value = getCharacteristicValue(characteristic);
            Log.d(TAG,"onCharacteristicChanged, characteristic:" + characteristic.getUuid().toString()
                    + ";value: " + value);

            if(characteristic.getUuid().equals(UUID_BETTARY_VALUE)){
                mBatteryPercent = Integer.valueOf(value);
                notifyBatteryChanged(mBatteryPercent);
            }else if(characteristic.getUuid().equals(UUID_STEPS_VALUE)){
                mSteps = Integer.valueOf(value);
                notifyStepsChanged(mSteps);
            }else if(characteristic.getUuid().equals(UUID_TEMP_VALUE)){
                mTemperature = Float.valueOf(value);
                notifyTemperatureChanged(mTemperature);
            }else if(characteristic.getUuid().equals(UUID_HEART_VALUE)){
                mHeartBeat = Integer.valueOf(value);
                notifyHeartBeatChanged(mHeartBeat);
            }
        }
    };

    /* CMD type*/
    private final static int TYPE_START_HB = 0;
    private final static int TYPE_START_BATTERY = 1;
    private final static int TYPE_START_STEPS = 2;
    private final static int TYPE_START_TEMP = 3;
    private final static int TYPE_START_VIBERATE = 4;
    private final static int TYPE_START_LED1 = 5;

    public final static int STOP = 0;
    public final static int START = 1;


    HandlerThread mBlueToothThread = new HandlerThread("BluetoothCmd");


    Handler mBlueToothThreadHandler ;

    private static BluetoothDataManager mInstance = null;

    private BluetoothDataManager(Context context){
        mContext = context;
        initialise(context);
        //init(context);
    }

    public static synchronized BluetoothDataManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new BluetoothDataManager(context);
        }
        return mInstance;
    }

    /* Need be called before use */
    private boolean initialise(Context context){

        mRecordDb = RecordDatabase.getInstance(context);
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        mScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mBlueToothThread.start();
        mBlueToothThreadHandler = new Handler(mBlueToothThread.getLooper()){
            @Override
            public void handleMessage (Message msg){
                switch (msg.what){
                    case TYPE_START_HB:
                        startHeartbeatInternal((boolean)msg.obj);
                        break;
                    case TYPE_START_BATTERY:
                        startBatteryInternal((boolean)msg.obj);
                        break;
                    case TYPE_START_STEPS:
                        startStepsInternal((boolean)msg.obj);
                        break;
                    case TYPE_START_TEMP:
                        startTemperatureInternal((boolean)msg.obj);
                        break;
                    case TYPE_START_VIBERATE:
                        startViberateInternal((boolean)msg.obj);
                        break;
                    case TYPE_START_LED1:
                        startLED1Internal((boolean)msg.obj);
                        break;
                    default:
                        break;
                }

            }
        };

        return true;
    }

    public void destroy(){
        mBlueToothThread.quitSafely();
        close();
    }



    private void initDataChannel(){
        mServiceA = findService(UUID_SERVICE_A);
        mServiceB = findService(UUID_SERVICE_B);
        mHearBeatEnableCharacteristic = findCharacteristic(mServiceB,UUID_HEART_ENABLE);
        mHearBeatValueCharacteristic = findCharacteristic(mServiceB,UUID_HEART_VALUE);;
        mStepsEnableCharacteristic = findCharacteristic(mServiceB,UUID_STEPS_ENABLE);;
        mStepsValueCharacteristic = findCharacteristic(mServiceB,UUID_STEPS_VALUE);;
        mStepsFeatureCharacteristic = findCharacteristic(mServiceB,UUID_STEPS_FEATURE);;
        mTempEnableCharacteristic = findCharacteristic(mServiceB,UUID_TEMP_ENABLE);;
        mTempValueCharacteristic = findCharacteristic(mServiceB,UUID_TEMP_VALUE);;

        mBatteryValueCharacteristic = findCharacteristic(mServiceB,UUID_BETTARY_VALUE);
        mBatteryTimCharacteristic = findCharacteristic(mServiceB,UUID_BETTARY_TIM);
        mBatteryChargeRateCharacteristic = findCharacteristic(mServiceB,UUID_BETTARY_CHARGE_RATE);

        mVibratorEnableCharacteristic = findCharacteristic(mServiceA,UUID_VIBRATOR_ENABLE);
        mVibratorAutoTurnOffeCharacteristic = findCharacteristic(mServiceA,UUID_VIBRATOR_AUTO_TURN_OFF);
        mVibratorDuaratuinCharacteristic = findCharacteristic(mServiceA,UUID_VIBRATOR_DURATION);

        mLEDEnableCharacteristic = findCharacteristic(mServiceA,UUID_LED_ENABLE);
        mLEDRangeCharacteristic = findCharacteristic(mServiceA,UUID_LED_RANGE);

        startBattery(true);

       // startSteps(true);


    }

    public boolean isDeviceAvailable(){
        return mBluetoothGatt != null;
    }

    public boolean isSupportHeartBeat(){
        return isDeviceAvailable() && mHearBeatEnableCharacteristic != null && mHearBeatValueCharacteristic != null;
    }

    public boolean isSupportSteps(){
        return isDeviceAvailable() && mStepsEnableCharacteristic != null && mStepsValueCharacteristic != null;
    }

    public boolean isSupportStepsFullFeatrue(){
        return isSupportSteps() && mStepsFeatureCharacteristic != null;
    }

    public boolean isSupportTemperature(){
        return isDeviceAvailable() && mTempEnableCharacteristic != null && mTempValueCharacteristic != null;
    }

    public boolean isSupportBattary(){
        return isDeviceAvailable() && mBatteryValueCharacteristic != null;
    }

    public boolean isSupportBattaryFullFeature(){
        return isSupportBattary() && mBatteryTimCharacteristic != null && mBatteryChargeRateCharacteristic!=null;
    }

    public boolean isSupportVibrator(){
        return isDeviceAvailable() && mVibratorEnableCharacteristic != null;
    }
    public boolean isSupportVibratorFullFeature(){
        return isSupportVibrator() && mVibratorAutoTurnOffeCharacteristic != null && mVibratorDuaratuinCharacteristic != null;
    }

    public boolean isSupportLED1(){
        return isDeviceAvailable() && mLEDRangeCharacteristic != null && mLEDEnableCharacteristic != null;
    }


    public void addDataChangedListener(DataChangedListener listener){
        if(mDataChangedListeners != null){
            mDataChangedListeners.add(listener);
        }
    }

    public void removeDataChangedListener(DataChangedListener listener){
        if(mDataChangedListeners != null){
            mDataChangedListeners.remove(listener);
        }
    }

    public void addConnectionListener(ConnectionListener listener){
        if(mConnectionListeners != null){
            mConnectionListeners.add(listener);
        }
    }

    public void removeConnectionListener(ConnectionListener listener){
        if(mConnectionListeners != null){
            mConnectionListeners.remove(listener);
        }
    }

    private void notifyConnectionChanged(BluetoothDevice device,int state){
        if(mConnectionListeners!=null){
            for(int i=0;i<mConnectionListeners.size();i++){
                mConnectionListeners.get(i).OnConnectStatusChanged(device,state);
            }
        }
    }

    private void notifyHeartBeatChanged(int rate){
        if(mDataChangedListeners!=null){
            for(int i=0;i<mDataChangedListeners.size();i++){
                mDataChangedListeners.get(i).OnHeartBeatChanged(rate);
            }
        }
    }

    private void notifyStepsChanged(int steps){
        if(mDataChangedListeners!=null){
            for(int i=0;i<mDataChangedListeners.size();i++){
                mDataChangedListeners.get(i).OnStepsChanged(steps);
            }
        }
    }

    private void notifyTemperatureChanged(float temperature){
        if(mDataChangedListeners!=null){
            for(int i=0;i<mDataChangedListeners.size();i++){
                mDataChangedListeners.get(i).OnTemperatureChanged(temperature);
            }
        }
    }

    private void notifyBatteryChanged(int percent){
        if(mDataChangedListeners!=null){
            for(int i=0;i<mDataChangedListeners.size();i++){
                mDataChangedListeners.get(i).OnBatteryChanged(percent);
            }
        }
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        Log.e(TAG,"UUID:" + characteristic.getUuid().toString() + " setCharacteristicNotification:" + enabled);
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR);
        descriptor.setValue(enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : new byte[] { 0x00, 0x00 });
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }

    /* data */
    private BluetoothGattService findService(UUID service){
        if(mBluetoothGatt == null)return null;
        return mBluetoothGatt.getService(service);
    }

    private BluetoothGattCharacteristic findCharacteristic(BluetoothGattService service, UUID characteristic){
        if(service == null){
            return null;
        }
        return service.getCharacteristic(characteristic);
    }

    private String getCharacteristicValue(BluetoothGattCharacteristic characteristic){
        final byte[] data = characteristic.getValue();
        int value = 0;
        float temperature = 0.0f;
        if (data != null && data.length > 0) {

            final StringBuilder stringBuilder = new StringBuilder();
            //for(byte byteChar : data) {
            /*
            for(int i = data.length - 1; i >= 0; i--){
                Log.e(TAG, "value: " + data[i]);
                value = (data[i] & 0xff) | (value << 8);
            }
            stringBuilder.append(String.format("%d", value));
            */
            //for(int i = data.length - 1; i >= 0; i--){

            if(characteristic.getUuid().equals(UUID_TEMP_VALUE)) {
                if(data.length == 1) {
                    temperature = temperature + (float) data[0];
                }else if(data.length == 2){
                    temperature = temperature + (float) data[0] + (float) data[1] / 10;
                }else{

                }
                stringBuilder.append(String.format("%.1f", temperature));
            }else{
                for (int i = 0; i < data.length; i++) {
                    Log.e(TAG, "value: " + data[i]);
                    value = ((data[i] & 0xff) | (value << 8));
                }
                stringBuilder.append(String.format("%d", value));
            }


            return stringBuilder.toString();
        }else{
            return new String("0");
        }

    }

    public boolean isFindMeStarted(){
        return isLED1Started();
    }
    public void startFindeMe(boolean start){
        startLED1(start);
    }



    public boolean isLED1Started(){
        return isLED1Started;
    }

    public void startLED1(boolean start){
        mBlueToothThreadHandler.sendMessage(mBlueToothThreadHandler.obtainMessage(TYPE_START_LED1,start));
    }

    public void startLED1Internal(boolean start){
        if(isSupportLED1()){
            /*write enable*/
            byte[] values_write = start?(new byte[]{1}):(new byte[]{0});
            mLEDRangeCharacteristic.setValue(values_write);
            mBluetoothGatt.writeCharacteristic(mLEDRangeCharacteristic);

            moment();
            mLEDEnableCharacteristic.setValue(values_write);
            mBluetoothGatt.writeCharacteristic(mLEDEnableCharacteristic);
            isLED1Started =start;


        }else{
            Log.e(TAG,"Not support led.");
        }
    }

    public void startViberate(boolean start){
        mBlueToothThreadHandler.sendMessage(mBlueToothThreadHandler.obtainMessage(TYPE_START_VIBERATE,start));
    }

    public void startViberateInternal(boolean start){
        if(isSupportVibrator()){
            /*write enable*/
            byte[] values_write = start?(new byte[]{1}):(new byte[]{0});
            mVibratorEnableCharacteristic.setValue(values_write);
            mBluetoothGatt.writeCharacteristic(mVibratorEnableCharacteristic);
        }
    }


    public boolean isHearBeatStarted(){return isHearBeatStarted;}


    public void startHeartbeat(boolean start){
        mBlueToothThreadHandler.sendMessage(mBlueToothThreadHandler.obtainMessage(TYPE_START_HB,start));
    }


    private void startHeartbeatInternal(boolean start){
        if(isSupportHeartBeat()){
            /*write enable*/
            byte[] values_write = start?(new byte[]{1}):(new byte[]{0});
            mHearBeatEnableCharacteristic.setValue(values_write);
            mBluetoothGatt.writeCharacteristic(mHearBeatEnableCharacteristic);
            moment();
            /*Read once and enable notification*/
            if(start)readCharacteristic(mHearBeatValueCharacteristic);
            moment();
            setCharacteristicNotification(mHearBeatValueCharacteristic,start);
            isHearBeatStarted = start;

        }
    }

    public boolean isStepStarted(){return isStepStarted;}

    public void startSteps(boolean start) {
        mBlueToothThreadHandler.sendMessage(mBlueToothThreadHandler.obtainMessage(TYPE_START_STEPS,start));
    }
    private void startStepsInternal(boolean start){
        if(isSupportSteps()){
            /*write enable*/
            byte[] values_write = start?(new byte[]{1}):(new byte[]{0});
            mStepsEnableCharacteristic.setValue(values_write);
            mBluetoothGatt.writeCharacteristic(mStepsEnableCharacteristic);
            moment();
            /*Read once and enable notification*/
            if(start) readCharacteristic(mStepsValueCharacteristic);
            moment();
            setCharacteristicNotification(mStepsValueCharacteristic,start);
            isStepStarted = start;
        }
    }

    public boolean isTempStarted(){return isTempStarted;}

    public void startTemperature(boolean start){
        mBlueToothThreadHandler.sendMessage(mBlueToothThreadHandler.obtainMessage(TYPE_START_TEMP,start));
    }
    private void startTemperatureInternal(boolean start){
        if(isSupportHeartBeat()){
            /* write enable*/
            byte[] values_write = start?(new byte[]{1}):(new byte[]{0});
            mTempEnableCharacteristic.setValue(values_write);
            mBluetoothGatt.writeCharacteristic(mTempEnableCharacteristic);
            moment();
            /*Read once and enable notification*/
            if(start)readCharacteristic(mTempValueCharacteristic);
            moment();
            setCharacteristicNotification(mTempValueCharacteristic,start);
            isTempStarted = start;
        }
    }

    private void moment(){
        try {
            Thread.sleep(1000);
        }catch(Exception e){};
    }

    public boolean isBatteryStarted(){return isBatteryStarted;}

    public void startBattery(boolean start){
        mBlueToothThreadHandler.sendMessage(mBlueToothThreadHandler.obtainMessage(TYPE_START_BATTERY,start));
    }
    private void startBatteryInternal(boolean start){
        if(isSupportBattary()){
            if(start)readCharacteristic(mBatteryValueCharacteristic);
            moment();
            setCharacteristicNotification(mBatteryValueCharacteristic,start);
            isBatteryStarted = start;
        }
    }

    public String getModelName(){
        return mModeName;
    }


    public int getHeartBeat(){
        //if(mHeartBeat == null)return new String("0");
        return mHeartBeat;
        /*
        if (mHeartBeat > 150){
            return 150;
        }else if (mHeartBeat < 60){
            return 60;
        }else{
            return mHeartBeat;
        }
        */
    }

    public int getDailySteps(){
        //if(mSteps == null)return new String("0");
        return mSteps;
        /*
        if (mSteps > 50000){
            return 50000;
        }else if (mSteps < 10){
            return 10;
        }else{
            return mSteps;
        }*/
    }

    public float getTemperature(){
        //if(mTemperature == null)return new String("0");
        return mTemperature;
        /*
        if(mTemperature > 42){
            return 42;
        }else if(mTemperature < 35){
            return 35;
        }else{
            return mTemperature;
        }
        */
    }

    public int getBatteryPercent(){
        //if(mBatteryPercent == null)return new String("0");
        return mBatteryPercent;
        /*
        if(mBatteryPercent < 0){
            return 0;
        }else if(mBatteryPercent > 100){
            return 100;
        }else{
            return mBatteryPercent;
        }
        */
    }


    /* new scan api*/

    final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.v("Callback", "in the callback");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.v("ScanTask", "Some error occurred");
        }
    };


    BluetoothLeScanner mScanner;// = mBluetoothAdapter.getBluetoothLeScanner();

    public void scanBLEDevice(ScanCallback callback){
        //BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();


        //The list for the filters
        ArrayList<ScanFilter> filters = new ArrayList<>();
        ScanFilter.Builder builder = new ScanFilter.Builder();
        //builder.setServiceUuid(new ParcelUuid(UUID_SERVICE_B)).build();
        //filters.add(builder.setDeviceName("ZGLUE-*").build());
        //filters.add(builder.setDeviceName("ZGLUE-*").build());


        //mac adresses of ble devices
        /*
        String[] filterlist = {
                "D4:B4:C8:7E:D1:35",
                "C8:86:3A:91:0C:0C",
                "FD:49:FD:36:04:B4",
                "E9:91:4A:42:AC:3B",
                //... some 20 more addresses
        };

        //adding the mac adresses to the filters list
        for (int i=0; i< filterlist.length ; i++) {
            ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(filterlist[i]).build();
            filters.add(filter);
            Log.v("Filter: "," "+ filters.get(i).getDeviceAddress());
        }
        */
        mScanner.startScan(null, settings, callback);



    }

    public void stopScan(ScanCallback callback){
        mScanner.stopScan(callback);
    }



}
