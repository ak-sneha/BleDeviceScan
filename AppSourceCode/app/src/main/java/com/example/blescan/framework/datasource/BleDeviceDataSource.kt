package com.example.blescan.framework.datasource

import android.os.Build
import com.example.blescan.framework.datasource.helper.BleDataSourceHelper
import com.example.blescan.framework.util.Mapper
import com.example.core.data.IBleDeviceDataSource
import com.example.core.domain.BleDeviceInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

/**
 * Performs BLE device scan. Provides Scan start/stop functionality.
 */
class BleDeviceDataSource @Inject constructor(private val scanConfigProvider: BleScanConfigProvider) :
    IBleDeviceDataSource {

    private var scanTime = 10000L

    override suspend fun performBleScan(): List<BleDeviceInfo> {

        withTimeoutOrNull(scanTime) {
            startScan()
            delay(scanTime)
        }
        stopBleScan()
        val scanResults = scanConfigProvider.getBleScanCallback().getScanResults()
        return BleDataSourceHelper().getSortedDevicesBySignalStrength(scanResults.map {
            Mapper().getBleDeviceInfo(it)
        })
    }

    override fun setScanTime(timeInMilliseconds: Long) {
        scanTime = timeInMilliseconds
    }

    override fun isBluetoothDisabled(): Boolean {
        return !scanConfigProvider.bluetoothAdapter.isEnabled
    }

    override fun isLocationPermissionRequired(): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !scanConfigProvider.isLocationPermissionGranted)
    }

    private fun startScan() {
        scanConfigProvider.bleScanner.startScan(
            null,
            scanConfigProvider.getScanSettings(),
            scanConfigProvider.getBleScanCallback()
        )
    }

    override fun stopBleScan() {
        scanConfigProvider.bleScanner.stopScan(scanConfigProvider.getBleScanCallback())
    }

}