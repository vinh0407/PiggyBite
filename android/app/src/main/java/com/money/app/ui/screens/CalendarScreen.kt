package com.money.app.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.money.app.model.Transaction
import com.money.app.ui.theme.DarkExpenseText
import com.money.app.ui.theme.DarkIncomeText
import com.money.app.ui.theme.DarkTitleBlue
import com.money.app.ui.theme.ExpenseRed
import com.money.app.ui.theme.IncomeGreen
import com.money.app.ui.theme.LocalIsDarkMode
import com.money.app.ui.theme.SkyBluePrimary
import com.money.app.ui.theme.TextMuted
import com.money.app.util.Formatters
import java.util.Calendar

@Composable
fun CalendarScreen(
    transactions: List<Transaction>
) {
    val isDark = LocalIsDarkMode.current

    var selectedDay by remember {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        mutableIntStateOf(today)
    }

    val currentMonthYear = remember {
        val cal = Calendar.getInstance()
        "Tháng ${cal.get(Calendar.MONTH) + 1}, ${cal.get(Calendar.YEAR)}"
    }

    val daysInMonth = remember {
        val cal = Calendar.getInstance()
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Text(
            text = "Lịch chi tiêu",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Month selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentMonthYear,
                fontSize = 16.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) DarkTitleBlue else SkyBluePrimary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Calendar Days Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            val rows = (daysInMonth + 6) / 7
            for (r in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (c in 0 until 7) {
                        val dayNum = r * 7 + c + 1
                        if (dayNum <= daysInMonth) {
                            val isSelected = selectedDay == dayNum
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) SkyBluePrimary else Color.Transparent)
                                    .clickable { selectedDay = dayNum },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$dayNum",
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(38.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Giao dịch ngày $selectedDay",
            fontSize = 16.5.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { tx ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tx.note.ifBlank { tx.category },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${tx.bankName} • ${Formatters.formatDate(tx.timestamp)}",
                            fontSize = 11.5.sp,
                            color = TextMuted
                        )
                    }

                    val txColor = if (isDark) {
                        if (tx.isIncome) DarkIncomeText else DarkExpenseText
                    } else {
                        if (tx.isIncome) IncomeGreen else ExpenseRed
                    }

                    Text(
                        text = if (tx.isIncome) "+${Formatters.formatMoney(tx.amount)}" else "−${Formatters.formatMoney(tx.amount)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = txColor
                    )
                }
            }
        }
    }
}
