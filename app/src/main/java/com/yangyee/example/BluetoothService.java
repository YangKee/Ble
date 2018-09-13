

package com.yangyee.example;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.yangyee.ble.BleCallback;
import com.yangyee.ble.BleManager;
import com.yangyee.ble.IBle;
import com.yangyee.ble.Interceptor.NameInterceptor;

import java.util.UUID;

/**
 * author: Yangxusong
 * created on: 2018/9/5 0005
 */
public class BluetoothService extends Service {
    private String TAG = "BluetoothService";

    public BluetoothBinder mBinder = new BluetoothBinder();
    private BleManager bleManager;
    private Callback mCallback;


    @Override
    public void onCreate() {
        bleManager = new BleManager(getApplicationContext());
        bleManager.enableBle();
        bleManager.setBleCallback(mBleCallback);
        acquireWakeLock();
        startForeground(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bleManager = null;
        releaseWakeLock();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bleManager.closeBluetoothGatt();
        return super.onUnbind(intent);
    }

    public class BluetoothBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    public void setBleCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }


    public void startScan() {
        if (null != bleManager) {
            bleManager.enableBle();
        }
//        bleManager.scan();
        bleManager.scanAndConnect(8 * 1000, new NameInterceptor(DEVICE_NAME, true));
    }

    public void stopScan() {
        if (null != bleManager) {
            bleManager.stopScan();
        }
    }

    public static interface Callback {

        void onStartScan();

        void onLeScan(BluetoothDevice mDevice);

        void onScanComplete();

        void onConnecting();

        void onConnected();

        void onConnectFail();

        void onDisConnected();

        void onServicesDiscovered();

        void onGetData(byte[] data);

    }


    private int mState;
    private BleCallback mBleCallback = new BleCallback() {
        @Override
        public void onScanResult(BluetoothDevice device) {
            if (null != mCallback) {
                mCallback.onLeScan(device);
            }
        }

        @Override
        public void getData(byte[]  data) {
            if (null != mCallback) {
                mCallback.onGetData(data);
            }
        }

        @Override
        public void onStateChange(int state) {
            if (null == mCallback) {
                mState = state;
                return;
            } else {
                if (state == IBle.STATE_IDLE) {
                    if (mState == IBle.STATE_SCANNING) {
                        mCallback.onScanComplete();
                    }
                } else if (state == IBle.STATE_SCANNING) {
                    mCallback.onStartScan();
                } else if (state == IBle.STATE_CONNECTING) {
                    mCallback.onConnecting();
                } else if (state == IBle.STATE_CONNECTED) {
                    mCallback.onConnected();
                } else if (state == IBle.STATE_DISCONNECTED) {
                    if (mState == IBle.STATE_CONNECTING) {
                        mCallback.onConnectFail();
                    } else {
                        mCallback.onDisConnected();
                    }
                } else if (state == IBle.STATE_SERVICES_DISCOVERED) {
                    mCallback.onServicesDiscovered();
                    if (setNotify()) {
                        senHandle.removeCallbacks(sendRunnable);
                        senHandle.postDelayed(sendRunnable, 0);
                    }
                } else {

                }
                mState = state;
            }
        }
    };


    String DEVICE_NAME = "Jin Ge  ";


    public static final UUID SERVIE_UUID = UUID
            .fromString("8a0dffd0-b80c-4335-8e5f-630031415354");
    public static final UUID RED_LIGHT_CONTROL_UUID = UUID
            .fromString("8a0dffd1-b80c-4335-8e5f-630031415354");
    public static final UUID RED_LIGHT_CONTROL_UUID_TWO = UUID
            .fromString("8a0dffd2-b80c-4335-8e5f-630031415354");


    byte[] bb = {(byte) 0xFC, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0xED};
    private Handler senHandle = new Handler();
    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            writeLlsAlertLevel(1, bb);
            senHandle.postDelayed(sendRunnable, 10000);
        }
    };

    public void writeLlsAlertLevel(int iAlertLevel, byte[] bb) {
        bleManager.writeLlsAlertLevel(iAlertLevel, SERVIE_UUID, RED_LIGHT_CONTROL_UUID, bb);
    }

    public boolean setNotify() {
        return bleManager.notify(SERVIE_UUID, RED_LIGHT_CONTROL_UUID_TWO);
    }


    // 申请电源锁，禁止休眠
    private PowerManager.WakeLock mWakeLock = null;

    private void acquireWakeLock() {
        if (null == mWakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            if (null != mWakeLock) {
                mWakeLock.acquire();
                Log.e(TAG, "mWakeLock---");
            } else {
                Log.e(TAG, "mWakeLock---is--null");
            }
        }
    }

    // 释放设备电源锁
    private void releaseWakeLock() {
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }


    Notification notification = null;

    /**
     * 设置前台
     *
     * @param context
     */
    public void startForeground(Service context) {

        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText("")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.mipmap.icon)
                .setAutoCancel(true);

        Intent resultIntent = new Intent(App.getInstance().getApplicationContext(), MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            PendingIntent pIntent = PendingIntent.getActivity(App.getInstance().getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pIntent);
        }
        notification = builder.build();
        context.startForeground(8888, notification);
    }
}
