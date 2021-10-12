package com.example.core.data

import com.example.core.domain.IScanCallbacks

interface IDeviceRepository {

    suspend fun getBleDevices(status: IScanCallbacks)

    fun setScanTime(timeInMilliseconds: Long)

    fun stopScanning()
}