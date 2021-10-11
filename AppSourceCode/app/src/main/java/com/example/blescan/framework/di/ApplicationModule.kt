package com.example.blescan.framework.di

import android.bluetooth.le.ScanSettings
import com.example.blescan.framework.BleDeviceRepository
import com.example.blescan.framework.BleScanApp
import com.example.blescan.framework.datasource.BleDeviceDataSource
import com.example.blescan.framework.datasource.BleScanCallback
import com.example.blescan.framework.datasource.BleScanConfigProvider
import com.example.core.data.IBleDeviceDataSource
import com.example.core.data.IDeviceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideDataRepository(dataSource: IBleDeviceDataSource): IDeviceRepository {
        return BleDeviceRepository(dataSource)
    }

    @Provides
    fun provideDataSource(scanConfigProvider: BleScanConfigProvider): IBleDeviceDataSource {
        return BleDeviceDataSource(scanConfigProvider)
    }

    @Provides
    fun provideDeviceHelper(
        scanSetting: ScanSettings,
        scanCallback: BleScanCallback
    ): BleScanConfigProvider {
        return BleScanConfigProvider(BleScanApp.applicationContext(), scanSetting, scanCallback)
    }

    @Provides
    fun provideScanCallback(): BleScanCallback {
        return BleScanCallback()
    }

    @Provides
    fun provideScanSettings(): ScanSettings {
        return ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
    }
}