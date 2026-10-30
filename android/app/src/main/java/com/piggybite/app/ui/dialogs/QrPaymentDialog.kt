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
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkButtonBorder
import com.piggybite.app.ui.theme.DarkCardBackground
import com.piggybite.app.ui.theme.DarkCardBorder
import com.piggybite.app.ui.theme.DarkTextMain
import com.piggybite.app.ui.theme.DarkTextMuted
import com.piggybite.app.ui.theme.DarkTextSub
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.NepGreenPrimary
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
    CHECK_EXPENSE // Exact Nếp screen "Kiểm tra khoản chi"
}

@Composable
fun QrPaymentDialog(
    accounts: List<BankAccount>,
    onDismiss: () -> Unit,
    onPaymentConfirmed: (Transaction) -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkMode.current

    var currentStep by remember { mutableStateOf(QrPaymentStep.SCAN_CAMERA) }

    // Smart Parsed Data
    var receiverName by remember { mutableStateOf("Nguyen Thu Ha") }
    var receiverBank by remember { mutableStateOf(BankDirectory.ALL_BANKS.firstOrNull { it.shortName == "Vietcombank" } ?: BankDirectory.ALL_BANKS[0]) }
    var receiverAccNum by remember { mutableStateOf("0071000123456") }
    var amountValue by remember { mutableStateOf(65000L) }
    var amountText by remember { mutableStateOf("65000") }
    var selectedCategory by remember { mutableStateOf("Ăn uống") }
    var noteText by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf<BankAccount?>(accounts.firstOrNull()) }

    // Paste text input
    var pasteInputText by remember { mutableStateOf("Techcombank 1903 6688 999 Tran Thi Mai") }

    // Parse pasted string on change
    fun parsePastedText(raw: String) {
        val foundBank = BankDirectory.ALL_BANKS.firstOrNull { raw.contains(it.shortName, ignoreCase = true) || raw.contains(it.id, ignoreCase = true) }
        if (foundBank != null) {
            receiverBank = foundBank
        }

        // Extract digits (STK)
        val digits = raw.filter { it.isDigit() }
        if (digits.length >= 6) {
            receiverAccNum = digits.take(16)
        }

        // Extract name
        val words = raw.split(" ", "\n", "\t").filter { it.isNotBlank() && it.none { c -> c.isDigit() } }
        val probableName = words.filter { it.length > 2 && it.lowercase() !in listOf("techcombank", "vietcombank", "mbbank", "bidv", "stk", "tk", "ngan", "hang") }
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
            // STEP 1: CAMERA QR SCANNER
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

            // STEP 2: "CHỈ CÓ SỐ TÀI KHOẢN? DÁN LÀ XONG" (Screenshot 2)
            QrPaymentStep.PASTE_ACCOUNT -> {
                Surface(
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        // Header: Huỷ | Số tài khoản | Tiếp
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Huỷ",
                                fontSize = 15.sp,
                                color = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                                modifier = Modifier
                                    .clickable { currentStep = QrPaymentStep.SCAN_CAMERA }
                                    .padding(8.dp)
                            )
                            Text(
                                text = "Số tài khoản",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tiếp",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                modifier = Modifier
                                    .clickable {
                                        parsePastedText(pasteInputText)
                                        currentStep = QrPaymentStep.CHECK_EXPENSE
                                    }
                                    .padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Large Input Box with Clear (x) button
                        OutlinedTextField(
                            value = pasteInputText,
                            onValueChange = {
                                pasteInputText = it
                                parsePastedText(it)
                            },
                            placeholder = {
                                Text(
                                    "Dán STK hoặc nội dung: Techcombank 1903 6688 999 Tran Thi Mai",
                                    color = DarkTextMuted,
                                    fontSize = 13.5.sp
                                )
                            },
                            trailingIcon = {
                                if (pasteInputText.isNotEmpty()) {
                                    IconButton(onClick = { pasteInputText = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Xoá",
                                            tint = DarkTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                unfocusedBorderColor = if (isDark) DarkCardBorder else Color(0xFFE5E7EB),
                                focusedContainerColor = if (isDark) DarkButtonBg else Color(0xFFF9FAFB),
                                unfocusedContainerColor = if (isDark) DarkButtonBg else Color(0xFFF9FAFB),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // "Nếp đọc được" (PiggyBite đọc được) Card
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
                                .border(1.dp, if (isDark) DarkCardBorder else Color(0xFFE5E7EB), RoundedCornerShape(18.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            // Row 1: Ngân hàng nhận
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ngân hàng nhận",
                                    fontSize = 13.5.sp,
                                    color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                                )
                                Text(
                                    text = "${receiverBank.shortName} ›",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(if (isDark) DarkCardBorder else Color(0xFFE5E7EB)))

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
                                    text = "$receiverAccNum ›",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(if (isDark) DarkCardBorder else Color(0xFFE5E7EB)))

                            // Row 3: Tên
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tên",
                                    fontSize = 13.5.sp,
                                    color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                                )
                                Text(
                                    text = "$receiverName ›",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(if (isDark) DarkCardBorder else Color(0xFFE5E7EB)))

                            // Row 4: Số tiền
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
                                    color = if (isDark) DarkTitleBlue else SkyBluePrimary
                                )
                            }
                        }
                    }
                }
            }

            // STEP 3: "KIỂM TRA KHOẢN CHI" (Exact Nếp Screen 2)
            QrPaymentStep.CHECK_EXPENSE -> {
                Surface(
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        // Header: Quay lại | Kiểm tra khoản chi | Sửa
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
                                    .clickable { currentStep = QrPaymentStep.SCAN_CAMERA }
                                    .padding(4.dp)
                            )
                            Text(
                                text = "Kiểm tra khoản chi",
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
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

                        // Recipient Info (Nếp centered layout)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = receiverName,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${receiverBank.shortName} · $receiverAccNum",
                                fontSize = 13.5.sp,
                                color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Giant Amount: 65.000đ
                            OutlinedTextField(
                                value = amountText,
                                onValueChange = { input ->
                                    amountText = input.filter { it.isDigit() }
                                    amountValue = amountText.toLongOrNull() ?: 0L
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
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

                        Spacer(modifier = Modifier.height(24.dp))

                        // Form Card: Danh mục & Ghi chú
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isDark) DarkCardBackground else Color(0xFFF9FAFB))
                                .border(1.dp, if (isDark) DarkCardBorder else Color(0xFFE5E7EB), RoundedCornerShape(18.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            // Row 1: Danh mục
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                Text(
                                    text = selectedCategory,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(if (isDark) DarkCardBorder else Color(0xFFE5E7EB)))

                            // Row 2: Ghi chú
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Ghi chú",
                                        fontSize = 14.sp,
                                        color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                                    )
                                }
                                Text(
                                    text = noteText.ifBlank { "Không bắt buộc" },
                                    fontSize = 14.sp,
                                    color = if (noteText.isBlank()) (if (isDark) DarkTextSub else Color(0xFF9CA3AF)) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Sticky Bottom Row: Source Bank Chip + "Chuyển khoản" Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Source bank selector chip (e.g. [BIDV] BIDV)
                            val displaySourceBank = selectedAccount?.bankName ?: "BIDV"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isDark) DarkButtonBg else Color(0xFFF3F4F6))
                                    .border(1.dp, if (isDark) DarkButtonBorder else Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = displaySourceBank,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Big Green Button: "Chuyển khoản" (Exact Nếp design)
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
                                        bankAccountId = selectedAccount?.id ?: receiverBank.id,
                                        bankName = selectedAccount?.bankName ?: receiverBank.shortName,
                                        note = "$receiverName (${if (noteText.isNotBlank()) noteText else selectedCategory})",
                                        timestamp = System.currentTimeMillis(),
                                        category = selectedCategory
                                    )

                                    onPaymentConfirmed(tx)

                                    // Copy STK to clipboard
                                    try {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("VietQR STK", receiverAccNum)
                                        clipboard?.setPrimaryClip(clip)
                                    } catch (_: Exception) {}

                                    // Deep-link to target bank
                                    try {
                                        val launchIntent = context.packageManager.getLaunchIntentForPackage(receiverBank.packageName)
                                        if (launchIntent != null) {
                                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            context.startActivity(launchIntent)
                                        }
                                    } catch (_: Exception) {}

                                    Toast.makeText(context, "Đã ghi chép & mở app ngân hàng (Đã sao chép STK: $receiverAccNum)", Toast.LENGTH_LONG).show()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NepGreenPrimary,
                                    contentColor = Color.White
                                )
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
}
