package com.example.blescan.framework.datasource.helper

import com.example.core.data.IBleDataSourceHelper
import com.example.core.domain.BleDeviceInfo

/**
 * Helper for BLE device data source.
 */
class BleDataSourceHelper : IBleDataSourceHelper {

    override fun getSortedDevicesBySignalStrength(devices: List<BleDeviceInfo>): List<BleDeviceInfo> {
        return devices.sortedByDescending { scanResult -> scanResult.signalStrength }
    }
}