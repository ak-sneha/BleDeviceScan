package com.example.core.domain

interface IScanCallbacks {

    fun scanStatus(status: Boolean)

    fun enableBluetooth()

    fun requiredLocationPermission()

    fun scanCompleted(result: List<BleDeviceInfo>)

}