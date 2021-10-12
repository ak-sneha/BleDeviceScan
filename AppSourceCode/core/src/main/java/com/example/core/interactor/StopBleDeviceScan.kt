package com.example.core.interactor

import com.example.core.data.IDeviceRepository

class StopBleDeviceScan {

    fun stop(repository: IDeviceRepository) {
        repository.stopScanning()
    }
}