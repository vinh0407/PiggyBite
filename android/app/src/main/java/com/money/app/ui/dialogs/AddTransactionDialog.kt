package com.money.app.ui.dialogs

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
import androidx.compose.foundation.lazy.LazyRow
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
import com.money.app.model.Transaction
import com.money.app.ui.theme.DarkButtonBg
import com.money.app.ui.theme.DarkButtonBorder
import com.money.app.ui.theme.DarkExpenseBg
import com.money.app.ui.theme.DarkExpenseBorder
import com.money.app.ui.theme.DarkExpenseText
import com.money.app.ui.theme.DarkIncomeBg
import com.money.app.ui.theme.DarkIncomeBorder
import com.money.app.ui.theme.DarkIncomeText
import com.money.app.ui.theme.DarkTitleBlue
import com.money.app.ui.theme.ExpenseRed
import com.money.app.ui.theme.IncomeGreen
import com.money.app.ui.theme.LocalIsDarkMode
import com.money.app.ui.theme.SkyBluePrimary
import com.money.app.ui.theme.SkyBlueSoft
import com.money.app.ui.theme.TextMuted
import java.util.UUID

@Composable
fun AddTransactionDialog(
    accounts: List<BankAccount>,
    onDismiss: () -> Unit,
    onAddTransaction: (Transaction) -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkMode.current

    var isIncome by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var categoryText by remember { mutableStateOf(if (isIncome) "Lương" else "Ăn uống") }
    var selectedAccount by remember { mutableStateOf<BankAccount?>(accounts.firstOrNull()) }

    val expenseCategories = listOf("Ăn uống", "Mua sắm", "Di chuyển", "Hóa đơn", "Giải trí", "Sức khỏe", "Khác")
    val incomeCategories = listOf("Lương", "Thưởng", "Kinh doanh", "Được cho/tặng", "Khác")

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
                        text = "Ghi chép giao dịch",
                        fontSize = 18.sp,
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

                // Toggle Income / Expense
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                        .padding(4.dp)
                ) {
                    val expBg = if (!isIncome) {
                        if (isDark) DarkExpenseBg else ExpenseRed
                    } else Color.Transparent
                    val expBorder = if (!isIncome && isDark) DarkExpenseBorder else Color.Transparent
                    val expText = if (!isIncome) {
                        if (isDark) DarkExpenseText else Color.White
                    } else TextMuted

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(expBg)
                            .border(if (!isIncome && isDark) 1.dp else 0.dp, expBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                isIncome = false
                                categoryText = "Ăn uống"
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chi tiêu",
                            color = expText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    val incBg = if (isIncome) {
                        if (isDark) DarkIncomeBg else IncomeGreen
                    } else Color.Transparent
                    val incBorder = if (isIncome && isDark) DarkIncomeBorder else Color.Transparent
                    val incText = if (isIncome) {
                        if (isDark) DarkIncomeText else Color.White
                    } else TextMuted

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(incBg)
                            .border(if (isIncome && isDark) 1.dp else 0.dp, incBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                isIncome = true
                                categoryText = "Lương"
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Thu nhập",
                            color = incText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount
                Text(
                    text = "SỐ TIỀN (VNĐ):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input -> amountText = input.filter { it.isDigit() } },
                    placeholder = { Text("0 đ", color = TextMuted) },
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

                Spacer(modifier = Modifier.height(12.dp))

                // Note
                Text(
                    text = "GHI CHÚ / MÔ TẢ:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { input ->
                        noteText = input
                        if (input.length >= 3) {
                            if (!isIncome) {
                                val aiRes = com.money.app.service.ExpenseClassifierAI.getInstance(context).classifyExpense(input)
                                if (aiRes.confidence >= 0.70f) {
                                    val match = when (aiRes.category) {
                                        com.money.app.service.ExpenseClassifierAI.CAT_FOOD -> "Ăn uống"
                                        com.money.app.service.ExpenseClassifierAI.CAT_SHOPPING -> "Mua sắm"
                                        com.money.app.service.ExpenseClassifierAI.CAT_TRANSPORT -> "Di chuyển"
                                        com.money.app.service.ExpenseClassifierAI.CAT_BILLS -> "Hóa đơn"
                                        com.money.app.service.ExpenseClassifierAI.CAT_ENTERTAINMENT -> "Giải trí"
                                        com.money.app.service.ExpenseClassifierAI.CAT_HEALTH -> "Sức khỏe"
                                        else -> null
                                    }
                                    if (match != null && expenseCategories.contains(match)) {
                                        categoryText = match
                                    }
                                }
                            } else {
                                val aiRes = com.money.app.service.IncomeClassifierAI.getInstance(context).classifyIncome(input)
                                if (aiRes.confidence >= 0.70f) {
                                    val match = when (aiRes.category) {
                                        com.money.app.service.IncomeClassifierAI.CAT_SALARY -> "Lương"
                                        com.money.app.service.IncomeClassifierAI.CAT_BONUS -> "Thưởng"
                                        com.money.app.service.IncomeClassifierAI.CAT_BUSINESS -> "Kinh doanh"
                                        com.money.app.service.IncomeClassifierAI.CAT_GIFT -> "Được cho/tặng"
                                        else -> null
                                    }
                                    if (match != null && incomeCategories.contains(match)) {
                                        categoryText = match
                                    }
                                }
                            }
                        }
                    },
                    placeholder = { Text("Ví dụ: Cơm trưa, đổ xăng...", color = TextMuted) },
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

                Spacer(modifier = Modifier.height(12.dp))

                // Categories
                Text(
                    text = "DANH MỤC:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val cats = if (isIncome) incomeCategories else expenseCategories
                    items(cats) { cat ->
                        val isSelected = categoryText == cat
                        val catBg = if (isSelected) {
                            if (isDark) DarkButtonBg else SkyBluePrimary
                        } else {
                            if (isDark) Color(0xFF172033) else Color(0xFFF1F5F9)
                        }
                        val catBorder = if (isSelected) {
                            if (isDark) DarkButtonBorder else SkyBluePrimary
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                        val catText = if (isSelected) {
                            if (isDark) DarkTitleBlue else Color.White
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(catBg)
                                .border(1.dp, catBorder, RoundedCornerShape(10.dp))
                                .clickable { categoryText = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = catText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bank Account Selection
                Text(
                    text = "TÀI KHOẢN NGÂN HÀNG:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(accounts) { acc ->
                        val isSelected = selectedAccount?.id == acc.id
                        val accBg = if (isSelected) {
                            if (isDark) DarkButtonBg else SkyBlueSoft
                        } else {
                            if (isDark) Color(0xFF172033) else Color(0xFFF8FAFC)
                        }
                        val accBorder = if (isSelected) {
                            if (isDark) DarkButtonBorder else SkyBluePrimary
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                        val accText = if (isSelected) {
                            if (isDark) DarkTitleBlue else SkyBluePrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(accBg)
                                .border(1.dp, accBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedAccount = acc }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = acc.bankName,
                                color = accText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val amount = amountText.toLongOrNull() ?: 0L
                        val acc = selectedAccount
                        if (amount <= 0L) {
                            Toast.makeText(context, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (acc == null) {
                            Toast.makeText(context, "Vui lòng chọn tài khoản ngân hàng", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val tx = Transaction(
                            id = "tx_" + UUID.randomUUID().toString().take(8),
                            amount = amount,
                            isIncome = isIncome,
                            bankAccountId = acc.id,
                            bankName = acc.bankName,
                            note = noteText.ifBlank { categoryText },
                            category = categoryText
                        )
                        onAddTransaction(tx)
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
                        text = "Lưu giao dịch",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
