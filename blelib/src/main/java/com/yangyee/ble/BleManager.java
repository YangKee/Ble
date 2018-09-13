package com.yangyee.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yangyee.ble.Interceptor.ScanInterceptor;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.ContentValues.TAG;


/**
 * author: Yangxusong
 * created on: 2018/9/5 0005
 */
public class BleManager {
    private Context mContext;
    private IBle mBle;
    private int mBleState = IBle.STATE_IDLE;
    private BleCallback mBleCallback;
    private ScanInterceptor mScanInterceptor;
    private static final long TIME_OUT = 10 * 1000;
    private Handler mHandler = new Handler();
    private AtomicBoolean foundDevice = new AtomicBoolean(false);

    public BleManager(Context context) {
        this.mContext = context.getApplicationContext();
        this.mBle = new BleImpl(context);
    }

    public void enableBle() {
        this.mBle.enableBle();
    }

    public void closeBluetoothGatt() {
        this.mBle.closeBluetoothGatt();
    }

    public void scan() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, TIME_OUT);
        this.mScanInterceptor = null;
        if (this.mBle.scan(mScanCallback)) {
            updateState(IBle.STATE_SCANNING);
        }
    }

    /**
     * 扫描设备自动连接
     *
     * @param timeout          超时时间，扫描不到自动停止
     * @param mScanInterceptor 扫描到的设备判断是否符合条件
     */
    public void scanAndConnect(long timeout, ScanInterceptor mScanInterceptor) {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, timeout);
        this.foundDevice.set(false);
        this.mScanInterceptor = mScanInterceptor;
        if (this.mBle.scan(mScanCallback)) {
            updateState(IBle.STATE_SCANNING);
        }
    }

    public void stopScan() {
        mHandler.removeCallbacks(mRunnable);
        this.mBle.stopScan(mScanCallback);
        if (getBleState() == IBle.STATE_SCANNING) {
            updateState(IBle.STATE_IDLE);
        }
    }

    public void connect(BluetoothDevice mDevice) {
        Log.e(TAG, "connect-");
        updateState(IBle.STATE_CONNECTING);
        this.mBle.connect(mDevice, mGattCallback);
    }

    public void disconnect() {
        this.mBle.disconnect();
    }

    public void readLlsAlertLevel(UUID mServiceUuid, UUID mRedLightControlUuid) {
        this.mBle.readAlertLevel(mServiceUuid, mRedLightControlUuid);
    }

    public void writeLlsAlertLevel(int mIAlertLevel, UUID mServiceUuid, UUID mRedLightControlUuid, byte[] mBb) {
        this.mBle.writeAlertLevel(mIAlertLevel, mServiceUuid, mRedLightControlUuid, mBb);
    }

    public boolean notify(UUID mServiceUuid, UUID mRedLightControlUuid) {
        return this.mBle.setNotify(mServiceUuid, mRedLightControlUuid);
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                gatt.discoverServices();
                updateState(IBle.STATE_CONNECTED);
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                updateState(IBle.STATE_DISCONNECTED);
            } else if (newState == BluetoothGatt.STATE_CONNECTING) {
                updateState(IBle.STATE_CONNECTING);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            updateState(IBle.STATE_SERVICES_DISCOVERED);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //进行接收数据的相关操作
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                if (null != mBleCallback) {
                    (mBleCallback).getData(data);
                }

            }

        }
    };


    public int getBleState() {
        return mBleState;
    }

    public void setBleCallback(BleCallback mBleCallback) {
        this.mBleCallback = mBleCallback;
    }

    public void updateState(int state) {
        mBleState = state;
        if (null != mBleCallback) {
            mBleCallback.onStateChange(state);
        }

        Log.e(TAG, "updateState--" + state);
    }


    private ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                device = result.getDevice();
                if (device == null) {
                    return;
                }
                if (null != mBleCallback) {
                    mBleCallback.onScanResult(device);
                }
                if (foundDevice.get()) {
                    return;
                }
                if (null != mScanInterceptor && mScanInterceptor.match(device)) {
                    foundDevice.set(true);
                    mBle.stopScan(this);
                    connect(device);
                }
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            updateState(IBle.STATE_IDLE);
        }

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            super.onLeScan(device, rssi, scanRecord);
            if (null != mBleCallback) {
                mBleCallback.onScanResult(device);
            }
        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };
}
