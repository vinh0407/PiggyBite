package com.money.app.ui.dialogs

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.money.app.model.BankAccount
import com.money.app.model.BankDirectory
import com.money.app.model.Transaction
import com.money.app.ui.components.BankLogoView
import com.money.app.ui.theme.DarkButtonBg
import com.money.app.ui.theme.DarkButtonBorder
import com.money.app.ui.theme.DarkExpenseBg
import com.money.app.ui.theme.DarkExpenseBorder
import com.money.app.ui.theme.DarkExpenseText
import com.money.app.ui.theme.DarkIncomeBg
import com.money.app.ui.theme.DarkIncomeBorder
import com.money.app.ui.theme.DarkIncomeText
import com.money.app.ui.theme.DarkTextMain
import com.money.app.ui.theme.DarkTitleBlue
import com.money.app.ui.theme.ExpenseRed
import com.money.app.ui.theme.ExpenseRedSoft
import com.money.app.ui.theme.LocalIsDarkMode
import com.money.app.ui.theme.SkyBluePrimary
import com.money.app.ui.theme.SkyBlueSoft
import com.money.app.ui.theme.TextDeepNavy
import com.money.app.ui.theme.TextMuted
import com.money.app.util.Formatters
import java.util.UUID

@Composable
fun QrPaymentDialog(
    accounts: List<BankAccount>,
    onDismiss: () -> Unit,
    onPaymentConfirmed: (Transaction) -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkMode.current

    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("Chuyển khoản thanh toán QR") }
    var selectedAccount by remember { mutableStateOf<BankAccount?>(accounts.firstOrNull()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Chuyển tiền quét mã QR",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Nhập số tiền và chọn ngân hàng nguồn",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", fontSize = 16.sp, color = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Amount input
                Text(
                    text = "SỐ TIỀN CẦN CHUYỂN (VNĐ):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        amountText = input.filter { it.isDigit() }
                    },
                    placeholder = { Text("Ví dụ: 150000", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )

                // Quick presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(50000L, 100000L, 200000L, 500000L).forEach { preset ->
                        val presetBg = if (isDark) DarkButtonBg else Color(0xFFF7F5F0)
                        val presetBorder = if (isDark) DarkButtonBorder else MaterialTheme.colorScheme.outline
                        val presetText = if (isDark) DarkTitleBlue else MaterialTheme.colorScheme.onSurface

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(presetBg)
                                .border(1.dp, presetBorder, RoundedCornerShape(10.dp))
                                .clickable { amountText = preset.toString() }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${preset / 1000}k",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = presetText
                            )
                        }
                    }
                }

                val parsedAmount = amountText.toLongOrNull() ?: 0L
                val aiRecommendation = remember(parsedAmount, accounts) {
                    com.money.app.service.SmartFinanceAdvisor.recommendBankForPayment(parsedAmount, accounts)
                }

                // AI Bank Recommendation Banner
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
                val bannerTitleColor = if (isDark) {
                    if (aiRecommendation.isSufficient) DarkIncomeText else DarkExpenseText
                } else {
                    if (aiRecommendation.isSufficient) SkyBluePrimary else ExpenseRed
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bannerBg)
                        .border(1.dp, bannerBorder, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (aiRecommendation.isSufficient) "💡 AI Gợi ý chọn nguồn tiền:" else "⚠️ Cảnh báo số dư:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = bannerTitleColor
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (aiRecommendation.recommendedAccount != null && selectedAccount?.id != aiRecommendation.recommendedAccount.id) {
                                Text(
                                    text = "Áp dụng",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isDark) DarkButtonBg else Color.White)
                                        .border(if (isDark) 1.dp else 0.dp, if (isDark) DarkButtonBorder else Color.Transparent, RoundedCornerShape(6.dp))
                                        .clickable { selectedAccount = aiRecommendation.recommendedAccount }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = aiRecommendation.reason,
                            fontSize = 11.sp,
                            color = if (isDark) DarkTextMain else TextDeepNavy,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Account Selector
                Text(
                    text = "CHỌN TÀI KHOẢN TRỪ TIỀN (KÈM SỐ DƯ):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (accounts.isEmpty()) {
                    Text(
                        text = "Chưa có ngân hàng nào. Vui lòng thêm ngân hàng trước.",
                        color = if (isDark) DarkExpenseText else ExpenseRed,
                        fontSize = 12.sp
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(accounts) { acc ->
                            val isSelected = selectedAccount?.id == acc.id
                            val rowBg = if (isSelected) {
                                if (isDark) DarkButtonBg else Color(0xFFF0F7F3)
                            } else {
                                if (isDark) Color(0xFF131B2E) else Color(0xFFFAFAF7)
                            }
                            val rowBorder = if (isSelected) {
                                if (isDark) DarkTitleBlue else SkyBluePrimary
                            } else {
                                MaterialTheme.colorScheme.outline
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(rowBg)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = rowBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedAccount = acc }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BankLogoView(bankId = acc.bankId, size = 32.dp)

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = acc.bankName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Số dư: ${Formatters.formatMoney(acc.balance)}",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }

                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm & Deep Link Button
                Button(
                    onClick = {
                        val amount = amountText.toLongOrNull() ?: 0L
                        val acc = selectedAccount
                        if (amount <= 0L) {
                            Toast.makeText(context, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (acc == null) {
                            Toast.makeText(context, "Vui lòng chọn tài khoản ngân hàng", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Create transaction
                        val tx = Transaction(
                            id = "tx_qr_" + UUID.randomUUID().toString().take(8),
                            amount = amount,
                            isIncome = false,
                            bankAccountId = acc.id,
                            bankName = acc.bankName,
                            note = "Thanh toán QR qua ${acc.bankName}",
                            category = "Quét mã QR"
                        )
                        onPaymentConfirmed(tx)

                        // Launch Bank App Deep Link / Package
                        launchBankApp(context, acc.bankId, amount)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) DarkButtonBg else SkyBluePrimary,
                        contentColor = if (isDark) DarkTitleBlue else Color.White
                    ),
                    border = BorderStroke(1.dp, if (isDark) DarkButtonBorder else Color.Transparent)
                ) {
                    Text(
                        text = "Xác nhận trừ tiền & Mở app ngân hàng",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun launchBankApp(context: Context, bankId: String, amount: Long) {
    val bank = BankDirectory.findBankById(bankId)
    val pm = context.packageManager

    // Try launch intent for package first
    if (bank != null) {
        val launchIntent = pm.getLaunchIntentForPackage(bank.packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            Toast.makeText(context, "Đã trừ ${Formatters.formatMoney(amount)} trên PiggyBite & đang mở ${bank.shortName}", Toast.LENGTH_LONG).show()
            return
        }

        // Try scheme URI
        try {
            val schemeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(bank.scheme))
            schemeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(schemeIntent)
            Toast.makeText(context, "Đang mở ${bank.shortName}...", Toast.LENGTH_SHORT).show()
            return
        } catch (e: Exception) {
            // Ignore scheme failure
        }
    }

    Toast.makeText(context, "Đã trừ ${Formatters.formatMoney(amount)} trên app. Chưa tìm thấy app ngân hàng trên máy để mở trực tiếp.", Toast.LENGTH_LONG).show()
}
