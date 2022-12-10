package com.sjty.emsbledemo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sjty.blelibrary.base.ydstrong.callback.OnReceivedDataHolder;
import com.sjty.blelibrary.server.BleCallbackHelper;
import com.sjty.blelibrary.utils.StringHexUtils;

/**
 * @Author
 * @Time
 * @Description
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected BleCallbackHelper mCallbackHelper = new BleCallbackHelper() {
        @Override
        public void scanDeviceCallBack(BluetoothDevice device, int rssi, byte[] advertising) {
            super.scanDeviceCallBack(device, rssi, advertising);
            //查询结果回调
            //通过该接口回到需要数据同步的activity或者fragment（可以不使用该接口，自己去定义）
            OnReceivedDataHolder.getInstance().onScanSuccessfully(device);
        }

        @Override
        public void connectedSuccessCallBack(BluetoothGatt gatt) {
            super.connectedSuccessCallBack(gatt);
            //通过该接口回到需要数据同步的activity或者fragment（可以不使用该接口，自己去定义）
            OnReceivedDataHolder.getInstance().onConnected(gatt);
        }

        @Override
        public void disConnectedCallBack(BluetoothGatt gatt) {
            super.disConnectedCallBack(gatt);
            //通过该接口回到需要数据同步的activity或者fragment（可以不使用该接口，自己去定义）
            OnReceivedDataHolder.getInstance().onDisConnected(gatt);
        }

        @Override
        public void onError(BluetoothGatt gatt) {
            super.onError(gatt);
            //连接错误回调
        }

        @Override
        public void noDiscoverServer(BluetoothGatt gatt) {
            super.noDiscoverServer(gatt);
        }

        @Override
        public void onRestartError(BluetoothGatt gatt) {
            super.onRestartError(gatt);
            //重连失败回调
        }

        @Override
        public void notifyValueCallBack(String notifiCharacteristicUUID, BluetoothGatt gatt, byte[] order) {
            super.notifyValueCallBack(notifiCharacteristicUUID, gatt, order);
            //将字节数组转换为16进制字符串(StringHexUtils转换工具类)
            String data = StringHexUtils.Bytes2HexString(order);
            //通过该接口回到需要数据同步的activity或者fragment（可以不使用该接口，自己去定义）
            OnReceivedDataHolder.getInstance().notifyUpdate(gatt,data);
        }
    };
}
