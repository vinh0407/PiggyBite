package com.piggybite.app.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

/**
 * Android không cho phép ứng dụng tự cấp quyền "Truy cập thông báo".
 * Helper này kiểm tra trạng thái quyền và mở thẳng tới công tắc của PiggyBite
 * (Android 11+), hoặc danh sách quyền truy cập thông báo trên Android cũ hơn.
 */
object NotificationAccessHelper {

    fun isGranted(context: Context): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(context)
            .contains(context.packageName)
    }

    fun openSettings(context: Context) {
        val component = ComponentName(context, BankNotificationListenerService::class.java)
        val intents = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                add(
                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS).putExtra(
                        Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                        component.flattenToString()
                    )
                )
            }
            add(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            add(Intent(Settings.ACTION_SETTINGS))
        }
        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            } catch (_: Exception) {
                // thử intent tiếp theo
            }
        }
    }
}
