package com.example.blescan.framework.util

import android.bluetooth.le.ScanResult
import com.example.core.domain.BleDeviceInfo

class Mapper {
    fun getBleDeviceInfo(scanResult: ScanResult) =
        BleDeviceInfo(scanResult.device.name, scanResult.device.address, scanResult.rssi)
}