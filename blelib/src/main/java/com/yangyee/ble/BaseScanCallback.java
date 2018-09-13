package com.yangyee.ble;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * author: Yangxusong
 * created on: 2018/9/5 0005
 */
public class BaseScanCallback implements BluetoothAdapter.LeScanCallback{
    private ScanCallback mScanCallback;

    public BaseScanCallback(ScanCallback mScanCallback) {
        this.mScanCallback = mScanCallback;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (null!=mScanCallback){
            mScanCallback.onLeScan(device,rssi,scanRecord);
        }
    }
}
