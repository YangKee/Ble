package com.yangyee.ble.Interceptor;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

/**
 * author: Yangxusong
 * created on: 2018/9/7 0007
 */
public class NameInterceptor implements ScanInterceptor {
    boolean isFuzzy =false;
    String mName =null;

    public NameInterceptor(String name,boolean mIsFuzzy) {
        mName = name;
        isFuzzy = mIsFuzzy;
    }

    @Override
    public boolean match(BluetoothDevice mDevice) {
        if (null==mName||null==mDevice.getName()){
            return false;
        }else{
            boolean b =  isFuzzy?mName.contains(mDevice.getName()):mName.equalsIgnoreCase(mDevice.getName());
            if (b){
                Log.e("NameInterceptor",String.format("mDevice--%s----%s",mDevice.getName(),mDevice.getAddress()));
            }
            return b;
        }

    }
}
