package com.piggybite.app.ui.dialogs

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.piggybite.app.util.Formatters

@Composable
fun WeeklyReviewDialog(
    onDismiss: () -> Unit,
    onAddManual: () -> Unit
) {
    val isDark = LocalIsDarkMode.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
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
                // Header: Tuần này | Đóng
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tuần này",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary subtitle
                    item {
                        Text(
                            text = "Tuần trước sổ có 23 khoản, 18 khoản PiggyBite ghi giúp.",
                            fontSize = 14.sp,
                            color = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                            lineHeight = 20.sp
                        )
                    }

                    // Card "Ngoài sổ"
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isDark) DarkCardBackground else Color(0xFFF9FAFB))
                                .border(1.dp, if (isDark) DarkCardBorder else Color(0xFFE5E7EB), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Ngoài sổ",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tuần trước chi thêm khoảng bao nhiêu?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ước lượng phần thiếu",
                                    fontSize = 13.sp,
                                    color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                                )
                                Text(
                                    text = "Không bắt buộc",
                                    fontSize = 13.sp,
                                    color = if (isDark) DarkTextSub else Color(0xFF9CA3AF)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(if (isDark) DarkCardBorder else Color(0xFFE5E7EB)))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Ghi từng khoản",
                                    fontSize = 13.sp,
                                    color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                                )
                                Text(
                                    text = "+ Ghi tay",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                    modifier = Modifier.clickable { onAddManual() }
                                )
                            }
                        }
                    }

                    // Card "Chưa rõ danh mục · 3"
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isDark) DarkCardBackground else Color(0xFFF9FAFB))
                                .border(1.dp, if (isDark) DarkCardBorder else Color(0xFFE5E7EB), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Chưa rõ danh mục · 3",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            listOf(
                                Triple("Tiệm Hoa Đầu Ngõ", "Chủ Nhật 18/10", "-80.000đ"),
                                Triple("Chú Tư Sửa Xe", "Thứ Sáu 16/10", "-120.000đ"),
                                Triple("Cô Ba Bánh Mì", "Thứ Tư 14/10", "-35.000đ")
                            ).forEach { (name, date, amt) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = name, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                        Text(text = date, fontSize = 11.5.sp, color = if (isDark) DarkTextSub else Color(0xFF9CA3AF))
                                    }
                                    Text(text = amt, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }

                    // Total spending
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) DarkCardBackground else Color.White)
                                .border(1.dp, if (isDark) DarkCardBorder else Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tổng chi tuần trước",
                                fontSize = 14.sp,
                                color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                            )
                            Text(
                                text = "1.842.000đ",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Budget warning insight
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color(0xFF261914) else Color(0xFFFFF7ED))
                                .border(1.dp, if (isDark) Color(0xFF4A2B1D) else Color(0xFFFED7AA), RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) Color(0xFF381F15) else Color(0xFFFFEDD5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalCafe,
                                    contentDescription = null,
                                    tint = Color(0xFFF97316),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Cà phê",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFFDBA74) else Color(0xFFC2410C)
                                )
                                Text(
                                    text = "Vượt hạn mức tháng 10 · đã chi 825.000đ, hạn mức 700.000đ",
                                    fontSize = 11.5.sp,
                                    color = if (isDark) DarkTextMuted else Color(0xFF9A3412)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Khép sổ (Green) | Tạm nghỉ
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NepGreenPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Khép sổ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Tạm nghỉ",
                        fontSize = 13.5.sp,
                        color = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
