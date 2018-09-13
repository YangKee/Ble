package com.yangyee.ble;

import android.annotation.SuppressLint;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import java.util.List;

/**
 * author: Yangxusong
 * created on: 2018/9/5 0005
 */

@SuppressLint("NewApi")
public class NewBaseScanCallback extends ScanCallback {
    private com.yangyee.ble.ScanCallback mScanCallback;

    public NewBaseScanCallback(com.yangyee.ble.ScanCallback mScanCallback) {
        this.mScanCallback = mScanCallback;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        if (null!=mScanCallback){
            mScanCallback.onScanResult(callbackType,result);
        }
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);
        if (null!=mScanCallback){
            mScanCallback.onBatchScanResults(results);
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
        if (null!=mScanCallback){
            mScanCallback.onScanFailed(errorCode);
        }
    }
}
