package com.piggybite.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piggybite.app.data.NotificationEvent
import com.piggybite.app.ui.theme.ExpenseRed
import com.piggybite.app.ui.theme.ExpenseRedSoft
import com.piggybite.app.ui.theme.IncomeGreen
import com.piggybite.app.ui.theme.IncomeGreenSoft
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.ui.theme.SkyBlueSoft
import com.piggybite.app.ui.theme.TextDeepNavy
import com.piggybite.app.ui.theme.TextMuted
import com.piggybite.app.util.Formatters

@Composable
fun FloatingNotificationToast(
    event: NotificationEvent?,
    onDismiss: () -> Unit,
    onConfirm: (Boolean) -> Unit = {}
) {
    AnimatedVisibility(
        visible = event != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        if (event != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(22.dp), spotColor = SkyBluePrimary.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .border(1.5.dp, SkyBluePrimary, RoundedCornerShape(22.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "🔔",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp, top = 2.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = event.bankName,
                                color = TextDeepNavy,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (event.isIncome) IncomeGreenSoft else ExpenseRedSoft)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (event.isIncome) "+${Formatters.formatMoney(event.amount)}" else "−${Formatters.formatMoney(event.amount)}",
                                    color = if (event.isIncome) IncomeGreen else ExpenseRed,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // AI Classification Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SkyBlueSoft)
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "✨ AI tự phân loại: ${event.aiCategory}",
                                    color = SkyBluePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (event.isIncome) {
                                "Đã tự động cộng vào tài khoản ${event.bankName}. Số dư mới: ${Formatters.formatMoney(event.newBalance)}"
                            } else {
                                "Đã tự động trừ từ tài khoản ${event.bankName}. Số dư mới: ${Formatters.formatMoney(event.newBalance)}"
                            },
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Interactive Confirmation & Training prompt
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0FDF4))
                                .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "🤖 AI hỏi: Đây có phải thông báo giao dịch không?",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(IncomeGreen)
                                        .clickable {
                                            onConfirm(true)
                                            onDismiss()
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✓ Đúng (Lưu giao dịch)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                        .clickable {
                                            onConfirm(false)
                                            onDismiss()
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✕ Không (Bỏ qua)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
