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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BluetoothDataManager {
    final static String TAG = "BluetoothDataManager";

    private Context mContext;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private String mBluetoothDeviceAddress;
    private String mModelName;

    private BluetoothGatt mBluetoothGatt;

    //private Device mCurrentDevice = null;

    private RecordManager mRecordManager;

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



    private volatile int mHeartRateConnStatus = SENSOR_CONN_OFF;
    private volatile int mTemperatureConnStatus = SENSOR_CONN_OFF;
    private volatile int mStepConnStatus = SENSOR_CONN_OFF;
    private volatile int mBatteryConnStatus = SENSOR_CONN_OFF;
    private volatile int mLED1ConnStatus = SENSOR_CONN_OFF;

    private boolean isFindMeStarted = false;

    private int mLastHeartBeatRecord = 0;
    private int mHeartBeat = 0;

    private int mSteps = 0;

    private float mTemperature = 0;
    private float mLastTemperatureRecord = 0;

    private int mBatteryPercent = 0;

    private  int mStepFeature = STEP_STAND;

    private int mConnectionState = STATE_DISCONNECTED;

    public static final int STEP_STAND = 0;
    public static final int STEP_FEATURE_WALK = 1;
    public static final int STEP_FEATURE_RUN = 2;


    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public static final int SENSOR_CONN_ON = 0;
    public static final int SENSOR_CONN_OFF = 1;
    public static final int SENSOR_CONN_IN_PROGRESS = 2;

    private ArrayList<DataChangedListener> mDataChangedListeners = new ArrayList<>();
    private ArrayList<ConnectionListener> mConnectionListeners = new ArrayList<>();
    private ArrayList<SensorConnectionStatusListener> mSensorConnectionStatusListeners = new ArrayList<>();

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
        mRecordManager = RecordManager.getInstance(context);
        //mRecordDb = RecordDatabase.getInstance(context);
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
                        moment();
                        startHeartbeatInternal((boolean)msg.obj);
                        break;
                    case TYPE_START_BATTERY:
                        moment();
                        startBatteryInternal((boolean)msg.obj);
                        break;
                    case TYPE_START_STEPS:
                        moment();
                        startStepsInternal((boolean)msg.obj);
                        break;
                    case TYPE_START_TEMP:
                        moment();
                        startTemperatureInternal((boolean)msg.obj);
                        break;
                    case TYPE_START_VIBERATE:
                        moment();
                        startViberateInternal((boolean)msg.obj);
                        break;
                    case TYPE_START_LED1:
                        moment(1000);
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

    private void setCharacteristicWriteType(BluetoothGattCharacteristic characteristic, int type){
        if(characteristic != null){
            characteristic.setWriteType(type);
        }
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

        startSteps(true);

        startTemperature(true);

    }

    private void reset(){

        mModelName = new String("No Device");

        mHeartBeat = 0;
        mSteps = 0;
        mTemperature = 0;
        mBatteryPercent = 0;

        setBatteryConnStatus(SENSOR_CONN_OFF);
        setHeartRateConnStatus(SENSOR_CONN_OFF);
        setStepConnStatus(SENSOR_CONN_OFF);
        setTemperatureConnStatus(SENSOR_CONN_OFF);
        setLED1ConnStatus(SENSOR_CONN_OFF);

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
                mConnectionListeners.get(i).OnDeviceConnectStatusChanged(device,state);
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

    public void addSensorConnectionStatusListener(SensorConnectionStatusListener listener){
        if(mSensorConnectionStatusListeners != null){
            mSensorConnectionStatusListeners.add(listener);
        }
    }

    public void removeSensorConnectionStatusListener(SensorConnectionStatusListener listener){
        if(mSensorConnectionStatusListeners != null){
            mSensorConnectionStatusListeners.remove(listener);
        }
    }

    public void notifyBatterySensorChanged(int curStatus){
        if(mSensorConnectionStatusListeners!=null){
            for(int i=0;i<mSensorConnectionStatusListeners.size();i++){
                mSensorConnectionStatusListeners.get(i).OnBatterySensorChanged(curStatus);
            }
        }
    }

    public void notifyHeartRateSensorChanged(int curStatus){
        if(mSensorConnectionStatusListeners!=null){
            for(int i=0;i<mSensorConnectionStatusListeners.size();i++){
                mSensorConnectionStatusListeners.get(i).OnHeartRateSensorChanged(curStatus);
            }
        }
    }

    public void notifyStepsSensorChanged(int curStatus){
        if(mSensorConnectionStatusListeners!=null){
            for(int i=0;i<mSensorConnectionStatusListeners.size();i++){
                mSensorConnectionStatusListeners.get(i).OnStepsSensorChanged(curStatus);
            }
        }
    }

    public void notifyTemperatureSensorChanged(int curStatus){
        if(mSensorConnectionStatusListeners!=null){
            for(int i=0;i<mSensorConnectionStatusListeners.size();i++){
                mSensorConnectionStatusListeners.get(i).OnTemperatureSensorChanged(curStatus);
            }
        }
    }

    public void notifyLED1Changed(int curStatus){
        if(mSensorConnectionStatusListeners!=null){
            for(int i=0;i<mSensorConnectionStatusListeners.size();i++){
                mSensorConnectionStatusListeners.get(i).OnLED1Changed(curStatus);
            }
        }
    }

    private  BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG,"onConnectionStateChange, newState:" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;

                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

                mModelName = gatt.getDevice().getName();

                //TODO
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                reset();
                close();
                Log.i(TAG, "Disconnected from GATT server.");
                //TODO
            } else if(newState == BluetoothProfile.STATE_CONNECTING){
                mConnectionState = STATE_CONNECTING;
                Log.i(TAG, "Connecting from GATT server.");
            }

            notifyConnectionChanged(gatt.getDevice(),mConnectionState);
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

            String value = getCharacteristicValue(characteristic);
            Log.d(TAG,"onCharacteristicChanged, characteristic:" + characteristic.getUuid().toString()
                    + ";value: " + value);

            if(characteristic.getUuid().equals(UUID_BETTARY_VALUE)){
                setBatteryPercent(Integer.valueOf(value));
            }else if(characteristic.getUuid().equals(UUID_STEPS_VALUE)){
                setDailySteps(Integer.valueOf(value));
            }else if(characteristic.getUuid().equals(UUID_TEMP_VALUE)){
                setTemperature(Float.valueOf(value));
            }else if(characteristic.getUuid().equals(UUID_HEART_VALUE)){
                setHeartBeat(Integer.valueOf(value));
            }else if(characteristic.getUuid().equals(UUID_STEPS_FEATURE)){
                setStepFeature(Integer.valueOf(value));
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            String value = getCharacteristicValue(characteristic);
            Log.d(TAG,"onCharacteristicChanged, characteristic:" + characteristic.getUuid().toString()
                    + ";value: " + value);

            if(characteristic.getUuid().equals(UUID_BETTARY_VALUE)){
                setBatteryPercent(Integer.valueOf(value));
            }else if(characteristic.getUuid().equals(UUID_STEPS_VALUE)){
                setDailySteps(Integer.valueOf(value));
            }else if(characteristic.getUuid().equals(UUID_TEMP_VALUE)){
                setTemperature(Float.valueOf(value));
            }else if(characteristic.getUuid().equals(UUID_HEART_VALUE)){
                setHeartBeat(Integer.valueOf(value));
            }else if(characteristic.getUuid().equals(UUID_STEPS_FEATURE)){
                setStepFeature(Integer.valueOf(value));
            }
        }
    };

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

        if(mConnectionState != STATE_DISCONNECTED){
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                notifyConnectionChanged(mBluetoothGatt.getDevice(),mConnectionState);
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
        notifyConnectionChanged(device,mConnectionState);

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

    public int getConnectionState(){
        return mConnectionState;
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
        BluetoothGattCharacteristic bgc = service.getCharacteristic(characteristic);

        if(bgc != null){
            setCharacteristicWriteType(bgc,BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        }

        return bgc;
    }

    private String getCharacteristicValue(BluetoothGattCharacteristic characteristic){
        final byte[] data = characteristic.getValue();
        int value = 0;
        float temperature = 0.0f;
        if (data != null && data.length > 0) {

            final StringBuilder stringBuilder = new StringBuilder();

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

    public int getFindMeStatus(){return getLED1ConnStatus();}



    public boolean isLED1Started(){
        return mLED1ConnStatus == SENSOR_CONN_ON;
    }
    public boolean isLED1StatusChanging(){return mLED1ConnStatus == SENSOR_CONN_IN_PROGRESS;}

    public void startLED1(boolean start){
        setLED1ConnStatus(SENSOR_CONN_IN_PROGRESS);
        mBlueToothThreadHandler.sendMessage(mBlueToothThreadHandler.obtainMessage(TYPE_START_LED1,start));
    }

    public void startLED1Internal(boolean start){
        if(isSupportLED1()){
            /*write enable*/
            byte[] values_write = start?(new byte[]{1}):(new byte[]{0});
            mLEDRangeCharacteristic.setValue(values_write);
            mBluetoothGatt.writeCharacteristic(mLEDRangeCharacteristic);

            moment(1000);
            mLEDEnableCharacteristic.setValue(values_write);
            mBluetoothGatt.writeCharacteristic(mLEDEnableCharacteristic);
            setLED1ConnStatus(start?SENSOR_CONN_ON:SENSOR_CONN_OFF);
        }else{
            Log.e(TAG,"Not support led.");
        }
    }

    public void setLED1ConnStatus(int led1ConnStatus) {
        mLED1ConnStatus = led1ConnStatus;
        notifyLED1Changed(led1ConnStatus);
    }
    public int getLED1ConnStatus(){return mLED1ConnStatus;}


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

    public void recordHeartBeat(){
        mLastHeartBeatRecord = mHeartBeat;
        mRecordManager.addRecordNow(RecordDatabase.RECORD_TYPE_HEART_RATE,mLastHeartBeatRecord);
    }

    public void recordTemprature(){

    }

    public int getLastRecord(){
        return mLastHeartBeatRecord;
    }

    public boolean isHearBeatStarted(){return mHeartRateConnStatus == SENSOR_CONN_ON;}
    public boolean isHearBeatStatusChanging(){return mHeartRateConnStatus == SENSOR_CONN_IN_PROGRESS;}

    public void setHeartRateConnStatus(int heartRateConnStatus) {
        mHeartRateConnStatus = heartRateConnStatus;
        notifyHeartRateSensorChanged(heartRateConnStatus);
    }

    public int getHeartRateConnStatus(){return mHeartRateConnStatus;}

    public void startHeartbeat(boolean start){
        setHeartRateConnStatus(SENSOR_CONN_IN_PROGRESS);
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
            setHeartRateConnStatus(start? SENSOR_CONN_ON:SENSOR_CONN_OFF);

        }
    }

    public boolean isStepStarted(){return mStepConnStatus == SENSOR_CONN_ON;}
    public boolean isStepStatusChanging(){return mStepConnStatus == SENSOR_CONN_IN_PROGRESS;}

    public void setStepConnStatus(int stepConnStatus) {
        mStepConnStatus = stepConnStatus;
        notifyStepsSensorChanged(stepConnStatus);
    }

    public int getStepConnStatus(){return mStepConnStatus;}

    public void startSteps(boolean start) {
        setStepConnStatus(SENSOR_CONN_IN_PROGRESS);
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
            moment();
            if(start) readCharacteristic(mStepsFeatureCharacteristic);
            moment();
            setCharacteristicNotification(mStepsFeatureCharacteristic,start);

            setStepConnStatus(start? SENSOR_CONN_ON:SENSOR_CONN_OFF);
        }
    }

    public boolean isTempStarted(){return mTemperatureConnStatus == SENSOR_CONN_ON;}
    public boolean isTempStatusChanging(){return mTemperatureConnStatus == SENSOR_CONN_IN_PROGRESS;}

    public void setTemperatureConnStatus(int temperatureConnStatus) {
        mTemperatureConnStatus = temperatureConnStatus;
        notifyTemperatureSensorChanged(temperatureConnStatus);
    }

    public int getTemperatureConnStatus(){return mTemperatureConnStatus;}

    public void startTemperature(boolean start){
        setTemperatureConnStatus(SENSOR_CONN_IN_PROGRESS);
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
            setTemperatureConnStatus(start? SENSOR_CONN_ON:SENSOR_CONN_OFF);
        }
    }

    private void moment(){

        try {
            Thread.sleep(500);
        }catch(Exception e){};

    }

    private void moment(int ms){

        try {
            Thread.sleep(ms);
        }catch(Exception e){};

    }

    public boolean isBatteryStarted(){return mBatteryConnStatus == SENSOR_CONN_ON;}
    public boolean isBatteryStatusChanging(){return mBatteryConnStatus == SENSOR_CONN_IN_PROGRESS;}

    public int getBatteryConnStatus(){return mBatteryConnStatus;}

    public void setBatteryConnStatus(int status){
        mBatteryConnStatus = status;
        notifyBatterySensorChanged(status);
    }

    public void startBattery(boolean start){
        setBatteryConnStatus(SENSOR_CONN_IN_PROGRESS);
        mBlueToothThreadHandler.sendMessage(mBlueToothThreadHandler.obtainMessage(TYPE_START_BATTERY,start));
    }
    private void startBatteryInternal(boolean start){
        if(isSupportBattary()){
            if(start)readCharacteristic(mBatteryValueCharacteristic);
            moment();
            setCharacteristicNotification(mBatteryValueCharacteristic,start);
            setBatteryConnStatus(start? SENSOR_CONN_ON:SENSOR_CONN_OFF);
        }
    }

    public String getModelName(){
        return mModelName;
    }

    public String getBluetoothDeviceAddress(){
        return mBluetoothDeviceAddress;
    }


    public int getHeartBeat(){
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

    public void setHeartBeat(int heartBeat){
        mHeartBeat = heartBeat;
        notifyHeartBeatChanged(heartBeat);
    }

    public int getDailySteps(){
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

    public int getStepFeature(){
        return mStepFeature;
    }

    public void setDailySteps(int steps){
        mSteps = steps;
        notifyStepsChanged(steps);
    }

    public void setStepFeature(int feature){
        mStepFeature = feature;
    }

    public float getTemperature(){
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

    public void setTemperature(float temperature){
        mTemperature = temperature;
        notifyTemperatureChanged(temperature);
    }

    public int getBatteryPercent(){
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

    public void setBatteryPercent(int batteryPercent){
        mBatteryPercent = batteryPercent;
        notifyBatteryChanged(batteryPercent);
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

    public static class Device{
        String _name;
        String _mac;
        public Device(String name, String mac){
            _name = name;
            _mac = mac;
        }
    }



}
