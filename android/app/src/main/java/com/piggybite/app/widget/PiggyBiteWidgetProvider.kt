package com.piggybite.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.piggybite.app.MainActivity
import com.piggybite.app.R
import com.piggybite.app.data.DataRepository
import com.piggybite.app.util.Formatters

class PiggyBiteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val repository = DataRepository.getInstance(context)
        val totalBalance = repository.accounts.value.sumOf { it.balance }
        val todayExpense = repository.getTodayExpenses()

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_piggybite)

            views.setTextViewText(R.id.tv_widget_balance, Formatters.formatMoney(totalBalance))
            views.setTextViewText(R.id.tv_widget_today_expense, "Chi hôm nay: −${Formatters.formatMoney(todayExpense)}")

            // Intent mở app thông thường
            val appIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val appPendingIntent = PendingIntent.getActivity(
                context, 0, appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, appPendingIntent)

            // Intent mở nhanh Quét VietQR
            val scanIntent = Intent(context, MainActivity::class.java).apply {
                action = ACTION_SCAN_QR
                putExtra("action", "scan_qr")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val scanPendingIntent = PendingIntent.getActivity(
                context, 1, scanIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_widget_scan_qr, scanPendingIntent)

            // Intent mở nhanh QR của tôi
            val myQrIntent = Intent(context, MainActivity::class.java).apply {
                action = ACTION_MY_QR
                putExtra("action", "my_qr")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val myQrPendingIntent = PendingIntent.getActivity(
                context, 2, myQrIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_widget_my_qr, myQrPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    companion object {
        const val ACTION_SCAN_QR = "com.piggybite.app.ACTION_SCAN_QR"
        const val ACTION_MY_QR = "com.piggybite.app.ACTION_MY_QR"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidget = ComponentName(context, PiggyBiteWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            if (appWidgetIds.isNotEmpty()) {
                val intent = Intent(context, PiggyBiteWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }
}
