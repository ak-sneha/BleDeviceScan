package com.example.blescan.framework.datasource

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import timber.log.Timber

/**
 * Implementation of BLE Device scan callback.
 */
open class BleScanCallback : ScanCallback() {

    private var scanResults = mutableListOf<ScanResult>()

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
        if (indexQuery != -1) { // A scan result already exists with the same address
            scanResults[indexQuery] = result
        } else {
            with(result.device) {
                Timber.i("Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
            }
            scanResults.add(result)
        }
    }

    override fun onScanFailed(errorCode: Int) {
        Timber.e("onScanFailed: code $errorCode")
    }

    open fun getScanResults(): MutableList<ScanResult> {
        return scanResults
    }
}