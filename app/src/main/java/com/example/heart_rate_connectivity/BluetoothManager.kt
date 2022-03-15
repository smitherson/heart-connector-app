package com.example.heart_rate_connectivity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.ArrayAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class BluetoothManager() {
    val TAG = "BluetoothManager"
    public var selectedDevicesList: ArrayList<String> = arrayListOf<String>()

    private val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    fun getAdapter(): BluetoothAdapter? {
        return mBluetoothAdapter
    }
}