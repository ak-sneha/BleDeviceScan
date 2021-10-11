package com.example.blescan.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blescan.framework.BleDeviceRepository
import com.example.core.domain.BleDeviceInfo
import com.example.core.domain.IScanCallbacks
import com.example.core.interactor.StartBleDeviceScan
import com.example.core.interactor.StopBleDeviceScan
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BleScanVM @Inject constructor(private val repository: BleDeviceRepository) : ViewModel() {

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
        StartBleDeviceScan().get(repository, object : IScanCallbacks {
            override fun scanStatus(status: Boolean) {
                isScanning = status
                scanProgress.postValue(isScanning)
            }

            override fun enableBluetooth() {
                enableBle.postValue(true)
            }

            override fun requiredLocationPermission() {
                requestLocPermission.postValue(true)
            }

            override fun scanCompleted(result: List<BleDeviceInfo>) {
                bleDevices.clear()
                bleDevices.addAll(result)
                devicesUpdate.postValue(bleDevices)
            }

        })
    }

    fun stopBleScan() {
        StopBleDeviceScan().stop(repository)
        isScanning = false
        scanProgress.postValue(isScanning)
    }
}