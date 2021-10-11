package com.example.blescan.framework.datasource

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.example.blescan.presentation.extensions.hasPermission
import javax.inject.Inject

/**
 * Provides configuration/settings required for BLE scanning.
 */
open class BleScanConfigProvider @Inject constructor(
    private val context: Context,
    private val scanSettings: ScanSettings,
    private val scanCallback: BleScanCallback
) {

    val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = context
            .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    val bleScanner: BluetoothLeScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    val isLocationPermissionGranted
        get() = context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    fun getScanSettings() = scanSettings

    fun getBleScanCallback() = scanCallback

}