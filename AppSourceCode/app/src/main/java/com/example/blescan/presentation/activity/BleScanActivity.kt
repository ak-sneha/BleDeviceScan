package com.example.blescan.presentation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.blescan.R
import com.example.blescan.presentation.extensions.requestPermission
import com.example.blescan.presentation.viewmodel.BleScanVM
import com.example.blescan.databinding.ActivityMainBinding
import com.example.blescan.presentation.adapter.BleScanResultAdapter
import com.example.core.domain.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import timber.log.Timber

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2

@AndroidEntryPoint
class BleScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var bleDevices = mutableListOf<BleDeviceInfo>()

    private var isScanning = false
        set(value) {
            field = value
            runOnUiThread {
                binding.scanButton.text =
                    if (value) getString(R.string.stop_scan) else getString(R.string.start_scan)
            }
        }

    private val scanResultAdapter: BleScanResultAdapter by lazy {
        BleScanResultAdapter(bleDevices)
    }

    private val enableBluetooth = Observer<Boolean> {
        promptEnableBluetooth()
    }

    private val requestLocationPermission = Observer<Boolean> {
        requestLocationPermission()
    }

    private val showScanProgress = Observer<Boolean> {
        it.let {
            isScanning = it
            binding.progressBar.visibility = if (isScanning) View.VISIBLE else View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private val bleDevicesUpdate = Observer<List<BleDeviceInfo>> {
        it.let {
            bleDevices.clear()
            bleDevices.addAll(it)
            scanResultAdapter.notifyDataSetChanged()
        }
    }

    private val bleVM: BleScanVM by viewModels { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupVmObservers()
        setupScanButton()
        setupRecyclerView()
    }

    private fun setupScanButton() {
        binding.scanButton.setOnClickListener { if (isScanning) stopBleScan() else startBleScan() }
    }

    private fun setupVmObservers() {
        bleVM.enableBluetooth.observe(this, enableBluetooth)
        bleVM.requestLocationPermission.observe(this, requestLocationPermission)
        bleVM.showScanProgress.observe(this, showScanProgress)
        bleVM.bleDevicesUpdate.observe(this, bleDevicesUpdate)
    }

    private fun setupRecyclerView() {
        binding.scanResultsRecyclerView.apply {
            adapter = scanResultAdapter
            layoutManager = LinearLayoutManager(
                this@BleScanActivity,
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }

        val animator = binding.scanResultsRecyclerView.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    private fun stopBleScan() {
        bleVM.stopBleScan()
    }

    private fun startBleScan() {
        lifecycleScope.launch(Dispatchers.Default) {
            bleVM.getBleDevices()
        }
    }

    private fun promptEnableBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityIfNeeded(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
    }

    private fun requestLocationPermission() {
        runOnUiThread {
            alert {
                title = getString(R.string.location_permission_title)
                message = getString(R.string.location_permission_content)
                isCancelable = false
                positiveButton(android.R.string.ok) {
                    requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }.show()
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    if (checkUserRequestedDontAskAgain()) {
                        requestLocationPermission()
                    }
                } else {
                    startBleScan()
                }
            }
        }
    }

    private fun checkUserRequestedDontAskAgain(): Boolean {
        var showPermissionRational = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showPermissionRational =
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
            Timber.d("User requested don't ask again $showPermissionRational")
            if (!showPermissionRational) {
                showPermissionDeniedDialog()
            }
        }
        return showPermissionRational
    }

    private fun showPermissionDeniedDialog() {
        alert {
            title = getString(R.string.permission_denied_title)
            message = getString(R.string.permission_denied_content)
            isCancelable = false
            positiveButton(android.R.string.ok) {
                navigateToPermissionScreen()
            }
        }.show()
    }

    private fun navigateToPermissionScreen() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}