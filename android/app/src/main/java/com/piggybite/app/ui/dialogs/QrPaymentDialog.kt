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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
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
import com.piggybite.app.ui.theme.DarkExpenseBg
import com.piggybite.app.ui.theme.DarkExpenseBorder
import com.piggybite.app.ui.theme.DarkExpenseText
import com.piggybite.app.ui.theme.DarkIncomeBg
import com.piggybite.app.ui.theme.DarkIncomeBorder
import com.piggybite.app.ui.theme.DarkIncomeText
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.ExpenseRed
import com.piggybite.app.ui.theme.ExpenseRedSoft
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
    ENTER_AMOUNT
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
    var scannedResult by remember { mutableStateOf<VietQrResult?>(null) }

    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("Thanh toán quét mã QR") }
    var manualAccountNum by remember { mutableStateOf("") }
    var manualBankName by remember { mutableStateOf("Vietcombank") }
    var selectedAccount by remember { mutableStateOf<BankAccount?>(accounts.firstOrNull()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        when (currentStep) {
            QrPaymentStep.SCAN_CAMERA -> {
                // Step 1: Live Camera QR Scanner Viewfinder
                CameraQrScannerView(
                    onQrCodeDetected = { rawText ->
                        val parsed = VietQrParser.parse(rawText)
                        scannedResult = parsed
                        if (parsed.amount != null && parsed.amount > 0) {
                            amountText = parsed.amount.toString()
                        }
                        if (!parsed.note.isNullOrBlank()) {
                            noteText = parsed.note
                        }
                        currentStep = QrPaymentStep.ENTER_AMOUNT
                    },
                    onManualInputClick = {
                        scannedResult = null
                        currentStep = QrPaymentStep.ENTER_AMOUNT
                    },
                    onClose = onDismiss
                )
            }

            QrPaymentStep.ENTER_AMOUNT -> {
                // Step 2: Enter Amount & Choose Bank to Pay
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
                            .padding(horizontal = 22.dp, vertical = 18.dp)
                    ) {
                        // Header with Back to Camera and Close
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { currentStep = QrPaymentStep.SCAN_CAMERA }
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Quét lại",
                                    tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Quét lại",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) DarkTitleBlue else SkyBluePrimary
                                )
                            }

                            Text(
                                text = "Nhập số tiền chuyển",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Đóng",
                                    tint = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Receiver Info Card
                            item {
                                val resolvedReceiverBank = scannedResult?.bank ?: BankDirectory.findBankByKeyword(manualBankName) ?: BankDirectory.ALL_BANKS[0]
                                val resolvedAccNum = scannedResult?.accountNumber ?: manualAccountNum.ifBlank { "1028394857" }
                                val resolvedReceiverName = scannedResult?.receiverName ?: "Người nhận thanh toán VietQR"

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(if (isDark) Color(0xFF1E293B) else SkyBlueSoft)
                                        .border(
                                            1.dp,
                                            if (isDark) DarkButtonBorder else SkyBluePrimary.copy(alpha = 0.3f),
                                            RoundedCornerShape(18.dp)
                                        )
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BankLogoView(bankId = resolvedReceiverBank.id, size = 44.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = resolvedReceiverName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.5.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${resolvedReceiverBank.shortName} • STK: $resolvedAccNum",
                                            fontSize = 12.5.sp,
                                            color = TextMuted
                                        )
                                        Text(
                                            text = "Đã quét thành công từ mã QR",
                                            fontSize = 11.sp,
                                            color = if (isDark) DarkIncomeText else Color(0xFF10B981),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            // Amount Input Field
                            item {
                                Text(
                                    text = "SỐ TIỀN CẦN CHUYỂN (VNĐ):",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = amountText,
                                    onValueChange = { input ->
                                        amountText = input.filter { it.isDigit() }
                                    },
                                    placeholder = { Text("Ví dụ: 50000", color = TextMuted) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    singleLine = true
                                )

                                // Quick presets
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(20000L, 50000L, 100000L, 200000L, 500000L).forEach { preset ->
                                        val isCurrent = amountText == preset.toString()
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (isCurrent) (if (isDark) DarkButtonBg else SkyBlueSoft) else (if (isDark) Color(0xFF1E293B) else Color(0xFFF7F5F0))
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isCurrent) (if (isDark) DarkTitleBlue else SkyBluePrimary) else MaterialTheme.colorScheme.outline,
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .clickable { amountText = preset.toString() }
                                                .padding(vertical = 7.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${preset / 1000}k",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCurrent) (if (isDark) DarkTitleBlue else SkyBluePrimary) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            // Note Field
                            item {
                                Text(
                                    text = "NỘI DUNG CHUYỂN KHOẢN:",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = noteText,
                                    onValueChange = { noteText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    singleLine = true
                                )
                            }

                            // AI Recommendation & Source Account Selection
                            item {
                                val parsedAmount = amountText.toLongOrNull() ?: 0L
                                val aiRecommendation = remember(parsedAmount, accounts) {
                                    com.piggybite.app.service.SmartFinanceAdvisor.recommendBankForPayment(parsedAmount, accounts)
                                }

                                val bannerBg = if (isDark) {
                                    if (aiRecommendation.isSufficient) DarkIncomeBg else DarkExpenseBg
                                } else {
                                    if (aiRecommendation.isSufficient) SkyBlueSoft else ExpenseRedSoft
                                }
                                val bannerBorder = if (isDark) {
                                    if (aiRecommendation.isSufficient) DarkIncomeBorder else DarkExpenseBorder
                                } else {
                                    if (aiRecommendation.isSufficient) SkyBluePrimary.copy(alpha = 0.3f) else ExpenseRed.copy(alpha = 0.3f)
                                }
                                val bannerTextColor = if (isDark) {
                                    if (aiRecommendation.isSufficient) DarkIncomeText else DarkExpenseText
                                } else {
                                    if (aiRecommendation.isSufficient) SkyBluePrimary else ExpenseRed
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(bannerBg)
                                        .border(1.dp, bannerBorder, RoundedCornerShape(14.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = bannerTextColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "AI Gợi ý nguồn tiền:",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = bannerTextColor
                                            )
                                            Text(
                                                text = aiRecommendation.reason,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "CHỌN TÀI KHOẢN NGUỒN ĐỂ TRỪ TIỀN:",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            }

                            if (accounts.isEmpty()) {
                                item {
                                    Text(
                                        text = "Chưa có tài khoản ngân hàng nào trong ví. Giao dịch sẽ được ghi nhận vào sổ chi tiêu chung.",
                                        fontSize = 12.sp,
                                        color = TextMuted,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            } else {
                                items(accounts) { acc ->
                                    val isSelected = selectedAccount?.id == acc.id
                                    val parsedAmount = amountText.toLongOrNull() ?: 0L
                                    val isSufficient = acc.balance >= parsedAmount

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                if (isSelected) (if (isDark) DarkButtonBg else SkyBlueSoft) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            )
                                            .border(
                                                if (isSelected) 1.5.dp else 1.dp,
                                                if (isSelected) (if (isDark) DarkTitleBlue else SkyBluePrimary) else MaterialTheme.colorScheme.outline,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable { selectedAccount = acc }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BankLogoView(bankId = acc.bankId, size = 32.dp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = acc.bankName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Số dư: ${Formatters.formatMoney(acc.balance)}",
                                                fontSize = 12.sp,
                                                color = if (isSufficient) (if (isDark) DarkIncomeText else Color(0xFF10B981)) else TextMuted
                                            )
                                        }
                                        if (isSelected) {
                                            Text(
                                                text = "✓ Chọn",
                                                color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Confirm & Payment Action Buttons
                        val parsedAmount = amountText.toLongOrNull() ?: 0L

                        Button(
                            onClick = {
                                if (parsedAmount <= 0) {
                                    Toast.makeText(context, "Vui lòng nhập số tiền cần chuyển!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val resolvedReceiverBank = scannedResult?.bank ?: BankDirectory.findBankByKeyword(manualBankName) ?: BankDirectory.ALL_BANKS[0]
                                val resolvedAccNum = scannedResult?.accountNumber ?: manualAccountNum.ifBlank { "1028394857" }
                                val resolvedReceiverName = scannedResult?.receiverName ?: "Người nhận VietQR"

                                val tx = Transaction(
                                    id = UUID.randomUUID().toString(),
                                    amount = parsedAmount,
                                    isIncome = false,
                                    bankAccountId = selectedAccount?.id ?: resolvedReceiverBank.id,
                                    bankName = selectedAccount?.bankName ?: resolvedReceiverBank.shortName,
                                    note = "Chuyển tiền: $resolvedReceiverName ($noteText)",
                                    timestamp = System.currentTimeMillis(),
                                    category = "Ăn uống / Mua sắm"
                                )

                                onPaymentConfirmed(tx)

                                // Copy recipient account number & note to clipboard so banking app can auto-fill or user can easily paste
                                try {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("VietQR Payment Info", resolvedAccNum)
                                    clipboard?.setPrimaryClip(clip)
                                } catch (_: Exception) {}

                                // Attempt deep-link to source bank or receiver bank app
                                val targetBank = selectedAccount?.let { BankDirectory.findBankById(it.bankId) } ?: resolvedReceiverBank
                                var launched = false
                                try {
                                    val launchIntent = context.packageManager.getLaunchIntentForPackage(targetBank.packageName)
                                    if (launchIntent != null) {
                                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(launchIntent)
                                        launched = true
                                    } else {
                                        val schemeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(targetBank.scheme)).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(schemeIntent)
                                        launched = true
                                    }
                                } catch (_: Exception) {
                                    try {
                                        val storeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${targetBank.packageName}")).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(storeIntent)
                                        launched = true
                                    } catch (_: Exception) {
                                        try {
                                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${targetBank.packageName}")).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            context.startActivity(webIntent)
                                            launched = true
                                        } catch (_: Exception) {}
                                    }
                                }

                                Toast.makeText(
                                    context,
                                    "Đã ghi nhận chi tiêu & chuyển đến ${targetBank.shortName} (Đã copy STK: $resolvedAccNum)",
                                    Toast.LENGTH_LONG
                                ).show()
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) DarkButtonBg else SkyBluePrimary,
                                contentColor = if (isDark) DarkTitleBlue else Color.White
                            ),
                            border = BorderStroke(1.dp, if (isDark) DarkButtonBorder else Color.Transparent)
                        ) {
                            Text(
                                text = "Xác nhận & Mở ứng dụng ngân hàng",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
