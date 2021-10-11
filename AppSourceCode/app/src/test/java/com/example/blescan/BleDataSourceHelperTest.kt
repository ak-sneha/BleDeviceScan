package com.example.blescan

import com.example.core.data.BleDataSourceHelper
import com.example.core.domain.BleDeviceInfo
import org.junit.Assert
import org.junit.Test

class BleDataSourceHelperTest {

    @Test
    fun testGetSortedDevicesBySignalStrength() {

        val bleDevices = createMockObjectList()
        val helper = BleDataSourceHelper()
        val sortedDevices = helper.getSortedDevicesBySignalStrength(bleDevices)

        var number = 10
        var signalStrength = 0
        for (device in sortedDevices) {
            Assert.assertEquals(device.name, "Device$number")
            Assert.assertEquals(device.address, "1A:2B:C3:D4:5E:Z$number")
            Assert.assertEquals(device.signalStrength, signalStrength)
            signalStrength -= 10
            number--
        }
    }

    @Test
    fun testSortDeviceEmptyInput() {
        val helper = BleDataSourceHelper()
        val sortedDevices = helper.getSortedDevicesBySignalStrength(mutableListOf())
        Assert.assertEquals(0, sortedDevices.size)
    }

    @Test
    fun testSortDeviceDuplicateInput() {
        val devices = mutableListOf<BleDeviceInfo>()
        val signal = intArrayOf(-50, -50, -40)
        devices.add(BleDeviceInfo("Device1", "1A:2B:C3:D4:5E:Z1", signalStrength = signal[0]))
        devices.add(BleDeviceInfo("Device2", "1A:2B:C3:D4:5E:Z2", signal[1]))
        devices.add(BleDeviceInfo("Device3", "1A:2B:C3:D4:5E:Z3", signal[2]))

        val helper = BleDataSourceHelper()
        val sortedDevices = helper.getSortedDevicesBySignalStrength(devices)
        Assert.assertEquals(signal[2], sortedDevices[0].signalStrength)
        Assert.assertEquals("Device3", sortedDevices[0].name)

        Assert.assertEquals(signal[1], sortedDevices[1].signalStrength)
        Assert.assertEquals("Device1", sortedDevices[1].name)

        Assert.assertEquals(signal[0], sortedDevices[2].signalStrength)
        Assert.assertEquals("Device2", sortedDevices[2].name)
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
}