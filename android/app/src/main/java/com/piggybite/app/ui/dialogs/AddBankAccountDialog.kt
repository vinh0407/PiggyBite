package com.piggybite.app.ui.dialogs

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
import com.piggybite.app.model.Bank
import com.piggybite.app.model.BankAccount
import com.piggybite.app.model.BankDirectory
import com.piggybite.app.ui.components.BankLogoView
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkButtonBorder
import com.piggybite.app.ui.theme.DarkExpenseBg
import com.piggybite.app.ui.theme.DarkExpenseBorder
import com.piggybite.app.ui.theme.DarkExpenseText
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.ui.theme.TextMuted
import java.util.UUID

@Composable
fun AddBankAccountDialog(
    initialAccount: BankAccount? = null,
    onDismiss: () -> Unit,
    onSaveAccount: (BankAccount) -> Unit,
    onDeleteAccount: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkMode.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedBank by remember {
        mutableStateOf(
            if (initialAccount != null) {
                BankDirectory.findBankById(initialAccount.bankId) ?: BankDirectory.ALL_BANKS.first()
            } else {
                BankDirectory.ALL_BANKS.first()
            }
        )
    }
    var accountNumber by remember { mutableStateOf(initialAccount?.accountNumber ?: "") }
    var balanceText by remember { mutableStateOf(initialAccount?.balance?.toString() ?: "0") }

    val filteredBanks = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            BankDirectory.ALL_BANKS
        } else {
            val q = searchQuery.lowercase()
            BankDirectory.ALL_BANKS.filter {
                it.shortName.lowercase().contains(q) || it.name.lowercase().contains(q)
            }
        }
    }

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
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialAccount != null) "Chỉnh sửa ngân hàng/ví" else "Thêm tài khoản ngân hàng / Ví",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

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

                Spacer(modifier = Modifier.height(14.dp))

                // Select Bank from all 36 Vietnamese Banks & Wallets
                Text(
                    text = "CHỌN NGÂN HÀNG HOẶC VÍ ĐIỆN TỬ (36 NGÂN HÀNG VIỆT NAM):",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Tìm kiếm ngân hàng, ví MoMo, VCB, MB...", fontSize = 12.sp, color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredBanks) { b ->
                        val isSelected = selectedBank.id == b.id
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
                                .clip(RoundedCornerShape(10.dp))
                                .background(rowBg)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = rowBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedBank = b }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BankLogoView(bankId = b.id, size = 28.dp)

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "${b.shortName} (${b.name})",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )

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

                Spacer(modifier = Modifier.height(12.dp))

                // Account Number
                Text(
                    text = "SỐ TÀI KHOẢN (STK) / SỐ ĐIỆN THOẠI VÍ:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    placeholder = { Text("Ví dụ: 0988776655", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Balance Input
                Text(
                    text = "SỐ TIỀN HIỆN CÓ TRONG TÀI KHOẢN (VNĐ):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { input -> balanceText = input.filter { it.isDigit() } },
                    placeholder = { Text("0", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (initialAccount != null && onDeleteAccount != null) {
                        Button(
                            onClick = {
                                onDeleteAccount(initialAccount.id)
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) DarkExpenseBg else Color(0xFFFDE8E7),
                                contentColor = if (isDark) DarkExpenseText else Color(0xFFD9534F)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isDark) DarkExpenseBorder else Color(0xFFFCA5A5)
                            )
                        ) {
                            Text(text = "Xóa", fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            val bal = balanceText.toLongOrNull() ?: 0L
                            val acc = BankAccount(
                                id = initialAccount?.id ?: ("acc_" + UUID.randomUUID().toString().take(8)),
                                bankId = selectedBank.id,
                                bankName = selectedBank.shortName,
                                accountNumber = accountNumber.trim(),
                                balance = bal
                            )
                            onSaveAccount(acc)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) DarkButtonBg else SkyBluePrimary,
                            contentColor = if (isDark) DarkTitleBlue else Color.White
                        ),
                        border = BorderStroke(1.dp, if (isDark) DarkButtonBorder else Color.Transparent)
                    ) {
                        Text(
                            text = if (initialAccount != null) "Lưu thay đổi" else "Thêm tài khoản",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
