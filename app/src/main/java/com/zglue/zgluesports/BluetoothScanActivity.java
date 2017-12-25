package com.zglue.zgluesports;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zglue.zgluesports.bluetooth.BluetoothDataManager;
import com.zglue.zgluesports.bluetooth.ConnectionListener;

import java.util.ArrayList;
import java.util.List;

import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_DUAL;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_LE;


/**
 * Created by Micki on 2017/12/3.
 */

public class BluetoothScanActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,ConnectionListener{
    public final static String TAG = "BluetoothScanActivity";

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private BluetoothDataManager bleManager;

    private ListView mDeviceList;
    private TextView mPairedTitle;
    private View mPairedDevice;
    private TextView mPairedName;
    private TextView mPairedAddress;
    private Button mDiscBtn;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        mDeviceList = (ListView) findViewById(R.id.device_list);
        mDeviceList.setOnItemClickListener(this);
        mPairedTitle = (TextView) findViewById(R.id.paired_title);
        mPairedDevice = findViewById(R.id.paired_device);
        mPairedName = (TextView)findViewById(R.id.device_name);
        mPairedAddress = (TextView)findViewById(R.id.device_address);
        mDiscBtn = (Button)findViewById(R.id.disconnect_btn);

        getSupportActionBar().setTitle(R.string.title_scan_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Phone not support BLE feature", Toast.LENGTH_SHORT).show();
            finish();
        }

        requestPermission();

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,"Phone not support BLE feature", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bleManager = BluetoothDataManager.getInstance(this.getApplicationContext());


        mDiscBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScanning) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    //bleManager.stopScan(mScanCallback);
                    mScanning = false;
                    invalidateOptionsMenu();
                }

                bleManager.disconnect();
                //updatePairedDevice();
                //mLeDeviceListAdapter.clear();
                //scanLeDevice(true);

            }
        });
        updatePairedDevice();
    }

    private void updatePairedDevice(){
        if(bleManager.getConnectionState() == BluetoothDataManager.STATE_CONNECTED){
            mPairedTitle.setVisibility(View.VISIBLE);
            mPairedDevice.setVisibility(View.VISIBLE);
            mPairedName.setText(bleManager.getModelName());
            mPairedAddress.setText(bleManager.getBluetoothDeviceAddress());
        }else {
            mPairedTitle.setVisibility(View.GONE);
            mPairedDevice.setVisibility(View.GONE);
        }
    }




    private void requestPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_actionbar, menu);

        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_progressbar);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        //setListAdapter(mLeDeviceListAdapter);
        mDeviceList.setAdapter(mLeDeviceListAdapter);
        bleManager.addConnectionListener(this);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
        bleManager.removeConnectionListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG,"onListItemClick, on position:" + position);

        final DeviceEntity device = mLeDeviceListAdapter.getDevice(position);
        if (device == null)
            return;

        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            //bleManager.stopScan(mScanCallback);
            mScanning = false;
            invalidateOptionsMenu();
        }

        /*Returning false means there's device connected*/
        if(!bleManager.connect(device._device.getAddress())){
            Toast.makeText(this,"Device has been paired. Please disconnect it and then try again.",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onSupportNavigateUp(){
        if(!super.onSupportNavigateUp()){
            supportFinishAfterTransition();
        }
        return true;
    }

    @Override
    public void OnDeviceConnectStatusChanged(final BluetoothDevice device,final int state){
        Log.e(TAG,"OnDeviceConnectStatusChanged,state:" + state);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /* when connected, remove it from un-paired device. When disconnect, re-scan device*/
                if(state == BluetoothDataManager.STATE_DISCONNECTED) {
                    mLeDeviceListAdapter.clear();
                    scanLeDevice(true);
                }else if(state == BluetoothProfile.STATE_CONNECTED) {
                    mLeDeviceListAdapter.removeDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }

                for(int i = 0;i<mLeDeviceListAdapter.getCount();i++){
                    DeviceEntity entity = (DeviceEntity)mLeDeviceListAdapter.getItem(i);
                    if(device.getAddress().equals( entity._device.getAddress())){
                        entity._connectState = state;
                        mLeDeviceListAdapter.notifyDataSetChanged();

                    }
                }

                updatePairedDevice();
            }
        });
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    //bleManager.stopScan(mScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            //bleManager.scanBLEDevice(mScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            //bleManager.stopScan(mScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mLeDeviceListAdapter.addDevice(device);
                            //mLeDeviceListAdapter.notifyDataSetChanged();
                            if(device.getType() == DEVICE_TYPE_LE && device.getName() != null && device.getName().length()>0) {
                                mLeDeviceListAdapter.addDevice(device);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            };
    /*
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(result.getDevice().getType() == DEVICE_TYPE_LE) {
                        mLeDeviceListAdapter.addDevice(new DeviceEntity(result.getDevice(),BluetoothDataManager.STATE_DISCONNECTED));
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
*/
    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<DeviceEntity> mLeDevices;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<>();
        }

        public void addDevice(BluetoothDevice device) {
            if(!contains(device)) {
                mLeDevices.add(new DeviceEntity(device,BluetoothDataManager.STATE_DISCONNECTED));
            }


        }

        private boolean contains(BluetoothDevice device){
            for(DeviceEntity entity: mLeDevices){
                if(entity._device.equals(device)){
                    return true;
                }
            }
            return false;
        }

        public void removeDevice(BluetoothDevice device){
            for(DeviceEntity entity: mLeDevices){
                if(entity._device.equals(device)){
                    mLeDevices.remove(entity);
                }
            }
        }

        public DeviceEntity getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }


        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = LayoutInflater.from(BluetoothScanActivity.this).inflate(R.layout.scan_list, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                //viewHolder.connBtn = (Button)view.findViewById(R.id.connect_btn);
                viewHolder.deviceStatus = (TextView)view.findViewById(R.id.connect_status);
                viewHolder.bar = (ProgressBar)view.findViewById(R.id.connect_btn_progress_bar);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            DeviceEntity entity = mLeDevices.get(i);
            final String deviceName = entity._device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText(R.string.unknown_device);
            }
            viewHolder.deviceAddress.setText(entity._device.getAddress());

            switch (entity._connectState){
                case BluetoothDataManager.STATE_CONNECTING:
                    viewHolder.bar.setVisibility(View.VISIBLE);
                    viewHolder.deviceStatus.setVisibility(View.INVISIBLE);
                    break;
                case BluetoothDataManager.STATE_CONNECTED:
                    viewHolder.bar.setVisibility(View.INVISIBLE);
                    viewHolder.deviceStatus.setVisibility(View.VISIBLE);
                    viewHolder.deviceStatus.setText("online");
                    break;
                case BluetoothDataManager.STATE_DISCONNECTED:
                    viewHolder.bar.setVisibility(View.INVISIBLE);
                    viewHolder.deviceStatus.setVisibility(View.VISIBLE);
                    viewHolder.deviceStatus.setText("offline");
                    break;
                default:
                    break;
            }

            return view;
        }
    }

    public static class DeviceEntity{
        public BluetoothDevice _device;
        public int _connectState;
        public DeviceEntity(BluetoothDevice device, int connectState){
            _device = device;
            _connectState = connectState;
        }
    }



    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        //Button connBtn;
        TextView deviceStatus;
        ProgressBar bar;
    }
}
