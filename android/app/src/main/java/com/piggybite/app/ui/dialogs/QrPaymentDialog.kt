package com.piggybite.app.ui.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.piggybite.app.model.BankAccount
import com.piggybite.app.model.BankDirectory
import com.piggybite.app.model.Transaction
import com.piggybite.app.ui.components.BankLogoView
import com.piggybite.app.ui.components.CameraQrScannerView
import com.piggybite.app.ui.theme.CardBorder
import com.piggybite.app.ui.theme.DarkAppBackground
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkButtonBorder
import com.piggybite.app.ui.theme.DarkCardBackground
import com.piggybite.app.ui.theme.DarkCardBorder
import com.piggybite.app.ui.theme.DarkHighlightNavyBg
import com.piggybite.app.ui.theme.DarkHighlightNavyBorder
import com.piggybite.app.ui.theme.DarkHighlightNavyText
import com.piggybite.app.ui.theme.DarkTextMain
import com.piggybite.app.ui.theme.DarkTextMuted
import com.piggybite.app.ui.theme.DarkTextSub
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.HighlightBlueLightBg
import com.piggybite.app.ui.theme.HighlightBlueLightBorder
import com.piggybite.app.ui.theme.HighlightBlueLightText
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.ui.theme.SkyBlueSoft
import com.piggybite.app.ui.theme.TextMuted
import com.piggybite.app.util.Formatters
import com.piggybite.app.util.VietQrParser
import com.piggybite.app.util.VietQrResult
import java.util.UUID

enum class QrPaymentStep {
    SCAN_CAMERA,
    PASTE_ACCOUNT,
    CHECK_EXPENSE
}

