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
import com.sjty.emsbledemo.databinding.ActivitySingleBinding;
import com.sjty.emsbledemo.entity.DeviceInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SingleActivity extends BaseActivity implements OnReceivedData {

    private static final String TAG = "SingleActivity";
    private ActivitySingleBinding mSingleBinding;
    private List<DeviceInfo> mDeviceInfoList = new ArrayList<>();
    private DeviceListAdapter mDeviceListAdapter;
    private Suit mSuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSingleBinding = ActivitySingleBinding.inflate(getLayoutInflater());
        setContentView(mSingleBinding.getRoot());
        //注册蓝牙回调
        BleManager.getInstance(App.getInstance()).registerCallback(mCallbackHelper);
        //注册回调接口
        OnReceivedDataHolder.getInstance().register(this);
        initView();
    }

    private void initView() {

        mDeviceListAdapter = new DeviceListAdapter(this, mDeviceInfoList, new DeviceListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                DeviceInfo deviceInfo = mDeviceInfoList.get(position);
                BleManager.getInstance(App.getInstance()).stopScan();
                BleManager.getInstance(App.getInstance()).closeAll();
                mSingleBinding.tvSend.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BleManager.getInstance(App.getInstance()).connectDevice(deviceInfo.getMac());
                    }
                }, 200);
            }

            @Override
            public void onDisConnect(int position) {
                DeviceInfo deviceInfo = mDeviceInfoList.get(position);
                BleManager.getInstance(App.getInstance()).stopScan();
                BleManager.getInstance(App.getInstance()).close(deviceInfo.getMac());
            }
        });
        mSingleBinding.rcvDeviceList.setLayoutManager(new LinearLayoutManager(this));
        mSingleBinding.rcvDeviceList.setAdapter(mDeviceListAdapter);

        //发送指令
        mSingleBinding.tvSend.setOnClickListener(view -> {
            sendDataToDevice();
        });
    }

    //发送指令方法
    private void sendDataToDevice() {
        //从DeviceConnectedBus中获取已连接的所有设备
        Collection<BaseDevice> allConnectDevice = DeviceConnectedBus.getInstance(App.getInstance()).getAllConnectDevice();
        //判断设备是否存在连接设备
        if (allConnectDevice != null && allConnectDevice.size() > 0) {
            for (BaseDevice device : allConnectDevice) {
                SuitDevice suitDevice = (SuitDevice) device;
                if (mSuit != null) {
                    //发送参数到设备
                    suitDevice.sendParameterToDevice(mSuit);
                    //发送部位档位到设备
                    suitDevice.sendWorkLevelToDevice(mSuit.mPartList,null);
                    //发送工作时间，工作间隔等到设备
                    suitDevice.sendTimeToDevice(mSuit.dischargeTime,mSuit.intervalTime,mSuit.bufferTime);
                    //发送工作开始
                    suitDevice.sendStartToDevice();
                }
            }
        }
    }

    //发送工作结束
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
        //查询设备
        BleManager.getInstance(App.getInstance()).scanDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //发送工作结束
        sendStopToDevice();
        //断开所有设备
        BleManager.getInstance(App.getInstance()).closeAll();
        //取消接口回调注册
        OnReceivedDataHolder.getInstance().unregister(this);
        //取消蓝牙回调注册
        BleManager.getInstance(App.getInstance()).unRegisterCallback(mCallbackHelper);
    }

    @Override
    public void onReceivedData(BluetoothGatt gatt, String data) {
        //TODO 通知回调
        Log.e(TAG, "===onReceivedData: " + data);
    }

    @Override
    public void onReadData(BluetoothGatt gatt, String data) {
        //TODO
    }

    @Override
    public void onDisConnection(BluetoothGatt gatt) {
        //TODO 连接断开
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
        //TODO 查询成功
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
        //TODO 连接成功
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
            DeviceConnectedBus.getInstance(App.getInstance()).getDevice(device.getAddress());
        }
        mDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(BluetoothGatt gatt) {
        //TODO 连接失败
    }

    /**
     * 初始化设备参数
     * @param gatt
     */
    private void initData(BluetoothGatt gatt) {
        BluetoothDevice device = gatt.getDevice();
        mSuit = new Suit(device.getName(),device.getAddress());
        //初始化部位
        mSuit.initPartList();
        for (Suit.Part part : mSuit.mPartList) {
            part.level = 0;
        }
        mSuit.mPartList.get(0).level = 1;
        //初始化频率
        mSuit.frequency = 15;
        //初始化脉宽
        mSuit.pulse = 250;
        //初始化基波
        mSuit.fundamentalWave = 1;
        //初始化载波
        mSuit.carrierWave = 1;
        //初始化放电时间
        mSuit.dischargeTime = 60;
        //初始化间隔时间
        mSuit.intervalTime = 2;
        //初始化缓冲时间
        mSuit.bufferTime = 1.5f;
    }
}