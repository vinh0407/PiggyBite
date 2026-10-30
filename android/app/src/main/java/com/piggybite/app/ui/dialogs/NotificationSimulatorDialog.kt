package com.piggybite.app.ui.dialogs

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NotificationsActive
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.piggybite.app.model.NotificationCategory
import com.piggybite.app.model.ReceivedNotification
import com.piggybite.app.ui.components.BankLogoView
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
import com.piggybite.app.ui.theme.IncomeGreen
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.ui.theme.SkyBlueSoft
import com.piggybite.app.ui.theme.TextMuted
import com.piggybite.app.util.Formatters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class NotificationFilter {
    ALL,
    INCOME,
    EXPENSE,
    PROMO
}

@Composable
fun NotificationSimulatorDialog(
    notifications: List<ReceivedNotification>,
    onDismiss: () -> Unit,
    onProcessText: (String) -> Unit,
    onClearAll: () -> Unit,
    onDeleteItem: (String) -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkMode.current

    var activeFilter by remember { mutableStateOf(NotificationFilter.ALL) }
    var inputPasteText by remember { mutableStateOf("") }

    val filteredNotifications = remember(notifications, activeFilter) {
        when (activeFilter) {
            NotificationFilter.ALL -> notifications
            NotificationFilter.INCOME -> notifications.filter { it.category == NotificationCategory.INCOME }
            NotificationFilter.EXPENSE -> notifications.filter { it.category == NotificationCategory.EXPENSE }
            NotificationFilter.PROMO -> notifications.filter { it.category == NotificationCategory.PROMO_OR_ADS }
        }
    }

    val incomeCount = remember(notifications) { notifications.count { it.category == NotificationCategory.INCOME } }
    val expenseCount = remember(notifications) { notifications.count { it.category == NotificationCategory.EXPENSE } }
    val promoCount = remember(notifications) { notifications.count { it.category == NotificationCategory.PROMO_OR_ADS } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(top = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isDark) DarkButtonBg else SkyBlueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Lịch sử thông báo biến động",
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${notifications.size} thông báo đã nhận diện từ ngân hàng",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Service Status Box & Permission button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) DarkIncomeBg else Color(0xFFF0FDF4))
                        .border(1.dp, if (isDark) DarkIncomeBorder else Color(0xFFBBF7D0), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isDark) DarkIncomeText else Color(0xFF16A34A))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lắng nghe SMS & biến động số dư: Sẵn sàng",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) DarkIncomeText else Color(0xFF166534)
                        )
                    }

                    Text(
                        text = "Cài đặt ›",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        modifier = Modifier
                            .clickable {
                                try {
                                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Filter Tabs (Tất cả, Tiền vào, Tiền ra, Quảng cáo)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tabs = listOf(
                        NotificationFilter.ALL to "Tất cả (${notifications.size})",
                        NotificationFilter.INCOME to "+ Nhận ($incomeCount)",
                        NotificationFilter.EXPENSE to "- Chuyển ($expenseCount)",
                        NotificationFilter.PROMO to "📢 Tin rác ($promoCount)"
                    )

                    tabs.forEach { (filter, label) ->
                        val isSelected = activeFilter == filter
                        val tabBg = if (isSelected) {
                            if (isDark) DarkButtonBg else SkyBlueSoft
                        } else {
                            if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                        }
                        val tabText = if (isSelected) {
                            if (isDark) DarkTitleBlue else SkyBluePrimary
                        } else {
                            TextMuted
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(tabBg)
                                .border(
                                    1.dp,
                                    if (isSelected) (if (isDark) DarkTitleBlue else SkyBluePrimary) else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { activeFilter = filter }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = tabText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notifications List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (filteredNotifications.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "📭", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Không có thông báo nào trong mục này",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextMuted
                                )
                                Text(
                                    text = "Khi điện thoại nhận SMS hoặc thông báo từ ngân hàng, PiggyBite sẽ tự động phân loại tại đây.",
                                    fontSize = 11.5.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(top = 4.dp, start = 20.dp, end = 20.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(filteredNotifications, key = { it.id }) { notif ->
                            NotificationItemCard(
                                notif = notif,
                                isDark = isDark,
                                onDelete = { onDeleteItem(notif.id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Paste / Manual Recognition Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(10.dp)
                ) {
                    OutlinedTextField(
                        value = inputPasteText,
                        onValueChange = { inputPasteText = it },
                        placeholder = {
                            Text(
                                "Dán thông báo biến động hoặc SMS ngân hàng vào đây để thử nhận diện...",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (notifications.isNotEmpty()) {
                            Text(
                                text = "Xóa lịch sử",
                                fontSize = 12.sp,
                                color = if (isDark) DarkExpenseText else ExpenseRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { onClearAll() }
                                    .padding(vertical = 4.dp, horizontal = 6.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Button(
                            onClick = {
                                if (inputPasteText.isNotBlank()) {
                                    onProcessText(inputPasteText.trim())
                                    inputPasteText = ""
                                    Toast.makeText(context, "Đã xử lý và phân loại thông báo!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = inputPasteText.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) DarkButtonBg else SkyBluePrimary,
                                contentColor = if (isDark) DarkTitleBlue else Color.White
                            ),
                            border = BorderStroke(1.dp, if (isDark) DarkButtonBorder else Color.Transparent)
                        ) {
                            Text("Nhận diện thông báo", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItemCard(
    notif: ReceivedNotification,
    isDark: Boolean,
    onDelete: () -> Unit
) {
    val timeFormatted = remember(notif.timestamp) {
        val sdf = SimpleDateFormat("HH:mm • dd/MM", Locale.getDefault())
        sdf.format(Date(notif.timestamp))
    }

    val cardBorder = when (notif.category) {
        NotificationCategory.INCOME -> if (isDark) DarkIncomeBorder else Color(0xFFBBF7D0)
        NotificationCategory.EXPENSE -> if (isDark) DarkExpenseBorder else Color(0xFFFECACA)
        NotificationCategory.PROMO_OR_ADS -> if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    }

    val badgeBg = when (notif.category) {
        NotificationCategory.INCOME -> if (isDark) DarkIncomeBg else Color(0xFFDCFCE7)
        NotificationCategory.EXPENSE -> if (isDark) DarkExpenseBg else ExpenseRedSoft
        NotificationCategory.PROMO_OR_ADS -> if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9)
    }

    val badgeColor = when (notif.category) {
        NotificationCategory.INCOME -> if (isDark) DarkIncomeText else IncomeGreen
        NotificationCategory.EXPENSE -> if (isDark) DarkExpenseText else ExpenseRed
        NotificationCategory.PROMO_OR_ADS -> if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    }

    val badgeLabel = when (notif.category) {
        NotificationCategory.INCOME -> "+ Tiền vào"
        NotificationCategory.EXPENSE -> "- Tiền ra"
        NotificationCategory.PROMO_OR_ADS -> "📢 Quảng cáo / Bỏ qua"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDark) Color(0xFF1E293B) else Color.White)
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        // Top row: Bank, category badge, time & delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BankLogoView(bankId = notif.bankId, size = 26.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = notif.bankName,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Category Badge
                Text(
                    text = badgeLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Xóa",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Amount & Summary
        if (notif.amount > 0) {
            val amountFormatted = (if (notif.category == NotificationCategory.INCOME) "+" else "-") + Formatters.formatMoney(notif.amount)
            Text(
                text = amountFormatted,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (notif.category == NotificationCategory.INCOME) (if (isDark) DarkIncomeText else IncomeGreen) else (if (isDark) DarkExpenseText else ExpenseRed)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Text(
            text = notif.summary,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Raw message quote
        Text(
            text = "\"${notif.rawText}\"",
            fontSize = 11.5.sp,
            color = TextMuted,
            lineHeight = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Timestamp and Auto-action explanation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val statusNote = when (notif.category) {
                NotificationCategory.INCOME -> "✓ Tự động cộng số dư vào ví"
                NotificationCategory.EXPENSE -> "✓ Tự động trừ số dư trong ví"
                NotificationCategory.PROMO_OR_ADS -> "Bỏ qua: Không thay đổi số dư"
            }

            Text(
                text = statusNote,
                fontSize = 10.5.sp,
                color = badgeColor,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = timeFormatted,
                fontSize = 10.5.sp,
                color = TextMuted
            )
        }
    }
}
