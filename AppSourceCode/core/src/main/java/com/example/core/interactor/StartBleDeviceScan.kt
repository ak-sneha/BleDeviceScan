package com.example.core.interactor

import com.example.core.data.IDeviceRepository
import com.example.core.domain.IScanCallbacks

class StartBleDeviceScan {

    suspend fun get(repository: IDeviceRepository, status: IScanCallbacks) {
        repository.getBleDevices(status)
    }
}