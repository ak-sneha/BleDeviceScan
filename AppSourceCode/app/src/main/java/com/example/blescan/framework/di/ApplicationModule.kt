package com.example.blescan.framework.di

import android.bluetooth.le.ScanSettings
import com.example.blescan.framework.BleDeviceRepository
import com.example.blescan.framework.BleScanApp
import com.example.blescan.framework.datasource.BleDeviceDataSource
import com.example.blescan.framework.datasource.BleScanCallback
import com.example.blescan.framework.datasource.BleScanConfigProvider
import com.example.core.data.BleDataSourceHelper
import com.example.core.data.IBleDataSourceHelper
import com.example.core.data.IBleDeviceDataSource
import com.example.core.data.IDeviceRepository
import com.example.core.interactor.StartBleDeviceScan
import com.example.core.interactor.StopBleDeviceScan
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
        scanCallback: BleScanCallback,
        helper: IBleDataSourceHelper
    ): BleScanConfigProvider {
        return BleScanConfigProvider(
            BleScanApp.applicationContext(),
            helper,
            scanSetting,
            scanCallback
        )
    }

    @Provides
    fun provideScanCallback(): BleScanCallback {
        return BleScanCallback()
    }

    @Provides
    fun provideScanSettings(): ScanSettings {
        return ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
    }

    @Provides
    fun provideStartBleDeviceScan(): StartBleDeviceScan {
        return StartBleDeviceScan()
    }

    @Provides
    fun provideStopBleDeviceScan(): StopBleDeviceScan {
        return StopBleDeviceScan()
    }

    @Provides
    fun provideBleDataSourceHelper(): IBleDataSourceHelper {
        return BleDataSourceHelper()
    }
}