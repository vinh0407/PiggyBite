package com.piggybite.app.util

import android.net.Uri
import com.piggybite.app.model.Bank
import com.piggybite.app.model.BankDirectory

data class VietQrResult(
    val rawText: String,
    val bank: Bank?,
    val bankBin: String?,
    val accountNumber: String?,
    val receiverName: String?,
    val amount: Long?,
    val note: String?
)

object VietQrParser {

    fun parse(raw: String): VietQrResult {
        val trimmed = raw.trim()

        // 1. Standard EMVCo VietQR format (starts with "000201")
        if (trimmed.startsWith("000201") || (trimmed.contains("A000000727") && trimmed.length > 20)) {
            val emvcoResult = parseEmvco(trimmed)
            if (emvcoResult != null) return emvcoResult
        }

        // 2. URL Format (e.g. img.vietqr.io, qr.sepay.vn, etc.)
        if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
            val urlResult = parseUrl(trimmed)
            if (urlResult != null) return urlResult
        }

        // 3. Simple text fallback (e.g. "VCB 1028394857 50000" or raw account number)
        val textResult = parseSimpleText(trimmed)
        return textResult ?: VietQrResult(
            rawText = trimmed,
            bank = null,
            bankBin = null,
            accountNumber = null,
            receiverName = null,
            amount = null,
            note = trimmed.take(50)
        )
    }

    private fun parseEmvco(text: String): VietQrResult? {
        try {
            var index = 0
            val tlvMap = mutableMapOf<String, String>()

            while (index + 4 <= text.length) {
                val tag = text.substring(index, index + 2)
                val lenStr = text.substring(index + 2, index + 4)
                val len = lenStr.toIntOrNull() ?: break
                val valStart = index + 4
                val valEnd = valStart + len
                if (valEnd > text.length) break
                val value = text.substring(valStart, valEnd)
                tlvMap[tag] = value
                index = valEnd
            }

            var bankBin: String? = null
            var accountNumber: String? = null

            // Tag 38: Merchant Account Information
            val tag38 = tlvMap["38"]
            if (tag38 != null) {
                var subIndex = 0
                while (subIndex + 4 <= tag38.length) {
                    val subTag = tag38.substring(subIndex, subIndex + 2)
                    val subLen = tag38.substring(subIndex + 2, subIndex + 4).toIntOrNull() ?: break
                    val subValStart = subIndex + 4
                    val subValEnd = subValStart + subLen
                    if (subValEnd > tag38.length) break
                    val subVal = tag38.substring(subValStart, subValEnd)

                    // Sub-tag 00: Guid (A000000727 for VietQR)
                    // Sub-tag 01: Beneficiary organization (nested TLV)
                    if (subTag == "01") {
                        var bIndex = 0
                        while (bIndex + 4 <= subVal.length) {
                            val bTag = subVal.substring(bIndex, bIndex + 2)
                            val bLen = subVal.substring(bIndex + 2, bIndex + 4).toIntOrNull() ?: break
                            val bStart = bIndex + 4
                            val bEnd = bStart + bLen
                            if (bEnd > subVal.length) break
                            val bValue = subVal.substring(bStart, bEnd)
                            if (bTag == "00") bankBin = bValue
                            if (bTag == "01") accountNumber = bValue
                            bIndex = bEnd
                        }
                    } else if (subTag == "02") {
                        // Consumer account number in some older templates
                        if (accountNumber == null) accountNumber = subVal
                    }

                    subIndex = subValEnd
                }
            }

            // Tag 54: Transaction Amount
            val amount = tlvMap["54"]?.toDoubleOrNull()?.toLong()

            // Tag 59: Merchant Name
            val receiverName = tlvMap["59"]

            // Tag 62: Additional Data (Sub-tag 08: Purpose/Content)
            var note: String? = null
            val tag62 = tlvMap["62"]
            if (tag62 != null) {
                var sIndex = 0
                while (sIndex + 4 <= tag62.length) {
                    val sTag = tag62.substring(sIndex, sIndex + 2)
                    val sLen = tag62.substring(sIndex + 2, sIndex + 4).toIntOrNull() ?: break
                    val sStart = sIndex + 4
                    val sEnd = sStart + sLen
                    if (sEnd > tag62.length) break
                    val sVal = tag62.substring(sStart, sEnd)
                    if (sTag == "08") note = sVal
                    sIndex = sEnd
                }
            }

            val resolvedBank = bankBin?.let { BankDirectory.findBankByBin(it) }

            return VietQrResult(
                rawText = text,
                bank = resolvedBank,
                bankBin = bankBin,
                accountNumber = accountNumber,
                receiverName = receiverName,
                amount = amount,
                note = note ?: "Thanh toán QR"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun parseUrl(urlStr: String): VietQrResult? {
        return try {
            val uri = Uri.parse(urlStr)
            val path = uri.path ?: ""
            var bank: Bank? = null
            var accountNumber: String? = null
            var amount: Long? = null
            var note: String? = null

            // Query parameters
            uri.getQueryParameter("amount")?.toDoubleOrNull()?.let { amount = it.toLong() }
            uri.getQueryParameter("des")?.let { note = it }
            uri.getQueryParameter("addInfo")?.let { note = it }
            uri.getQueryParameter("acc")?.let { accountNumber = it }
            uri.getQueryParameter("account")?.let { accountNumber = it }

            val bankParam = uri.getQueryParameter("bank")
            if (bankParam != null) {
                bank = BankDirectory.findBankByKeyword(bankParam) ?: BankDirectory.findBankById(bankParam.lowercase())
            }

            // Path pattern e.g. /image/VCB-1028394857-compact2.png
            if (path.contains("image/") || path.contains("qr/")) {
                val segments = path.split("/").filter { it.isNotBlank() }
                for (seg in segments) {
                    if (seg.contains("-")) {
                        val parts = seg.split("-")
                        if (parts.size >= 2) {
                            if (bank == null) {
                                bank = BankDirectory.findBankByKeyword(parts[0]) ?: BankDirectory.findBankById(parts[0].lowercase())
                            }
                            if (accountNumber == null) {
                                accountNumber = parts[1].replace(".png", "").replace(".jpg", "")
                            }
                        }
                    }
                }
            }

            VietQrResult(
                rawText = urlStr,
                bank = bank,
                bankBin = bank?.bin,
                accountNumber = accountNumber,
                receiverName = bank?.shortName ?: "Người nhận QR",
                amount = amount,
                note = note ?: "Thanh toán QR"
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseSimpleText(text: String): VietQrResult? {
        val bank = BankDirectory.findBankByKeyword(text)
        val digits = text.split(" ", "\n", "-", ":", ",").filter { it.matches(Regex("\\d{6,19}")) }
        val accountNumber = digits.firstOrNull()

        if (bank != null || accountNumber != null) {
            return VietQrResult(
                rawText = text,
                bank = bank,
                bankBin = bank?.bin,
                accountNumber = accountNumber,
                receiverName = bank?.shortName ?: "Tài khoản nhận",
                amount = null,
                note = "Thanh toán QR"
            )
        }
        return null
    }
}
