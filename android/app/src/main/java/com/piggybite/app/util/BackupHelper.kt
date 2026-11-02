package com.piggybite.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.piggybite.app.model.BankAccount
import com.piggybite.app.model.Transaction
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class FullBackupPackage(
    val version: Int = 1,
    val app: String = "PiggyBite",
    val exportedAt: Long = System.currentTimeMillis(),
    val accounts: List<BankAccount>? = null,
    val transactions: List<Transaction>? = null
)

object BackupHelper {

    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val fileDateSuffix = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    /**
     * Xuất dữ liệu ra file CSV chuẩn UTF-8 có BOM (tương thích 100% với Excel tiếng Việt)
     */
    fun createCsvFile(context: Context, transactions: List<Transaction>): File {
        val fileName = "PiggyBite_GiaoDich_${fileDateSuffix.format(Date())}.csv"
        val file = File(context.cacheDir, fileName)

        file.bufferedWriter(Charsets.UTF_8).use { writer ->
            // Ghi Byte Order Mark (BOM) để Microsoft Excel mở tiếng Việt không bị lỗi font
            writer.write("\uFEFF")
            // Header
            writer.write("Mã GD,Thời gian,Loại giao dịch,Số tiền (VNĐ),Danh mục,Tài khoản / Ngân hàng,Ghi chú\n")

            transactions.forEach { tx ->
                val dateStr = dateFormat.format(Date(tx.timestamp))
                val typeStr = if (tx.isIncome) "Thu nhập" else "Chi tiêu"
                val amountStr = tx.amount.toString()
                val categoryStr = escapeCsv(tx.category)
                val bankStr = escapeCsv(tx.bankName)
                val noteStr = escapeCsv(tx.note)
                val idStr = escapeCsv(tx.id)

                writer.write("$idStr,$dateStr,$typeStr,$amountStr,$categoryStr,$bankStr,$noteStr\n")
            }
        }

        return file
    }

    /**
     * Xuất toàn bộ gói dữ liệu dự phòng JSON (Bao gồm các ví và toàn bộ lịch sử)
     */
    fun createJsonBackupFile(
        context: Context,
        accounts: List<BankAccount>,
        transactions: List<Transaction>
    ): File {
        val fileName = "PiggyBite_SaoLuu_${fileDateSuffix.format(Date())}.json"
        val file = File(context.cacheDir, fileName)

        val backupPackage = FullBackupPackage(
            accounts = accounts,
            transactions = transactions
        )
        val json = gson.toJson(backupPackage)

        file.writeText(json, Charsets.UTF_8)
        return file
    }

    /**
     * Chia sẻ file sao lưu qua Google Drive, Zalo, Email, Gmail hoặc lưu vào máy
     */
    fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
        val contentUri: Uri = try {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            Uri.fromFile(file)
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, file.nameWithoutExtension)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    /**
     * Khôi phục dữ liệu từ InputStream của file người dùng chọn (hỗ trợ cả JSON và CSV)
     */
    fun restoreFromUri(
        context: Context,
        uri: Uri
    ): Pair<List<BankAccount>?, List<Transaction>?> {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Không thể mở tệp")

        val content = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }

        // 1. Thử phân tích cú pháp JSON
        if (content.trim().startsWith("{")) {
            try {
                val backup = gson.fromJson(content, FullBackupPackage::class.java)
                if (backup != null && (backup.accounts != null || backup.transactions != null)) {
                    return Pair(backup.accounts ?: emptyList(), backup.transactions ?: emptyList())
                }
            } catch (_: Exception) {}
        }

        // 2. Thử phân tích cú pháp CSV
        val lines = content.lines().filter { it.isNotBlank() }
        if (lines.isNotEmpty()) {
            val transactions = mutableListOf<Transaction>()
            // Bỏ qua dòng header nếu có
            val startIndex = if (lines[0].contains("Thời gian") || lines[0].contains("Loại")) 1 else 0

            for (i in startIndex until lines.size) {
                val line = lines[i].trim()
                if (line.isBlank()) continue
                val parts = parseCsvLine(line)
                if (parts.size >= 5) {
                    val id = if (parts.size >= 7 && parts[0].isNotBlank()) parts[0] else "tx_res_" + UUID.randomUUID().toString().take(8)
                    val dateStr = if (parts.size >= 7) parts[1] else parts[0]
                    val typeStr = if (parts.size >= 7) parts[2] else parts[1]
                    val amountStr = if (parts.size >= 7) parts[3] else parts[2]
                    val category = if (parts.size >= 7) parts[4] else parts[3]
                    val bankName = if (parts.size >= 7) parts[5] else parts[4]
                    val note = if (parts.size >= 7) parts[6] else if (parts.size >= 6) parts[5] else ""

                    val isIncome = typeStr.contains("Thu", ignoreCase = true)
                    val amount = amountStr.replace(".", "").replace(",", "").filter { it.isDigit() }.toLongOrNull() ?: 0L
                    val timestamp = try {
                        dateFormat.parse(dateStr)?.time ?: System.currentTimeMillis()
                    } catch (_: Exception) {
                        System.currentTimeMillis()
                    }

                    if (amount > 0) {
                        transactions.add(
                            Transaction(
                                id = id,
                                amount = amount,
                                isIncome = isIncome,
                                bankAccountId = "restored_account",
                                bankName = bankName.ifBlank { "Ví tiền mặt" },
                                note = note,
                                timestamp = timestamp,
                                category = category.ifBlank { if (isIncome) "Thu nhập khác" else "Chi tiêu khác" }
                            )
                        )
                    }
                }
            }

            if (transactions.isNotEmpty()) {
                return Pair(null, transactions)
            }
        }

        throw IllegalArgumentException("Định dạng tệp không hợp lệ")
    }

    private fun escapeCsv(text: String): String {
        return if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            "\"" + text.replace("\"", "\"\"") + "\""
        } else {
            text
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val c = line[i]
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                    current.append('\"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim())
                current.clear()
            } else {
                current.append(c)
            }
            i++
        }
        result.add(current.toString().trim())
        return result
    }
}
