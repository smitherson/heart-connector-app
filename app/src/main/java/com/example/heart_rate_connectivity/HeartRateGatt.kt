package com.example.heart_rate_connectivity

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.util.Log
import android.widget.Toast

class HeartRateGatt() {
    val TAG = "HeartRateGatt"
    var mGattHR: BluetoothGatt? = null
    public var hrMeasurments: ArrayList<Number> = arrayListOf<Number>()
    public var rrMeasurments: ArrayList<Number> = arrayListOf<Number>()
    //public var domainLabels: ArrayList<Number> = arrayListOf<Number>()
    public var name = ""
    public
    constructor(address: String, context: Context, adapter: BluetoothAdapter?) : this() {
        val device = adapter?.getRemoteDevice(address)
        mGattHR = device?.connectGatt(context, false, bluetoothGattCallback)
        Log.w(TAG, "HeartRateGatt $address")
        name = address.toString().subSequence(0, 5).toString()

    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {


        //private val bluetoothGattCallback = object : () {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                Log.w(TAG, "BluetoothProfile.STATE_CONNECTED")
                gatt?.discoverServices()

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                Log.w(TAG, "BluetoothProfile.STATE_DISCONNECTED")

            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "  broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED) ")
                mGattHR = gatt

                //gatt!!.setCharacteristicNotification(gatt!!.services[1].characteristics[0], true) // unknown
                gatt!!.setCharacteristicNotification(
                    gatt!!.services[2].characteristics[0],
                    true
                ) //0000180d-0000-1000-8000-00805f9b34fb Heart Rate Service

                //if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
                //val characteristic = gatt!!.services[3].characteristics[0]
                val descriptor: BluetoothGattDescriptor =
                    gatt!!.services[2].characteristics[0].descriptors[0]

                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
                //}
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            //broadcastUpdate(BluetoothService.ACTION_DATA_AVAILABLE, characteristic)
            Log.w(TAG, "onCharacteristicRead: ".plus(characteristic.getIntValue(0, 0)))
        }

        fun ByteArray.toHexString() =
            asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }

        fun getBit(value: Int, position: Int): Int {
            return (value shr position) and 1;
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            //super.onCharacteristicChanged(gatt, characteristic)
            //broadcastUpdate(BluetoothService.ACTION_DATA_AVAILABLE, characteristic)

            val conf = characteristic?.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0) ?: 0

            val hrFormat = getBit(conf, 0);
            val sensorContact1 = getBit(conf, 1);
            val sensorContact2 = getBit(conf, 2);
            val caloriesPresent = getBit(conf, 3);
            val rrPresent = getBit(conf, 4);
            val bit5 = getBit(conf, 5);
            val bit6 = getBit(conf, 5);
            val bit7 = getBit(conf, 5);

            val heartRate = characteristic?.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1)

            val rr1 = characteristic?.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 2)
            val rr2 = characteristic?.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 4)
            val rr3 = characteristic?.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 6)
            //val rr4 = characteristic?.getIntValue(FORMAT_UINT16, 8)

            hrMeasurments.add(heartRate ?: 0)
            rrMeasurments.add(rr1?: 0)
            //domainLabels.add(domainLabels.size)

            Log.w(
                TAG,
                "onCharacteristicChanged: hr:$heartRate format:$hrFormat cont:$sensorContact1$sensorContact2 calories:$caloriesPresent rrPres:$rrPresent $bit5$bit6$bit7 rr1:$rr1 rr2:$rr2 rr3:$rr3"
            )
        }
    }
}