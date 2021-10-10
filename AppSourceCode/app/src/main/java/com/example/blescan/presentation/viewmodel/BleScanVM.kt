package com.example.blescan.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.core.domain.BleDeviceInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

class BleScanVM : ViewModel() {

    private var bleDevices = mutableListOf<BleDeviceInfo>()

    private var isScanning = false

    private var enableBle = MutableLiveData<Boolean>()
    val enableBluetooth: LiveData<Boolean> get() = enableBle

    private var requestLocPermission = MutableLiveData<Boolean>()
    val requestLocationPermission: LiveData<Boolean> get() = requestLocPermission

    private var scanProgress = MutableLiveData<Boolean>()
    val showScanProgress: LiveData<Boolean> get() = scanProgress

    private var devicesUpdate = MutableLiveData<List<BleDeviceInfo>>()
    val bleDevicesUpdate: LiveData<List<BleDeviceInfo>> get() = devicesUpdate

    suspend fun getBleDevices() {
        withTimeout(10000) {
            bleDevices.addAll(createMockObjectList())
            delay(10000)
        }
        devicesUpdate.postValue(bleDevices)
    }

    private fun createMockObjectList(): List<BleDeviceInfo> {
        val devices = mutableListOf<BleDeviceInfo>()
        val signalStrength = -100
        for (number in 1..10) {

            val bleDeviceInfo = BleDeviceInfo(
                "Device$number",
                "1A:2B:C3:D4:5E:Z$number",
                signalStrength + (10 * number)
            )
            devices.add(bleDeviceInfo)
        }
        return devices
    }

    fun stopBleScan() {

    }
}