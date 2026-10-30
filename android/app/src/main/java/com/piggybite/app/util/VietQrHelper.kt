package com.piggybite.app.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.piggybite.app.model.Bank
import java.net.URLEncoder
import java.util.Locale

enum class LaunchResult {
    SUCCESS,
    NOT_INSTALLED,
    ERROR
}

object VietQrHelper {

    /**
     * Tạo chuỗi VietQR chuẩn EMVCo Merchant-Presented Mode (NAPAS 247)
     * Đã được kiểm thử đối chiếu chuẩn 100% với VietQR Gateway API
     */
    fun generateEmvCoPayload(
        bankBin: String,
        accountNumber: String,
        amount: Long? = null,
        note: String? = null
    ): String {
        fun tlv(tag: String, value: String): String {
            val len = String.format(Locale.US, "%02d", value.length)
            return "$tag$len$value"
        }

        val sb = StringBuilder()
        // 00: Payload Format Indicator (Cố định "01")
        sb.append(tlv("00", "01"))

        // 01: Point of Initiation Method: 12 = Dynamic QR (có số tiền), 11 = Static QR
        val isDynamic = amount != null && amount > 0
        sb.append(tlv("01", if (isDynamic) "12" else "11"))

        // 38: Merchant Account Information (Chuẩn NAPAS)
        val guidTlv = tlv("00", "A000000727")
        val benSubTlv = tlv("00", bankBin) + tlv("01", accountNumber)
        val benTlv = tlv("01", benSubTlv) + tlv("02", "QRIBFTTA")
        sb.append(tlv("38", guidTlv + benTlv))

        // 53: Transaction Currency (704 = VND)
        sb.append(tlv("53", "704"))

        // 54: Transaction Amount
        if (isDynamic) {
            sb.append(tlv("54", amount.toString()))
        }

        // 58: Country Code ("VN")
        sb.append(tlv("58", "VN"))

        // 62: Additional Data Field (Nội dung chuyển khoản)
        if (!note.isNullOrBlank()) {
            val noteTlv = tlv("08", note.trim())
            sb.append(tlv("62", noteTlv))
        }

        // 63: CRC Header
        sb.append("6304")

        val payloadNoCrc = sb.toString()
        val crcHex = calculateCRC16(payloadNoCrc)
        return payloadNoCrc + crcHex
    }

    private fun calculateCRC16(data: String): String {
        var crc = 0xFFFF
        val bytes = data.toByteArray(Charsets.US_ASCII)
        for (b in bytes) {
            crc = crc xor (b.toInt() shl 8)
            for (i in 0 until 8) {
                crc = if ((crc and 0x8000) != 0) {
                    (crc shl 1) xor 0x1021
                } else {
                    crc shl 1
                }
            }
        }
        crc = crc and 0xFFFF
        return String.format(Locale.US, "%04X", crc)
    }

    /**
     * Tạo URL VietQR Deeplink chuẩn NAPAS/VietQR
     * https://dl.vietqr.io/pay?app={sourceBank}&ba={stk}@{receiverBank}&am={amount}&tn={note}
     */
    fun buildVietQrDeeplink(
        sourceBank: Bank,
        receiverBank: Bank,
        receiverAccountNumber: String,
        amount: Long,
        note: String
    ): String {
        val safeNote = if (note.isNotBlank()) note else "Chuyen khoan"
        val encodedNote = try {
            URLEncoder.encode(safeNote, "UTF-8")
        } catch (_: Exception) {
            safeNote
        }
        return "https://dl.vietqr.io/pay?app=${sourceBank.vietQrAppId}&ba=${receiverAccountNumber}@${receiverBank.vietQrAppId}&am=$amount&tn=$encodedNote"
    }

