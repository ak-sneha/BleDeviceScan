package com.example.core.data

import com.example.core.domain.BleDeviceInfo

interface IBleDataSourceHelper {

    fun getSortedDevicesBySignalStrength(devices: List<BleDeviceInfo>): List<BleDeviceInfo>
}