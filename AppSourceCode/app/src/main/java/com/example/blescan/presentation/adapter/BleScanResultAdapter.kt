package com.example.blescan.presentation.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blescan.R
import com.example.blescan.databinding.RowScanResultBinding
import com.example.core.domain.BleDeviceInfo
import org.jetbrains.anko.layoutInflater

class BleScanResultAdapter(
    private val items: List<BleDeviceInfo>,
) : RecyclerView.Adapter<BleScanResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.context.layoutInflater.inflate(
            R.layout.row_scan_result,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    class ViewHolder(
        view: View,
    ) : RecyclerView.ViewHolder(view) {

        private val binding = RowScanResultBinding.bind(view)

        @SuppressLint("SetTextI18n")
        fun bind(result: BleDeviceInfo) {
            with(binding) {
                deviceName.text = result.name ?: "Unnamed"
                macAddress.text = result.address
                signalStrength.text = "${result.signalStrength} dBm"
            }
        }
    }

}