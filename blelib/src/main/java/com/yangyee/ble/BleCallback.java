package com.yangyee.ble;

import android.bluetooth.BluetoothDevice;

/**
 * author: Yangxusong
 * created on: 2018/9/6 0006
 */
public interface BleCallback {

    /**
     * 扫描单个结果返回
     *
     * @param device
     */
    void onScanResult(BluetoothDevice device);


    /**
     * 数据返回
     *
     * @param data
     */
    void getData(byte[]  data);

    /**
     * Ble状态回调
     *
     * @param state
     */
    void onStateChange(@IBle.State int state);
}
