package com.yangyee.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * author: Yangxusong
 * created on: 2018/9/5 0005
 */
public class BleImpl implements IBle {
    private String TAG;

    private Context mContext;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private static final String UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";

    public BleImpl(Context mContext) {
        this.TAG = getClass().getSimpleName();
        this.mContext = mContext.getApplicationContext();
        if (isSupportBle()) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
    }


    @Override
    public boolean isSupportBle() {
        return mContext.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    @Override
    public boolean isBleEnable() {
        if (null != mBluetoothAdapter) {
            return mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    @Override
    public void enableBle() {
        if (null != mBluetoothAdapter) {
            mBluetoothAdapter.enable();
        }
        if (isSupportBle()) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (null != mBluetoothAdapter) {
                boolean enable=  mBluetoothAdapter.enable();
            }
        }
    }

    @Override
    public void disableBle() {
        if (null != mBluetoothAdapter) {
            mBluetoothAdapter.disable();
        }
    }

    @Override
    public boolean scan(final ScanCallback listener) {
        boolean success = false;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2 || null == mBluetoothManager) {
            Log.e(TAG, "android.os.Build.VERSION.SDK_INT is lower than 18  or not support Ble");
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(new NewBaseScanCallback(listener));
            Log.e(TAG, "bluetoothLeScanner.startScan");
            success = true;
        } else {
            success = mBluetoothAdapter.startLeScan(new BaseScanCallback(listener));
            Log.e(TAG, "bluetoothAdapter.startLeScan(callback)--");
        }
        return success;
    }


    @Override
    public void stopScan(ScanCallback listener) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2 || null == mBluetoothManager) {
            Log.e(TAG, "android.os.Build.VERSION.SDK_INT is lower than 18  or not support Ble");
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.stopScan(new NewBaseScanCallback(listener));
        } else {
            mBluetoothAdapter.stopLeScan(new BaseScanCallback(listener));
        }
    }

    @Override
    public void closeBluetoothGatt() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }

        if (mBluetoothGatt != null) {
            refreshDeviceCache(mBluetoothGatt);
        }

        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
    }



    public boolean refreshDeviceCache(BluetoothGatt mBluetoothGatt) {
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null) {
                final boolean success = (Boolean) refresh.invoke(mBluetoothGatt);
                Log.i(TAG, "refreshDeviceCache, is success:  " + success);
                return success;
            }
        } catch (Exception e) {
            Log.i(TAG, "exception occur while refreshing device: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public BluetoothGatt connect(BluetoothDevice device, BluetoothGattCallback mGattCallback) {
        mBluetoothGatt = device.connectGatt(mContext, true, mGattCallback);
        if (mBluetoothGatt == null) {
            Log.e(TAG, "Trying to create a new connection.---false");
        }
        return mBluetoothGatt;
    }
    @Override
    public boolean readAlertLevel(UUID service_uuid, UUID read_uuid) {
        boolean status = false;
        if (null == mBluetoothGatt) {
            return status;
        }
        for (BluetoothGattService bluetoothGattService : mBluetoothGatt.getServices()) {
            Log.e(TAG, "BluetoothGattService-writeLlsAlertLevel--mBluetoothGatt.getServices()--" + bluetoothGattService.getUuid());
        }
        BluetoothGattService linkLossService = mBluetoothGatt.getService(service_uuid);
        if (linkLossService == null) {
            Log.e(TAG, "mBluetoothGatt.getService(SERVIE_UUID)---link loss Alert service not found!");
            return status;
        }
        BluetoothGattCharacteristic alertLevel = null;
        alertLevel = linkLossService.getCharacteristic(read_uuid);
        if (alertLevel == null) {
            Log.e(TAG, "link loss Alert Level charateristic not found!");
            return status;
        }
        status = mBluetoothGatt.readCharacteristic(alertLevel);
        Log.e(TAG, "readLlsAlertLevel() - status=" + status);
        return status;
    }

    @Override
    public boolean writeAlertLevel(int iAlertLevel, UUID service_uuid, UUID write_uuid, byte[] bb) {
        boolean status = false;
        if (null == mBluetoothGatt) {
            return status;
        }
        for (BluetoothGattService bluetoothGattService : mBluetoothGatt.getServices()) {
            Log.e(TAG, "BluetoothGattService-writeLlsAlertLevel--mBluetoothGatt.getServices()--" + bluetoothGattService.getUuid());
        }
        BluetoothGattService linkLossService = mBluetoothGatt.getService(service_uuid);
        if (linkLossService == null) {
            Log.e(TAG, "mBluetoothGatt.getService(SERVIE_UUID)---link loss Alert service not found!");
            return status;
        }
        BluetoothGattCharacteristic alertLevel = null;
        alertLevel = linkLossService.getCharacteristic(write_uuid);
        if (alertLevel == null) {
            Log.e(TAG, "link loss Alert Level charateristic not found!");
            return status;
        }

        int storedLevel = alertLevel.getWriteType();
        Log.e(TAG, "storedLevel() - storedLevel=" + storedLevel);
        alertLevel.setValue(bb);
        Log.e("发送的指令", "bb" + bb[0]);
        alertLevel.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        status = mBluetoothGatt.writeCharacteristic(alertLevel);
        Log.e(TAG, "writeLlsAlertLevel() - status=" + status);
        return status;
    }

    @Override
    public boolean setNotify(UUID service_uuid, UUID read_uuid) {
        if (null == getBluetoothGatt()) {
            return false;
        }
        BluetoothGattService service = getBluetoothGatt().getService(service_uuid);
        if (service == null) {
            return false;
        }
        BluetoothGattCharacteristic  characteristic = service.getCharacteristic(read_uuid);
        if (null == characteristic) {
            return false;
        }
        int properties = characteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
            Log.e(TAG, "Check characteristic property: false");
            return false;
        }
        boolean success = getBluetoothGatt().setCharacteristicNotification(characteristic, true);
        Log.e(TAG, "setCharacteristicNotification: "+success);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR));
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
             getBluetoothGatt().writeDescriptor(descriptor);
        }
       return success;
    }

    @Override
    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }

    @Override
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
}


