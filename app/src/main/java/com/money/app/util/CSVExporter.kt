package com.money.app.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.money.app.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Xuất dữ liệu tài chính (CSV Exporter):
 * Cho phép người dùng kết xuất toàn bộ giao dịch thành tệp .csv và chia sẻ qua Share Sheet.
 */
class CSVExporter(private val context: Context) {
    private val appDb = AppDatabase.getDatabase(context)

    suspend fun exportTransactionsToCSV(): File? = withContext(Dispatchers.IO) {
        try {
            val transactions = appDb.transactionDao().getAllTransactions()
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val csvFile = File(exportDir, "VINORA_Transactions_${System.currentTimeMillis()}.csv")
            csvFile.bufferedWriter().use { writer ->
                // Header CSV
                writer.write("ID,SyncID,Ngay,Loai,DanhMuc,SoTien,MoTa\n")
                transactions.forEach { t ->
                    val type = if (t.isExpense) "Chi Tieu" else "Thu Nhap"
                    val descEscaped = t.description.replace(",", " ")
                    val line = "${t.id},${t.syncId},${t.date},$type,${t.category},${t.amount},$descEscaped\n"
                    writer.write(line)
                }
            }
            csvFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareCSVFile(file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "Xuất báo cáo CSV VINORA")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