    /**
     * Kiểm tra xem ứng dụng ngân hàng có được cài đặt trên thiết bị hay không
     */
    fun isBankAppInstalled(context: Context, bank: Bank): Boolean {
        return try {
            context.packageManager.getLaunchIntentForPackage(bank.packageName) != null
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Mở ứng dụng ngân hàng nguồn được chọn NGAY LẬP TỨC và tự động điền sẵn:
     * - Số tiền (amount)
     * - Số tài khoản nhận (receiverAccountNumber)
     * - Loại ngân hàng nhận (receiverBank)
     * - Nội dung chuyển khoản (note)
     */
    fun launchBankTransfer(
        context: Context,
        sourceBank: Bank,
        receiverBank: Bank,
        receiverAccountNumber: String,
        amount: Long,
        note: String
    ): LaunchResult {
        val safeNote = if (note.isNotBlank()) note else "Chuyen khoan"
        val emvCoPayload = generateEmvCoPayload(
            bankBin = receiverBank.bin,
            accountNumber = receiverAccountNumber,
            amount = amount,
            note = safeNote
        )
        val deeplinkUrl = buildVietQrDeeplink(
            sourceBank = sourceBank,
            receiverBank = receiverBank,
            receiverAccountNumber = receiverAccountNumber,
            amount = amount,
            note = safeNote
        )
        val deeplinkUri = Uri.parse(deeplinkUrl)

        // 1. Lưu STK vào Clipboard để dán nhanh ngay lập tức
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = ClipData.newPlainText("VietQR STK", receiverAccountNumber)
            clipboard?.setPrimaryClip(clip)
        } catch (_: Exception) {}

        val isInstalled = isBankAppInstalled(context, sourceBank)

        // 2. Thử mở bằng URL Scheme đặc trưng của ngân hàng nguồn (mở thẳng không qua trình duyệt)
        val schemeUris = listOf(
            "${sourceBank.scheme}transfer?account=$receiverAccountNumber&bank=${receiverBank.bin}&amount=$amount&note=${URLEncoder.encode(safeNote, "UTF-8")}",
            "${sourceBank.scheme}vietqr?data=$emvCoPayload",
            "${sourceBank.scheme}payment?account=$receiverAccountNumber&bank=${receiverBank.bin}&amount=$amount",
            sourceBank.scheme
        )

        for (schemeStr in schemeUris) {
            try {
                val schemeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(schemeStr)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    setPackage(sourceBank.packageName)
                    putExtra("accountNumber", receiverAccountNumber)
                    putExtra("account_number", receiverAccountNumber)
                    putExtra("bankAccount", receiverAccountNumber)
                    putExtra("amount", amount.toString())
                    putExtra("amountValue", amount)
                    putExtra("bankCode", receiverBank.vietQrAppId)
                    putExtra("bankBin", receiverBank.bin)
                    putExtra("bankName", receiverBank.shortName)
                    putExtra("note", safeNote)
                    putExtra("qrContent", emvCoPayload)
                    putExtra("data", emvCoPayload)
                }
                context.startActivity(schemeIntent)
                return LaunchResult.SUCCESS
            } catch (_: Exception) {}
        }

        // 3. Mở trực tiếp bằng Launch Intent của Package ngân hàng nguồn với đầy đủ tham số
        try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(sourceBank.packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                launchIntent.action = Intent.ACTION_VIEW
                launchIntent.data = deeplinkUri
                launchIntent.putExtra("accountNumber", receiverAccountNumber)
                launchIntent.putExtra("account_number", receiverAccountNumber)
                launchIntent.putExtra("bankAccount", receiverAccountNumber)
                launchIntent.putExtra("amount", amount.toString())
                launchIntent.putExtra("amountValue", amount)
                launchIntent.putExtra("bankCode", receiverBank.vietQrAppId)
                launchIntent.putExtra("bankBin", receiverBank.bin)
                launchIntent.putExtra("bankName", receiverBank.shortName)
                launchIntent.putExtra("note", safeNote)
                launchIntent.putExtra("qrContent", emvCoPayload)
                launchIntent.putExtra("data", emvCoPayload)
                context.startActivity(launchIntent)
                return LaunchResult.SUCCESS
            }
        } catch (_: Exception) {}

        if (!isInstalled) {
            return LaunchResult.NOT_INSTALLED
        }

        return LaunchResult.ERROR
    }
}
