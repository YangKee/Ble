package com.yangyee.ble.Interceptor;

import android.bluetooth.BluetoothDevice;

/**
 * author: Yangxusong
 * created on: 2018/9/7 0007
 */
public class AddrInterceptor implements ScanInterceptor {
    String mMac;

    public AddrInterceptor(String mac) {
        mMac = mac;
    }

    @Override
    public boolean match(BluetoothDevice device) {
        if (null==mMac){
            return false;
        }else{
            return mMac.equalsIgnoreCase(device.getAddress());
        }

    }
}
