package com.money.app.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth

class LogoutService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        
        // Logout from Firebase
        FirebaseAuth.getInstance().signOut()
        
        // Clear local data
        val db = com.money.app.data.AppDatabase.getDatabase(this)
        kotlinx.coroutines.runBlocking {
            db.transactionDao().clearAll()
            db.fundDao().clearAll()
        }

        // Clear local preferences
        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()

        // Stop the service
        stopSelf()
    }
}
