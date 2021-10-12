package com.example.blescan.framework

import com.example.core.data.IBleDeviceDataSource
import com.example.core.data.IDeviceRepository
import com.example.core.domain.IScanCallbacks
import javax.inject.Inject

/**
 * Repository for BLE Device Scan.
 */
class BleDeviceRepository @Inject constructor(val dataSource: IBleDeviceDataSource) :
    IDeviceRepository {

    override suspend fun getBleDevices(status: IScanCallbacks) {

        if (dataSource.isBluetoothDisabled()) {
            status.enableBluetooth()
            return
        }

        if (dataSource.isLocationPermissionRequired()) {
            status.requiredLocationPermission()
            return
        }

        status.scanStatus(true)
        val devices = dataSource.performBleScan()
        status.scanStatus(false)
        status.scanCompleted(devices)
    }

    override fun setScanTime(timeInMilliseconds: Long) {
        dataSource.setScanTime(timeInMilliseconds)
    }

    override fun stopScanning() {
        dataSource.stopBleScan()
    }

}