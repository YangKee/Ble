package com.yangyee.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;

import java.util.List;

/**
 * author: Yangxusong
 * created on: 2018/9/5 0005
 */
public abstract class ScanCallback {

    private BleCallback mBleCallback;

    public void setBleCallback(BleCallback mBleCallback) {
        this.mBleCallback = mBleCallback;
    }

    /**
     * 21以上的回调
     * Callback when a BLE advertisement has been found.
     *
     * @param callbackType Determines how this callback was triggered. Could be one of
     *                     {@link ScanSettings#CALLBACK_TYPE_ALL_MATCHES},
     *                     {@link ScanSettings#CALLBACK_TYPE_FIRST_MATCH} or
     *                     {@link ScanSettings#CALLBACK_TYPE_MATCH_LOST}
     * @param result       A Bluetooth LE scan result.
     */
    public void onScanResult(int callbackType, ScanResult result) {
    }

    /**
     * 21以上的回调
     * Callback when batch results are delivered.
     *
     * @param results List of scan results that are previously scanned.
     */
    public void onBatchScanResults(List<ScanResult> results) {
    }

    /**
     * 21以上的回调
     * Callback when scan could not be started.
     *
     * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
     */
    public void onScanFailed(int errorCode) {
    }

    /**
     * 21以下的回调
     * <p>
     * Callback interface used to deliver LE scan results.
     *
     * @param device
     * @param rssi
     * @param scanRecord
     */
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
    }
}
