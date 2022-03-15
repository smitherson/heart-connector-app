package com.example.heart_rate_connectivity

import android.app.Application

class ApplicationHR : Application() {
    var data: String? = null
    public val bluetoothManager = BluetoothManager()
}