package com.money.app.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast

/**
 * Giám sát Mạng (Network Monitor): Lắng nghe sự thay đổi trạng thái kết nối Internet của thiết bị.
 * Cảnh báo cho người dùng khi mất mạng để họ biết rằng dữ liệu sẽ không được đồng bộ lên Firebase lúc đó.
 */
class NetworkMonitor : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        
        if (!isConnected) {
            // Hiển thị thông báo khi mất kết nối
            Toast.makeText(context, "Mất kết nối Internet. PiggyBite đang ở chế độ ngoại tuyến.", Toast.LENGTH_LONG).show()
        }
    }
}
