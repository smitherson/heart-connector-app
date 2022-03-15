package com.example.heart_rate_connectivity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT16
import android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class DevicesManageActivity : AppCompatActivity() {

    val TAG = "DevicesManageActivity"
    public var devicesList: ArrayList<String> = arrayListOf<String>()
    public var bluetoothListAdapter: ArrayAdapter<String>? = null

    var mBluetoothAdapter: BluetoothAdapter? = null

    val appPermissionsList = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_CONNECT
    )

    ///////
    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    fun startScan(context: Context): Boolean {

        if (mBluetoothAdapter?.isDiscovering == true) {
            mBluetoothAdapter?.cancelDiscovery()
        }

        return (mBluetoothAdapter?.startDiscovery() == true)
    }


    fun stopScan(context: Context) {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)

        // LocalBroadcastManager.getInstance(context).unregisterReceiver(mDeviceScanReceiver)
        if (mBluetoothAdapter?.isDiscovering == true) {
            mBluetoothAdapter?.cancelDiscovery()
        }
    }

    private val mDeviceScanReceiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            override fun onReceive(context: Context, intent: Intent) {

                val action = intent.action

                //Device found
                if (BluetoothDevice.ACTION_FOUND == action) {
                    // Get the BluetoothDevice object from the Intent
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    // Add the name and address to an array adapter to show in a list
                    devicesList.add(
                        """
                    ${device?.address}
                    ${device!!.name}
                    """.trimIndent()
                    )
                    bluetoothListAdapter?.notifyDataSetChanged()

                }
            }

        }


    // }


    ////////


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_manage_activity)
        registerReceiver(mDeviceScanReceiver, filter)

        val hasPermissions = checkAppPermissions()


        val app = this.application as ApplicationHR
        mBluetoothAdapter = app.bluetoothManager.getAdapter()

        bluetoothListAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, devicesList
        )

        val deviceListView = findViewById(R.id.devicesListView) as ListView

        deviceListView.adapter = bluetoothListAdapter

        deviceListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position)
                val mac = selectedItem.toString().subSequence(0, 17).toString()
                Toast.makeText(this@DevicesManageActivity, "Selected : $mac", Toast.LENGTH_SHORT)
                    .show()
                //connect(mac)

                app.bluetoothManager.selectedDevicesList.add(mac)
            }


        val scanButton = findViewById(R.id.scan_button) as Button
        scanButton.setOnClickListener {

            val app = this.application as ApplicationHR

            if (startScan(app.applicationContext) == true) {
                Toast.makeText(this@DevicesManageActivity, "startScan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@DevicesManageActivity, "failed startScan", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    //permisssion


    private fun checkAppPermissions(): Boolean {

        var hasPermissions = true
        var code = -1
        for (permission in this.appPermissionsList) {
            code = code + 1

            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestLocationPermission(permission, code)
                hasPermissions = false
                Toast.makeText(
                    this@DevicesManageActivity,
                    "Lacking permission!".plus(permission),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return hasPermissions
    }

    private fun requestLocationPermission(permission: String, code: Int) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                permission,
            ), code
        )
    }


    override fun onDestroy() {
        super.onDestroy()

    }


}