@Composable
fun QrPaymentDialog(
    accounts: List<BankAccount>,
    initialStep: QrPaymentStep = QrPaymentStep.SCAN_CAMERA,
    onDismiss: () -> Unit,
    onPaymentConfirmed: (Transaction) -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkMode.current

    var currentStep by remember { mutableStateOf(initialStep) }

    // Dữ liệu trống hoàn toàn (Xóa toàn bộ dữ liệu mẫu có sẵn)
    var receiverName by remember { mutableStateOf("") }
    var receiverBank by remember { mutableStateOf(BankDirectory.ALL_BANKS[0]) }
    var receiverAccNum by remember { mutableStateOf("") }
    var amountValue by remember { mutableStateOf(0L) }
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Ăn uống") }
    var noteText by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf<BankAccount?>(accounts.firstOrNull()) }

    // Ô nhập dán STK bắt đầu hoàn toàn rỗng
    var pasteInputText by remember { mutableStateOf("") }
    var isSourceCash by remember { mutableStateOf(false) }
    var showBankPickerModal by remember { mutableStateOf(false) }
    var showCategoryPickerModal by remember { mutableStateOf(false) }
    var showSourceAccountPickerModal by remember { mutableStateOf(false) }

    // AI Phân biệt dữ liệu (Fuzzy Bank Matching + Regex STK + Xử lý chính tả)
    fun parsePastedText(raw: String) {
        if (raw.isBlank()) return

        // 1. Nhận diện ngân hàng thông minh hỗ trợ lỗi chính tả (ví dụ: argibank -> Agribank, vietcombanh -> Vietcombank)
        val foundBank = BankDirectory.findBankByKeyword(raw)
        if (foundBank != null) {
            receiverBank = foundBank
        }

        // 2. Tách Số tài khoản (chuỗi số liên tiếp từ 6 - 19 chữ số)
        val digitMatch = Regex("\\b\\d{6,19}\\b").find(raw)
        if (digitMatch != null) {
            receiverAccNum = digitMatch.value
        } else {
            val allDigits = raw.filter { it.isDigit() }
            if (allDigits.length >= 6) {
                receiverAccNum = allDigits.take(19)
            }
        }

        // 3. Tách số tiền nếu có trong chuỗi (ví dụ: 50k, 100k, 50.000, 200000)
        val amountKMatch = Regex("(\\d+)\\s*(k|nghin|nghìn)", RegexOption.IGNORE_CASE).find(raw)
        if (amountKMatch != null) {
            val num = amountKMatch.groupValues[1].toLongOrNull() ?: 0L
            amountValue = num * 1000
            amountText = amountValue.toString()
        } else {
            val amountNumMatch = Regex("\\b(\\d{1,3}(?:[.,]\\d{3})+|\\d{4,9})\\s*(?:đ|vnd|dong|đồng)?\\b", RegexOption.IGNORE_CASE).find(raw)
            if (amountNumMatch != null) {
                val clean = amountNumMatch.groupValues[1].replace(".", "").replace(",", "")
                if (clean != receiverAccNum) {
                    val num = clean.toLongOrNull() ?: 0L
                    if (num in 1000..500_000_000) {
                        amountValue = num
                        amountText = num.toString()
                    }
                }
            }
        }

        // 4. Tên người nhận (nếu có thì lấy, không có thì không quan tâm theo yêu cầu)
        val words = raw.split(" ", "\n", "\t").filter { it.isNotBlank() && it.none { c -> c.isDigit() } }
        val probableName = words.filter { w ->
            val lw = w.lowercase()
            lw.length > 2 && lw !in listOf("techcombank", "vietcombank", "mbbank", "bidv", "agribank", "argibank", "vcb", "tcb", "stk", "tk", "ngan", "hang", "chuyen", "tien", "khoan")
        }
        if (probableName.isNotEmpty()) {
            receiverName = probableName.joinToString(" ").uppercase()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        when (currentStep) {
            // STEP 1: CAMERA QR SCANNER (Ghim mỗi ô vuông giữa, còn lại đen hoàn toàn)
            QrPaymentStep.SCAN_CAMERA -> {
                CameraQrScannerView(
                    onQrCodeDetected = { rawText ->
                        val parsed = VietQrParser.parse(rawText)
                        if (parsed.bank != null) receiverBank = parsed.bank
                        if (!parsed.accountNumber.isNullOrBlank()) receiverAccNum = parsed.accountNumber
                        if (!parsed.receiverName.isNullOrBlank()) receiverName = parsed.receiverName
                        if (parsed.amount != null && parsed.amount > 0) {
                            amountValue = parsed.amount
                            amountText = parsed.amount.toString()
                        }
                        if (!parsed.note.isNullOrBlank()) noteText = parsed.note

                        currentStep = QrPaymentStep.CHECK_EXPENSE
                    },
                    onManualInputClick = {
                        currentStep = QrPaymentStep.PASTE_ACCOUNT
                    },
                    onClose = onDismiss
                )
            }

            // STEP 2: DÁN SỐ TÀI KHOẢN (STK)
            QrPaymentStep.PASTE_ACCOUNT -> {
                Surface(
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = if (isDark) DarkAppBackground else Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Quay lại",
                                fontSize = 15.sp,
                                color = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                                modifier = Modifier
                                    .clickable { onDismiss() }
                                    .padding(8.dp)
                            )
                            Text(
                                text = "Dán số tài khoản",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkTextMain else Color(0xFF111827)
                            )
                            Text(
                                text = "Tiếp",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                modifier = Modifier
                                    .clickable {
                                        parsePastedText(pasteInputText)
                                        if (receiverAccNum.isBlank()) {
                                            Toast.makeText(context, "Vui lòng nhập hoặc dán số tài khoản", Toast.LENGTH_SHORT).show()
                                        } else {
                                            currentStep = QrPaymentStep.CHECK_EXPENSE
                                        }
                                    }
                                    .padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Ô nhập văn bản rỗng (Placeholder gợi ý: ví dụ "0337244067 agribank")
                        OutlinedTextField(
                            value = pasteInputText,
                            onValueChange = {
                                pasteInputText = it
                                parsePastedText(it)
                            },
                            placeholder = {
                                Text(
                                    "Dán STK & tên ngân hàng (Ví dụ: 0337244067 agribank)",
                                    color = if (isDark) DarkTextMuted else Color(0xFF9CA3AF),
                                    fontSize = 13.5.sp
                                )
                            },
                            trailingIcon = {
                                if (pasteInputText.isNotEmpty()) {
                                    IconButton(onClick = {
                                        pasteInputText = ""
                                        receiverAccNum = ""
                                        receiverName = ""
                                        amountValue = 0L
                                        amountText = ""
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Xoá",
                                            tint = if (isDark) DarkTextMuted else Color(0xFF9CA3AF),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                unfocusedBorderColor = if (isDark) DarkCardBorder else CardBorder,
                                focusedContainerColor = if (isDark) DarkCardBackground else Color(0xFFF9FAFB),
                                unfocusedContainerColor = if (isDark) DarkCardBackground else Color(0xFFF9FAFB),
                                focusedTextColor = if (isDark) DarkTextMain else Color(0xFF111827),
                                unfocusedTextColor = if (isDark) DarkTextMain else Color(0xFF111827)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Khối "PiggyBite đọc được"
                        Text(
                            text = "PiggyBite đọc được",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isDark) DarkCardBackground else Color(0xFFF9FAFB))
                                .border(1.dp, if (isDark) DarkCardBorder else CardBorder, RoundedCornerShape(18.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            // Row 1: Ngân hàng nhận (Có thể bấm để chọn hoặc tìm kiếm bất kỳ ngân hàng nào)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showBankPickerModal = true }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ngân hàng nhận",
                                    fontSize = 13.5.sp,
                                    color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BankLogoView(bankId = receiverBank.id, size = 20.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${receiverBank.shortName} ›",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) DarkTitleBlue else SkyBluePrimary
                                    )
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(if (isDark) DarkCardBorder else CardBorder))

                            // Row 2: Số tài khoản
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Số tài khoản",
                                    fontSize = 13.5.sp,
                                    color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                                )
                                Text(
                                    text = if (receiverAccNum.isNotBlank()) "$receiverAccNum ›" else "Chưa có ›",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (receiverAccNum.isNotBlank()) (if (isDark) DarkTitleBlue else SkyBluePrimary) else (if (isDark) DarkTextSub else Color(0xFF9CA3AF))
                                )
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(if (isDark) DarkCardBorder else CardBorder))

                            // Row 3: Số tiền
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Số tiền",
                                    fontSize = 13.5.sp,
                                    color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                                )
                                Text(
                                    text = if (amountValue > 0) "${Formatters.formatMoney(amountValue)} ›" else "Chưa có ›",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (amountValue > 0) (if (isDark) DarkTitleBlue else SkyBluePrimary) else (if (isDark) DarkTextSub else Color(0xFF9CA3AF))
                                )
                            }
                        }
                    }
                }
            }

            // STEP 3: "KIỂM TRA KHOẢN CHI"
            QrPaymentStep.CHECK_EXPENSE -> {
                Surface(
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = if (isDark) DarkAppBackground else Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Quay lại",
                                fontSize = 15.sp,
                                color = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                                modifier = Modifier
                                    .clickable { currentStep = QrPaymentStep.PASTE_ACCOUNT }
                                    .padding(4.dp)
                            )
                            Text(
                                text = "Kiểm tra khoản chi",
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkTextMain else Color(0xFF111827)
                            )
                            Text(
                                text = "Sửa",
                                fontSize = 15.sp,
                                color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                modifier = Modifier
                                    .clickable { currentStep = QrPaymentStep.PASTE_ACCOUNT }
                                    .padding(4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Thông tin người nhận
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (receiverName.isNotBlank()) {
                                Text(
                                    text = receiverName,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) DarkTextMain else Color(0xFF111827)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = "${receiverBank.shortName} · ${receiverAccNum.ifBlank { "Chưa nhập STK" }}",
                                fontSize = 14.sp,
                                color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Thẻ Nhập số tiền: Nhập tự do, hiển thị số tiền định dạng và các nút chọn nhanh
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(if (isDark) DarkCardBackground else Color(0xFFF9FAFB))
                                    .border(1.dp, if (isDark) DarkCardBorder else CardBorder, RoundedCornerShape(22.dp))
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Số tiền chuyển",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                OutlinedTextField(
                                    value = amountText,
                                    onValueChange = { input ->
                                        val clean = input.filter { it.isDigit() }
                                        amountText = clean
                                        amountValue = clean.toLongOrNull() ?: 0L
                                    },
                                    placeholder = {
                                        Text(
                                            "0 đ",
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isDark) DarkTextSub else Color(0xFF9CA3AF),
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    },
                                    trailingIcon = {
                                        if (amountText.isNotEmpty()) {
                                            IconButton(onClick = {
                                                amountText = ""
                                                amountValue = 0L
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Xoá",
                                                    tint = if (isDark) DarkTextMuted else Color(0xFF9CA3AF),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        color = if (isDark) DarkTitleBlue else SkyBluePrimary
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (amountValue > 0) {
                                    Text(
                                        text = "= ${Formatters.formatMoney(amountValue)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) DarkTextMain else Color(0xFF111827)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                } else {
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                // Các nút bấm chọn nhanh số tiền: +50k, +100k, +200k, +500k, +1tr
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    val quickChips = listOf(
                                        50_000L to "+50k",
                                        100_000L to "+100k",
                                        200_000L to "+200k",
                                        500_000L to "+500k",
                                        1_000_000L to "+1tr"
                                    )
                                    quickChips.forEach { (amt, label) ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isDark) DarkButtonBg else Color.White)
                                                .border(1.dp, if (isDark) DarkButtonBorder else CardBorder, RoundedCornerShape(10.dp))
                                                .clickable {
                                                    amountValue += amt
                                                    amountText = amountValue.toString()
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) DarkTitleBlue else SkyBluePrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Form Card: Danh mục (Bấm để chọn) & Ghi chú (Gõ trực tiếp)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isDark) DarkCardBackground else Color(0xFFF9FAFB))
                                .border(1.dp, if (isDark) DarkCardBorder else CardBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            // Row 1: Danh mục chi tiêu (Có thể bấm để chọn danh mục khác)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showCategoryPickerModal = true }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null,
                                        tint = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Danh mục",
                                        fontSize = 14.sp,
                                        color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$selectedCategory ›",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) DarkTitleBlue else SkyBluePrimary
                                    )
                                }
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(if (isDark) DarkCardBorder else CardBorder))

                            // Row 2: Ghi chú (Cho phép gõ trực tiếp nội dung giao dịch)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                OutlinedTextField(
                                    value = noteText,
                                    onValueChange = { noteText = it },
                                    placeholder = {
                                        Text(
                                            "Thêm ghi chú chuyển tiền...",
                                            fontSize = 13.5.sp,
                                            color = if (isDark) DarkTextSub else Color(0xFF9CA3AF)
                                        )
                                    },
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) DarkTextMain else Color(0xFF111827)
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Sticky Bottom Row: Nút chọn nguồn tiền (Tiền mặt / Ngân hàng) + Nút Chuyển khoản
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val displaySourceBank = when {
                                isSourceCash -> "💵 Tiền mặt"
                                selectedAccount != null -> selectedAccount!!.bankName
                                else -> "Chọn nguồn"
                            }
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isDark) DarkButtonBg else Color(0xFFF3F4F6))
                                    .border(1.dp, if (isDark) DarkButtonBorder else CardBorder, RoundedCornerShape(16.dp))
                                    .clickable { showSourceAccountPickerModal = true }
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isSourceCash) {
                                    Text(text = "💵", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                } else if (selectedAccount != null) {
                                    BankLogoView(bankId = selectedAccount!!.bankId, size = 18.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = displaySourceBank,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = if (isDark) DarkTextMain else Color(0xFF111827)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "▾",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) DarkTitleBlue else SkyBluePrimary
                                )
                            }

                            // Nút Chuyển khoản
                            Button(
                                onClick = {
                                    if (amountValue <= 0) {
                                        Toast.makeText(context, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    val tx = Transaction(
                                        id = UUID.randomUUID().toString(),
                                        amount = amountValue,
                                        isIncome = false,
                                        bankAccountId = if (isSourceCash) "cash" else (selectedAccount?.id ?: receiverBank.id),
                                        bankName = if (isSourceCash) "Tiền mặt" else (selectedAccount?.bankName ?: receiverBank.shortName),
                                        note = "Chuyển: ${receiverBank.shortName} STK $receiverAccNum (${if (noteText.isNotBlank()) noteText else selectedCategory})",
                                        timestamp = System.currentTimeMillis(),
                                        category = selectedCategory
                                    )

                                    onPaymentConfirmed(tx)

                                    if (isSourceCash) {
                                        Toast.makeText(context, "Đã ghi nhận chi tiền mặt: ${Formatters.formatMoney(amountValue)}", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                        return@Button
                                    }

                                    // Tự động sao chép STK chính xác vào Clipboard
                                    try {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("VietQR STK", receiverAccNum)
                                        clipboard?.setPrimaryClip(clip)
                                    } catch (_: Exception) {}

                                    // Mở ứng dụng ngân hàng nguồn (hoặc đích nếu chưa chọn nguồn)
                                    val bankToLaunch = selectedAccount?.let { BankDirectory.findBankById(it.bankId) } ?: receiverBank
                                    try {
                                        val launchIntent = context.packageManager.getLaunchIntentForPackage(bankToLaunch.packageName)
                                        if (launchIntent != null) {
                                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            context.startActivity(launchIntent)
                                        } else {
                                            val schemeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(bankToLaunch.scheme)).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            context.startActivity(schemeIntent)
                                        }
                                    } catch (_: Exception) {}

                                    Toast.makeText(context, "Đã sao chép STK: $receiverAccNum và mở app ${bankToLaunch.shortName}", Toast.LENGTH_LONG).show()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDark) DarkHighlightNavyBg else SkyBluePrimary,
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, if (isDark) DarkHighlightNavyBorder else Color.Transparent)
                            ) {
                                Text(
                                    text = "Chuyển khoản",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Hộp thoại chọn ngân hàng thủ công (khi người dùng bấm vào dòng Ngân hàng nhận)
    if (showBankPickerModal) {
        var bankSearchQuery by remember { mutableStateOf("") }
        val filteredBanks = remember(bankSearchQuery) {
            if (bankSearchQuery.isBlank()) {
                BankDirectory.ALL_BANKS
            } else {
                BankDirectory.ALL_BANKS.filter {
                    it.name.contains(bankSearchQuery, ignoreCase = true) ||
                    it.shortName.contains(bankSearchQuery, ignoreCase = true) ||
                    it.keywords.any { kw -> kw.contains(bankSearchQuery, ignoreCase = true) }
                }
            }
        }

        Dialog(onDismissRequest = { showBankPickerModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chọn ngân hàng nhận",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { showBankPickerModal = false }, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = bankSearchQuery,
                        onValueChange = { bankSearchQuery = it },
                        placeholder = { Text("Tìm kiếm ngân hàng (VCB, MB, Sacombank...)", fontSize = 12.5.sp, color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredBanks) { bank ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (receiverBank.id == bank.id) (if (isDark) DarkHighlightNavyBg else HighlightBlueLightBg) else Color.Transparent
                                    )
                                    .clickable {
                                        receiverBank = bank
                                        showBankPickerModal = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BankLogoView(bankId = bank.id, size = 32.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bank.shortName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = bank.name,
                                        fontSize = 11.sp,
                                        color = TextMuted,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Chọn danh mục chi tiêu
    if (showCategoryPickerModal) {
        val categories = listOf(
            "Ăn uống" to "🍜",
            "Mua sắm" to "🛍️",
            "Di chuyển" to "🚗",
            "Cà phê & Giải trí" to "☕",
            "Hóa đơn & Tiện ích" to "💡",
            "Sức khỏe" to "💊",
            "Nhà cửa & Sinh hoạt" to "🏠",
            "Giáo dục" to "🎓",
            "Khác" to "🏷️"
        )
        Dialog(onDismissRequest = { showCategoryPickerModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chọn danh mục chi tiêu",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { showCategoryPickerModal = false }, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { (cat, icon) ->
                            val isSelected = selectedCategory == cat
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) (if (isDark) DarkHighlightNavyBg else HighlightBlueLightBg) else Color.Transparent
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) (if (isDark) DarkHighlightNavyBorder else HighlightBlueLightBorder) else Color.Transparent,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        selectedCategory = cat
                                        showCategoryPickerModal = false
                                    }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = icon, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = cat,
                                    fontSize = 14.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) (if (isDark) DarkTitleBlue else SkyBluePrimary) else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) DarkTitleBlue else SkyBluePrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Chọn nguồn tiền chuyển (Tiền mặt, Tài khoản đã liên kết hoặc Mở app ngân hàng)
    if (showSourceAccountPickerModal) {
        Dialog(onDismissRequest = { showSourceAccountPickerModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chọn nguồn tiền chuyển",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { showSourceAccountPickerModal = false }, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Tùy chọn 1: Tiền mặt
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSourceCash) (if (isDark) DarkHighlightNavyBg else HighlightBlueLightBg) else (if (isDark) DarkCardBackground else Color(0xFFF9FAFB))
                                    )
                                    .border(
                                        1.dp,
                                        if (isSourceCash) (if (isDark) DarkHighlightNavyBorder else HighlightBlueLightBorder) else (if (isDark) DarkCardBorder else CardBorder),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        isSourceCash = true
                                        selectedAccount = null
                                        showSourceAccountPickerModal = false
                                    }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💵", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Tiền mặt",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Ghi nhận chi tiêu bằng tiền mặt trực tiếp",
                                        fontSize = 11.5.sp,
                                        color = TextMuted
                                    )
                                }
                                if (isSourceCash) {
                                    Text(text = "✓", fontWeight = FontWeight.Bold, color = if (isDark) DarkTitleBlue else SkyBluePrimary)
                                }
                            }
                        }

                        // Danh sách tài khoản ngân hàng của người dùng
                        if (accounts.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "TÀI KHOẢN NGÂN HÀNG CỦA BẠN:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            }
                            items(accounts) { acc ->
                                val isSelected = !isSourceCash && selectedAccount?.id == acc.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) (if (isDark) DarkHighlightNavyBg else HighlightBlueLightBg) else (if (isDark) DarkCardBackground else Color(0xFFF9FAFB))
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) (if (isDark) DarkHighlightNavyBorder else HighlightBlueLightBorder) else (if (isDark) DarkCardBorder else CardBorder),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable {
                                            selectedAccount = acc
                                            isSourceCash = false
                                            showSourceAccountPickerModal = false
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BankLogoView(bankId = acc.bankId, size = 32.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = acc.bankName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "STK: ${acc.accountNumber} • ${Formatters.formatMoney(acc.balance)}",
                                            fontSize = 11.5.sp,
                                            color = TextMuted
                                        )
                                    }
                                    if (isSelected) {
                                        Text(text = "✓", fontWeight = FontWeight.Bold, color = if (isDark) DarkTitleBlue else SkyBluePrimary)
                                    }
                                }
                            }
                        }

                        // Hoặc chọn mở app ngân hàng bất kỳ để chuyển khoản
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "HOẶC CHỌN ỨNG DỤNG NGÂN HÀNG ĐỂ MỞ APP:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }

                        items(BankDirectory.ALL_BANKS) { bank ->
                            val isSelected = !isSourceCash && selectedAccount?.bankId == bank.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedAccount = BankAccount(
                                            id = "source_${bank.id}",
                                            bankId = bank.id,
                                            bankName = bank.shortName,
                                            accountNumber = "",
                                            balance = 0L
                                        )
                                        isSourceCash = false
                                        showSourceAccountPickerModal = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BankLogoView(bankId = bank.id, size = 26.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = bank.shortName,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) (if (isDark) DarkTitleBlue else SkyBluePrimary) else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Mở app",
                                    fontSize = 11.5.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
