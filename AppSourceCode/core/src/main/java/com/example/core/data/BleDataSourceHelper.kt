package com.example.core.data

import com.example.core.domain.BleDeviceInfo

class BleDataSourceHelper : IBleDataSourceHelper {

    override fun getSortedDevicesBySignalStrength(devices: List<BleDeviceInfo>): List<BleDeviceInfo> {
        return devices.sortedByDescending { scanResult -> scanResult.signalStrength }
    }
}