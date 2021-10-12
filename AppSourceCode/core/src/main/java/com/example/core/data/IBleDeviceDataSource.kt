package com.example.core.data

import com.example.core.domain.BleDeviceInfo

interface IBleDeviceDataSource {

    fun isBluetoothDisabled(): Boolean

    fun isLocationPermissionRequired(): Boolean

    fun setScanTime(timeInMilliseconds: Long)

    suspend fun performBleScan(): List<BleDeviceInfo>

    fun stopBleScan()
}