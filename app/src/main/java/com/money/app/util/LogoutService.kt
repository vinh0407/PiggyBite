package com.money.app.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth

/**
 * Dịch vụ Đăng xuất (Logout Service): Đảm bảo an toàn dữ liệu khi ứng dụng bị đóng hoàn toàn.
 * Chức năng: Khi người dùng vuốt bỏ ứng dụng khỏi danh sách đa nhiệm (onTaskRemoved), 
 * dịch vụ này sẽ tự động đăng xuất và xóa dữ liệu tạm trên máy nếu cần thiết (Tùy cấu hình bảo mật).
 */
class LogoutService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    /**
     * Được gọi khi ứng dụng bị người dùng đóng (Kill task)
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        
        // Tùy chọn: Tự động đăng xuất Firebase khi ứng dụng bị đóng hoàn toàn
        // FirebaseAuth.getInstance().signOut()
        
        // Tùy chọn: Xóa sạch dữ liệu cục bộ để bảo mật tuyệt đối
        // val db = com.money.app.data.AppDatabase.getDatabase(this)
        // kotlinx.coroutines.runBlocking {
        //     db.transactionDao().clearAll()
        //     db.fundDao().clearAll()
        // }

        stopSelf()
    }
}
