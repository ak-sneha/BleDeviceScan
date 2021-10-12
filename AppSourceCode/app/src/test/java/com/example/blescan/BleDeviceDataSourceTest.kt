package com.example.blescan

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import com.example.blescan.presentation.extensions.hasPermission
import com.example.blescan.framework.datasource.BleDeviceDataSource
import com.example.blescan.framework.datasource.BleScanCallback
import com.example.blescan.framework.datasource.BleScanConfigProvider
import com.example.core.data.BleDataSourceHelper
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.lang.reflect.Field
import java.lang.reflect.Modifier


class BleDeviceDataSourceTest {

    private lateinit var context: Context
    private lateinit var manager: BluetoothManager
    private lateinit var adapter: BluetoothAdapter
    private lateinit var scanner: BluetoothLeScanner
    private lateinit var callback: BleScanCallback
    private lateinit var settings: ScanSettings
    private lateinit var helper: BleDataSourceHelper

    @Before
    fun setup() {
        mockkStatic("com.example.blescan.presentation.extensions.ContextExtensionKt")
        context = mock(Context::class.java)
        manager = mock(BluetoothManager::class.java)
        adapter = mock(BluetoothAdapter::class.java)
        scanner = mock(BluetoothLeScanner::class.java)
        callback = mock(BleScanCallback::class.java)
        settings = mock(ScanSettings::class.java)
        helper = mock(BleDataSourceHelper::class.java)
        `when`(context.getSystemService(Context.BLUETOOTH_SERVICE)).thenReturn(manager)

        `when`(manager.adapter).thenReturn(adapter)
        `when`(adapter.bluetoothLeScanner).thenReturn(scanner)
    }

    @Test
    fun testIsBluetoothEnabled() {

        val dataProvider = BleScanConfigProvider(context, helper, settings, callback)
        val bleDeviceDataSource = BleDeviceDataSource(dataProvider)

        `when`(dataProvider.bluetoothAdapter).thenReturn(adapter)

        `when`(adapter.isEnabled).thenReturn(true)
        var bluetoothDisabled = bleDeviceDataSource.isBluetoothDisabled()

        Assert.assertFalse(bluetoothDisabled)

        `when`(adapter.isEnabled).thenReturn(false)
        bluetoothDisabled = bleDeviceDataSource.isBluetoothDisabled()

        Assert.assertTrue(bluetoothDisabled)
    }

    @Test
    fun testIsLocationPermissionRequired() {

        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 23)
        mockkStatic("com.example.blescan.presentation.extensions.ContextExtensionKt")

        val dataProvider = BleScanConfigProvider(context, helper, settings, callback)
        val bleDeviceDataSource = BleDeviceDataSource(dataProvider)

        every {
            context.hasPermission("android.permission.ACCESS_FINE_LOCATION")
        } returns true
        var required = bleDeviceDataSource.isLocationPermissionRequired()

        Assert.assertFalse(required)

        every {
            context.hasPermission("android.permission.ACCESS_FINE_LOCATION")
        } returns false
        required = bleDeviceDataSource.isLocationPermissionRequired()

        Assert.assertTrue(required)

        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 0)
    }


    @Test
    fun testPerformBleScan() {
        val createMockObjectList = createMockObjectList()
        runBlocking {
            setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 23)

            val dataProvider = spy(BleScanConfigProvider(context, BleDataSourceHelper(), settings, callback))
            val bleDeviceDataSource = BleDeviceDataSource(dataProvider)

            bleDeviceDataSource.setScanTime(2000L)

            `when`(dataProvider.bluetoothAdapter).thenReturn(adapter)
            `when`(dataProvider.bleScanner).thenReturn(scanner)

            `when`(callback.getScanResults()).thenReturn(createMockObjectList)
            val response = bleDeviceDataSource.performBleScan()
            var number = 10
            var signalStrength = 0
            for (device in response) {
                Assert.assertEquals(device.name, "Device$number")
                Assert.assertEquals(device.address, "1A:2B:C3:D4:5E:Z$number")
                Assert.assertEquals(device.signalStrength, signalStrength)
                signalStrength -= 10
                number--
            }
        }
    }

    @Throws(Exception::class)
    fun setFinalStatic(field: Field, newValue: Any) {
        field.isAccessible = true

        val modifiersField = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

        field.set(null, newValue)
    }

    private fun createMockObjectList(): MutableList<ScanResult> {
        val devices = mutableListOf<ScanResult>()
        val signalStrength = -100
        for (number in 1..10) {

            val scaResult = mock(ScanResult::class.java)
            val device = mock(BluetoothDevice::class.java)
            `when`(device.name).thenReturn("Device$number")
            `when`(device.address).thenReturn("1A:2B:C3:D4:5E:Z$number")
            `when`(scaResult.rssi).thenReturn(signalStrength + (10 * number))
            `when`(scaResult.device).thenReturn(device)
            devices.add(scaResult)
        }
        return devices
    }
}