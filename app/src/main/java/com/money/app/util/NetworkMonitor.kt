package com.money.app.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast

class NetworkMonitor : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        
        if (!isConnected) {
            Toast.makeText(context, "Mất kết nối Internet. PiggyBite đang ở chế độ ngoại tuyến.", Toast.LENGTH_LONG).show()
        } else {
            // Optional: notify when back online
            // Toast.makeText(context, "Đã kết nối lại Internet. Dữ liệu sẽ được đồng bộ.", Toast.LENGTH_SHORT).show()
        }
    }
}
