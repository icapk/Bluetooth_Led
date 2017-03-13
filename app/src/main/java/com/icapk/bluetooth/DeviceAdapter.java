package com.icapk.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by songjian on 2017/3/3.
 */

public class DeviceAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> mDevices;
    private Context mContext;
    private static final String NUM_FOR_BLUETOOTH_TYPE_EARPHONE ="240404";
    private static final String NUM_FOR_BLUETOOTH_TYPE_PHONE ="5a020c";
//    private static final String NUM_FOR_BLUETOOTH_TYPE_EARPHONE ="240404";
    private ViewHolder holder;

    public DeviceAdapter(Context context, ArrayList<BluetoothDevice> devices){
        mDevices = devices;
        mContext = context;
    }


    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_item, null);
            holder = new ViewHolder();
            holder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mTvAddress = (TextView) convertView.findViewById(R.id.tv_address);
            holder.mImgType = (ImageView) convertView.findViewById(R.id.img_type);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        BluetoothDevice device = mDevices.get(position);
        holder.mTvName.setText(device.getName());
        holder.mTvAddress.setText(device.getAddress());

        System.out.println("设备号： "+device.getName()+device.getBluetoothClass().toString());

        if (device.getBluetoothClass().toString() != NUM_FOR_BLUETOOTH_TYPE_EARPHONE) {

            holder.mImgType.setBackgroundResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
        }
        if (device.getBluetoothClass().toString() == NUM_FOR_BLUETOOTH_TYPE_PHONE) {
//            System.out.println("设备号： "+device.getName()+device.getBluetoothClass().toString()+"----"+NUM_FOR_BLUETOOTH_TYPE_);
            holder.mImgType.setBackgroundResource(R.drawable.phone);
        }
        return convertView;
    }

    class ViewHolder{
        TextView mTvName;
        TextView mTvAddress;
        ImageView mImgType;
    }

}
