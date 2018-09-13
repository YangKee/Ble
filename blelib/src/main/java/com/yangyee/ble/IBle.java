package com.yangyee.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;

/**
 * author: Yangxusong
 * created on: 2018/9/5 0005
 */
public interface IBle {


    /**
     * @hide
     */
    @IntDef({STATE_IDLE, STATE_DISCONNECTED, STATE_SCANNING, STATE_CONNECTING, STATE_CONNECTED, STATE_SERVICES_DISCOVERED})
    @Retention(RetentionPolicy.SOURCE)
    @interface State {
    }

    int STATE_IDLE = 0x100;
    int STATE_DISCONNECTED = 0x101;
    int STATE_SCANNING = 0x102;
    int STATE_CONNECTING = 0x103;
    int STATE_CONNECTED = 0x104;
    int STATE_SERVICES_DISCOVERED = 0x105;


    /**
     * 是否支持BLE
     *
     * @return
     */
    boolean isSupportBle();

    /**
     * BLE是否打开
     *
     * @return
     */
    boolean isBleEnable();

    /**
     * 打开BLE
     */
    void enableBle();

    /**
     * 关闭BLE
     */
    void disableBle();


    /**
     * 扫描设备
     *
     * @param listener
     */
    boolean scan(ScanCallback listener);

    /**
     * 停止扫描制备
     *
     * @param listener
     */
    void stopScan(ScanCallback listener);


    /**
     * 连接设备
     *
     * @param device
     * @return
     */

    BluetoothGatt connect(BluetoothDevice device,BluetoothGattCallback mGattCallback);


    /**
     *
     */
    void closeBluetoothGatt();


    /**
     * 写
     */
    boolean readAlertLevel( UUID service_uuid, UUID read_uuid);
    /**
     * 写
     */
    boolean writeAlertLevel(int iAlertLevel, UUID service_uuid, UUID write_uuid, byte[] bb);


    /**
     * 设置通知
     * @param service_uuid
     * @param read_uuid
     */
    boolean setNotify(UUID service_uuid, UUID read_uuid);

    /**
     *
     */
    BluetoothGatt getBluetoothGatt();

    /**
     * 断开连接
     */
    void disconnect();

}
