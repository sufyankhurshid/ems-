package com.sjty.emsbledemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sjty.emsbledemo.R;
import com.sjty.emsbledemo.entity.DeviceInfo;

import java.util.List;

/**
 * @Author
 * @Time
 * @Description
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private Context mContext;
    private List<DeviceInfo> mDeviceInfoList;
    private OnItemClickListener mItemClickListener;

    public DeviceListAdapter(Context context, List<DeviceInfo> deviceInfoList, OnItemClickListener itemClickListener) {
        mContext = context;
        mDeviceInfoList = deviceInfoList;
        mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_device, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(view1 -> {
            if (mItemClickListener != null) {
                int position = viewHolder.getAdapterPosition();
                if (position >= 0) {
                    mItemClickListener.onItemClick(position);
                }
            }
        });
        viewHolder.mTvConnState.setOnClickListener(view1 -> {
            if (mItemClickListener != null) {
                int position = viewHolder.getAdapterPosition();
                if (position >= 0) {
                    mItemClickListener.onDisConnect(position);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceInfo deviceInfo = mDeviceInfoList.get(position);
        holder.mTvDeviceName.setText(deviceInfo.getName());
        if (deviceInfo.isConn()) {
            holder.mTvConnState.setVisibility(View.VISIBLE);
        } else {
            holder.mTvConnState.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (mDeviceInfoList != null) {
            return mDeviceInfoList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTvDeviceName,mTvConnState;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvDeviceName = itemView.findViewById(R.id.tv_device_name);
            mTvConnState = itemView.findViewById(R.id.tv_conn_state);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDisConnect(int position);
    }
}
