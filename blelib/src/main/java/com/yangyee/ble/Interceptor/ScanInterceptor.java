package com.yangyee.ble.Interceptor;

import android.bluetooth.BluetoothDevice;

/**
 * author: Yangxusong
 * created on: 2018/9/7 0007
 */
public interface ScanInterceptor {
    boolean match(BluetoothDevice mDevice);
}
