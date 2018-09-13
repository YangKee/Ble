package com.yangyee.example;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Yangxusong
 * created on: 2018/9/7 0007
 */
public class DeviceAdapter extends BaseAdapter {
    private List<BluetoothDevice> mBluetoothDeviceList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public DeviceAdapter(Context mContext) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mBluetoothDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBluetoothDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null==convertView){
            convertView = mLayoutInflater.inflate(R.layout.item_device,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (null!=viewHolder){
            viewHolder.maneTv.setText("Name:"+mBluetoothDeviceList.get(position).getName());
            viewHolder.macTv.setText("MAC:"+mBluetoothDeviceList.get(position).getAddress());
        }
        return convertView;
    }

    private static class ViewHolder{
        View rootView;
        TextView maneTv;
        TextView macTv;

        public ViewHolder(View mRootView) {
            rootView = mRootView;
            maneTv = rootView.findViewById(R.id.tv_name);
            macTv = rootView.findViewById(R.id.tv_mac);
        }
    }


    public void addDevice(BluetoothDevice mDevice){
        if (!mBluetoothDeviceList.contains(mDevice)) {
            mBluetoothDeviceList.add(mDevice);
            notifyDataSetChanged();
        }
    }

    public void clearDevice(){
        mBluetoothDeviceList.clear();
        notifyDataSetChanged();
    }
}
