package com.piggybite.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piggybite.app.model.BankAccount
import com.piggybite.app.model.Transaction
import com.piggybite.app.service.AIAnswer
import com.piggybite.app.service.InsightLevel
import com.piggybite.app.service.SmartFinanceAdvisor
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
import com.piggybite.app.ui.theme.IncomeGreen
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.ui.theme.SkyBlueSoft
import com.piggybite.app.ui.theme.SkyBlueUltraLight
import com.piggybite.app.ui.theme.TextMuted

@Composable
fun AiAssistantCard(
    totalBalance: Long,
    todayExpense: Long,
    accounts: List<BankAccount>,
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkMode.current
    val insight = remember(totalBalance, todayExpense, transactions) {
        SmartFinanceAdvisor.analyzeCashflow(totalBalance, todayExpense, transactions)
    }

    val recurringItems = remember(transactions) {
        SmartFinanceAdvisor.detectRecurringExpenses(transactions)
    }

    var questionInput by remember { mutableStateOf("") }
    var currentAnswer by remember { mutableStateOf<AIAnswer?>(null) }
    var isExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val sampleQueries = listOf(
        "Hôm nay tiêu gì?",
        "Tiền ăn tháng này",
        "Ngân hàng nhiều tiền nhất",
        "Dự báo dòng tiền",
        "Chi phí cố định"
    )

    fun askAi(query: String) {
        if (query.isBlank()) return
        currentAnswer = SmartFinanceAdvisor.answerQuestion(query, accounts, transactions)
        questionInput = ""
        isExpanded = true
        focusManager.clearFocus()
    }

    val statusBg = when (insight.level) {
        InsightLevel.SAFE -> if (isDark) DarkIncomeBg else IncomeGreen.copy(alpha = 0.12f)
        InsightLevel.WARNING -> if (isDark) Color(0xFF2E1C0C) else Color(0xFFFEF3C7)
        InsightLevel.DANGER -> if (isDark) DarkExpenseBg else ExpenseRed.copy(alpha = 0.12f)
    }

    val statusBorder = when (insight.level) {
        InsightLevel.SAFE -> if (isDark) DarkIncomeBorder else IncomeGreen.copy(alpha = 0.25f)
        InsightLevel.WARNING -> if (isDark) Color(0xFF5E3A1A) else Color(0xFFD97706).copy(alpha = 0.25f)
        InsightLevel.DANGER -> if (isDark) DarkExpenseBorder else ExpenseRed.copy(alpha = 0.25f)
    }

    val statusColor = when (insight.level) {
        InsightLevel.SAFE -> if (isDark) DarkIncomeText else IncomeGreen
        InsightLevel.WARNING -> if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706)
        InsightLevel.DANGER -> if (isDark) DarkExpenseText else ExpenseRed
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF1E293B) else SkyBlueSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Trợ lý Tài chính AI Piggy",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isDark) DarkButtonBg else SkyBluePrimary)
                            .border(if (isDark) 1.dp else 0.dp, if (isDark) DarkButtonBorder else Color.Transparent, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Offline",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) DarkTitleBlue else Color.White
                        )
                    }
                }
                Text(
                    text = "Dự báo dòng tiền, phát hiện bất thường & hỏi đáp",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cashflow Insight Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(statusBg)
                .border(1.dp, statusBorder, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = insight.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Duy trì ~${insight.runwayDays} ngày",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = insight.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp
                )
            }
        }

        // Subscriptions / Recurring Alert if detected
        if (recurringItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) Color(0xFF172033) else SkyBlueUltraLight)
                    .border(if (isDark) 1.dp else 0.dp, if (isDark) Color(0xFF2A364F) else Color.Transparent, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI phát hiện ${recurringItems.size} khoản chi định kỳ (tiền nhà, wifi, netflix...)",
                    fontSize = 11.sp,
                    color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // AI Answer Display
        AnimatedVisibility(
            visible = currentAnswer != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            currentAnswer?.let { ans ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) DarkButtonBg else SkyBlueSoft.copy(alpha = 0.5f))
                        .border(1.dp, if (isDark) DarkButtonBorder else SkyBluePrimary.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "💬 \"${ans.question}\"",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { currentAnswer = null },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Đóng",
                                    tint = TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = ans.mainResponse,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (ans.highlightNumber != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ans.highlightNumber,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkTitleBlue else SkyBluePrimary
                            )
                        }

                        if (ans.subNote != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ans.subNote,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Query Suggestions Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            sampleQueries.forEach { q ->
                val chipBg = if (isDark) Color(0xFF1E293B) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                val chipBorder = if (isDark) Color(0xFF334155) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(chipBg)
                        .border(1.dp, chipBorder, RoundedCornerShape(12.dp))
                        .clickable { askAi(q) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = q,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Custom Question Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = questionInput,
                onValueChange = { questionInput = it },
                placeholder = {
                    Text(
                        text = "Hỏi AI về chi tiêu, số dư, dòng tiền...",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { askAi(questionInput) })
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = { askAi(questionInput) },
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) DarkButtonBg else SkyBluePrimary)
                    .border(if (isDark) 1.dp else 0.dp, if (isDark) DarkButtonBorder else Color.Transparent, RoundedCornerShape(14.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Gửi",
                    tint = if (isDark) DarkTitleBlue else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
