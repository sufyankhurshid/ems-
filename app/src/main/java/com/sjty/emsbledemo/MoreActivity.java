package com.sjty.emsbledemo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.sjty.blelibrary.BleManager;
import com.sjty.blelibrary.DeviceConnectedBus;
import com.sjty.blelibrary.base.impl.BaseDevice;
import com.sjty.blelibrary.base.interfaces.NotificationFilsh;
import com.sjty.blelibrary.base.ydstrong.callback.OnReceivedData;
import com.sjty.blelibrary.base.ydstrong.callback.OnReceivedDataHolder;
import com.sjty.blelibrary.base.ydstrong.impl.SuitDevice;
import com.sjty.blelibrary.base.ydstrong.model.Suit;
import com.sjty.emsbledemo.adapter.DeviceListAdapter;
import com.sjty.emsbledemo.databinding.ActivityMoreBinding;
import com.sjty.emsbledemo.entity.DeviceInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MoreActivity extends BaseActivity implements OnReceivedData {

    private static final String TAG = "MoreActivity";
    private ActivityMoreBinding mMoreBinding;
    private List<DeviceInfo> mDeviceInfoList = new ArrayList<>();
    private DeviceListAdapter mDeviceListAdapter;
    private Suit mSuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMoreBinding = ActivityMoreBinding.inflate(getLayoutInflater());
        setContentView(mMoreBinding.getRoot());

        BleManager.getInstance(App.getInstance()).registerCallback(mCallbackHelper);
        OnReceivedDataHolder.getInstance().register(this);
        initView();
    }

    private void initView() {
        mDeviceListAdapter = new DeviceListAdapter(this, mDeviceInfoList, new DeviceListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                DeviceInfo deviceInfo = mDeviceInfoList.get(position);
                BleManager.getInstance(App.getInstance()).stopScan();
                BleManager.getInstance(App.getInstance()).connectDevice(deviceInfo.getMac());
            }

            @Override
            public void onDisConnect(int position) {
                DeviceInfo deviceInfo = mDeviceInfoList.get(position);
                BleManager.getInstance(App.getInstance()).stopScan();
                BleManager.getInstance(App.getInstance()).close(deviceInfo.getMac());
            }
        });
        mMoreBinding.rcvDeviceList.setLayoutManager(new LinearLayoutManager(this));
        mMoreBinding.rcvDeviceList.setAdapter(mDeviceListAdapter);

        mMoreBinding.tvSend.setOnClickListener(view -> {
            sendDataToDevice();
        });
    }

    private void sendDataToDevice() {
        Collection<BaseDevice> allConnectDevice = DeviceConnectedBus.getInstance(App.getInstance()).getAllConnectDevice();
        if (allConnectDevice != null && allConnectDevice.size() > 0) {
            for (BaseDevice device : allConnectDevice) {
                SuitDevice suitDevice = (SuitDevice) device;
                if (mSuit != null) {
                    suitDevice.sendParameterToDevice(mSuit);
                    suitDevice.sendWorkLevelToDevice(mSuit.mPartList,null);
                    suitDevice.sendTimeToDevice(mSuit.dischargeTime,mSuit.intervalTime,mSuit.bufferTime);
                    suitDevice.sendStartToDevice();
                }
            }
        }

    }

    private void sendStopToDevice() {
        Collection<BaseDevice> allConnectDevice = DeviceConnectedBus.getInstance(App.getInstance()).getAllConnectDevice();
        if (allConnectDevice != null && allConnectDevice.size() > 0) {
            for (BaseDevice device : allConnectDevice) {
                SuitDevice suitDevice = (SuitDevice) device;
                suitDevice.sendStopToDevice();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        BleManager.getInstance(App.getInstance()).scanDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendStopToDevice();
        BleManager.getInstance(App.getInstance()).closeAll();
        OnReceivedDataHolder.getInstance().unregister(this);
        BleManager.getInstance(App.getInstance()).unRegisterCallback(mCallbackHelper);
    }

    @Override
    public void onReceivedData(BluetoothGatt gatt, String data) {
        Log.e(TAG, "===onReceivedData: " + data);
    }

    @Override
    public void onReadData(BluetoothGatt gatt, String data) {

    }

    @Override
    public void onDisConnection(BluetoothGatt gatt) {
        BluetoothDevice device = gatt.getDevice();
        Log.e(TAG, "===onDisConnection:name: " + device.getName() + " ===mac: " + device.getAddress());
        for (DeviceInfo deviceInfo : mDeviceInfoList) {
            if (device.getAddress().equals(deviceInfo.getMac())) {
                deviceInfo.setConn(false);
            }
        }
        DeviceConnectedBus.getInstance(App.getInstance()).removeDevice(device.getAddress());
        mDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScanSuccessfully(BluetoothDevice device) {
        Log.e(TAG, "===onScanSuccessfully:name: " + device.getName() + " ===mac: " + device.getAddress());
        if (device.getName().contains("YDSC")) {
            if (mDeviceInfoList.size() <= 0) {
                mDeviceInfoList.add(new DeviceInfo(device.getName(),device.getAddress()));
                mDeviceListAdapter.notifyDataSetChanged();
            } else {
                boolean isExist = false;
                for (DeviceInfo deviceInfo : mDeviceInfoList) {
                    if (device.getAddress().equals(deviceInfo.getMac())) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    mDeviceInfoList.add(new DeviceInfo(device.getName(),device.getAddress()));
                    mDeviceListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onConnected(BluetoothGatt gatt) {
        BluetoothDevice device = gatt.getDevice();
        Log.e(TAG, "===onConnected:name: " + device.getName() + " ===mac: " + device.getAddress());
        for (DeviceInfo deviceInfo : mDeviceInfoList) {
            if (device.getAddress().equals(deviceInfo.getMac())) {
                deviceInfo.setConn(true);
            }
        }
        SuitDevice suitDevice;
        BaseDevice baseDevice = DeviceConnectedBus.getInstance(App.getInstance()).getDevice(gatt.getDevice().getAddress());
        if(baseDevice != null){
            suitDevice = (SuitDevice)baseDevice;
            suitDevice.setBluetoothGatt(gatt);
            suitDevice.setNotification(true);
            suitDevice.setNotificationFilsh(new NotificationFilsh() {
                @Override
                public void notificationFilsh(String uuid) {
                }
            });
            if (mSuit == null) {
                initData(gatt);
            }
        }else {
            suitDevice = new SuitDevice(App.getInstance(), gatt);
            suitDevice.setNotification(true);
            suitDevice.setNotificationFilsh(new NotificationFilsh() {
                @Override
                public void notificationFilsh(String uuid) {
                }
            });
            if (mSuit == null) {
                initData(gatt);
            }
            DeviceConnectedBus.getInstance(App.getInstance()).addDevice(suitDevice);
        }
        mDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(BluetoothGatt gatt) {

    }

    private void initData(BluetoothGatt gatt) {
        BluetoothDevice device = gatt.getDevice();
        mSuit = new Suit(device.getName(),device.getAddress());
        mSuit.initPartList();
        for (Suit.Part part : mSuit.mPartList) {
            part.level = 1;
        }
        mSuit.frequency = 15;
        mSuit.pulse = 250;
        mSuit.fundamentalWave = 1;
        mSuit.carrierWave = 1;
        mSuit.dischargeTime = 30;
        mSuit.intervalTime = 10;
        mSuit.bufferTime = 1.5f;
    }
}