package com.yangyee.example;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private BluetoothService mBluetoothService;
    private Button mScanBtn;
    private ListView mDevicelist;

    ProgressBar mProgressBar;
    private TextView mMsgTV;
    private DeviceAdapter mDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mScanBtn = findViewById(R.id.btn_scan);
        mProgressBar = findViewById(R.id.pro);
        mMsgTV = findViewById(R.id.tv_msg);
        mScanBtn.setOnClickListener(this);
        mDevicelist = findViewById(R.id.list);


        mDeviceAdapter = new DeviceAdapter(mContext);
        mDevicelist.setAdapter(mDeviceAdapter);
        checkSelfPermission();
    }

    private void bindService() {
        Intent bindIntent = new Intent(mContext, BluetoothService.class);
        this.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            mBluetoothService.setBleCallback(mCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };


    private BluetoothService.Callback mCallback = new BluetoothService.Callback() {
        @Override
        public void onStartScan() {
            showLoading("SCANNING");
        }

        @Override
        public void onLeScan(BluetoothDevice mDevice) {
            mDeviceAdapter.addDevice(mDevice);
        }

        @Override
        public void onScanComplete() {
            hideLoading();
            mScanBtn.setText("SCAN");
        }

        @Override
        public void onConnecting() {
            showLoading("CONNECTING");
        }

        @Override
        public void onConnected() {
            hideLoading();
        }

        @Override
        public void onConnectFail() {
            hideLoading();
        }

        @Override
        public void onDisConnected() {

        }

        @Override
        public void onServicesDiscovered() {

        }

        @Override
        public void onGetData(byte[] data) {
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                Log.e(TAG, "data===========" + new String(data) + "\n" + stringBuilder.toString());
            }
        }
    };


    private final int MY_PERMISSIONS_REQUEST_LOCATION = 8;
    private void checkSelfPermission() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M
            &&(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            bindService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (null != grantResults && grantResults.length > 1) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        bindService();
                    } else {

                    }
                    return;
                }
            }
            default:
                break;
        }
    }


    private void startScan() {
        mDeviceAdapter.clearDevice();
        if (null != mBluetoothService) {
            mBluetoothService.startScan();
        }
    }

    private void stopScan() {
        if (null != mBluetoothService) {
            mBluetoothService.stopScan();
        }
    }


    @Override
    public void onClick(View v) {
        if (mScanBtn.getText().toString().equals("STOP")) {
            stopScan();
            mScanBtn.setText("SCAN");
        } else {
            startScan();
            mScanBtn.setText("STOP");
        }
    }


    public void showLoading(String string) {
        mMsgTV.setText(string);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mMsgTV.setText("");
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public static  boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkProvider || gpsProvider) {
            return true;
        }
        return false;
    }


    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2;

    private void setLocationService() {
        try {
            Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
        }catch (Exception mE){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (isLocationEnable(this)) {
                //定位已打开的处理
            } else {
                //定位依然没有打开的处理
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
