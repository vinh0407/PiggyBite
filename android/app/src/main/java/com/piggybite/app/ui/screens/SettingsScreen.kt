package com.piggybite.app.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.piggybite.app.ui.theme.DarkExpenseBg
import com.piggybite.app.ui.theme.DarkExpenseBorder
import com.piggybite.app.ui.theme.DarkExpenseText
import com.piggybite.app.ui.theme.DarkIncomeBg
import com.piggybite.app.ui.theme.DarkIncomeText
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.ui.theme.SkyBlueSoft
import com.piggybite.app.ui.theme.TextMuted

@Composable
fun SettingsScreen(
    onOpenNotificationSimulator: () -> Unit,
    onResetData: () -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkMode.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Cài đặt & Hệ thống",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Cấu hình quyền thông báo, chế độ tối và trí tuệ nhân tạo",
                fontSize = 13.sp,
                color = TextMuted
            )
        }

        // Direct Download APK AT THE TOP
        item {
            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://github.com/vinh0407/PiggyBite/raw/main/APK/PiggyBite.apk"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Không thể mở liên kết: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) com.piggybite.app.ui.theme.DarkButtonBg else SkyBluePrimary,
                    contentColor = if (isDark) DarkTitleBlue else Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) com.piggybite.app.ui.theme.DarkButtonBorder else SkyBluePrimary
                )
            ) {
                Text(
                    text = "Tải & Cài đặt file APK mới nhất (Android)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp
                )
            }
        }

        // Settings items
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // AI Self-learning status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Trí tuệ nhân tạo AI Phân loại thu nhập",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tự động học và huấn luyện (Self-training) từ SMS / thông báo chuyển tiền",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = "Tự học ✓",
                        color = if (isDark) DarkIncomeText else SkyBluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) DarkIncomeBg else SkyBlueSoft)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Notification Access Settings Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            com.piggybite.app.service.NotificationAccessHelper.openSettings(context)
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Quyền đọc thông báo ngân hàng",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Cho phép app tự động nhận diện SMS & biến động số dư các ngân hàng",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = "Cấp quyền ›",
                        color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Offline mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Chế độ lưu trữ thiết bị",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "100% Offline trên máy, bảo mật số dư và ảnh mã QR cục bộ",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = "An toàn 🔒",
                        color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0xFF1E293B) else SkyBlueSoft)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Donate / Support Author Section with Small QR
        item {
            var showEnlargedQr by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "☕ Ủng hộ tác giả",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "MoMo / VietQR",
                        fontSize = 11.5.sp,
                        color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) com.piggybite.app.ui.theme.DarkButtonBg else SkyBlueSoft)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Nếu PiggyBite giúp ích cho bạn trong việc quản lý tài chính, hãy ủng hộ tác giả một ly cà phê để tiếp tục duy trì và nâng cấp ứng dụng nhé!",
                    fontSize = 12.5.sp,
                    color = TextMuted,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Small QR Code Thumbnail (click to view larger)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(
                            1.5.dp,
                            if (isDark) DarkTitleBlue else SkyBluePrimary.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { showEnlargedQr = true }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = com.piggybite.app.R.drawable.author_qr_card),
                        contentDescription = "Mã QR ủng hộ tác giả",
                        modifier = Modifier.size(130.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Chạm vào mã QR để phóng to",
                    fontSize = 11.5.sp,
                    color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (showEnlargedQr) {
                Dialog(onDismissRequest = { showEnlargedQr = false }) {
                    androidx.compose.material3.Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ủng hộ tác giả PiggyBite ☕",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = com.piggybite.app.R.drawable.author_qr_card),
                                contentDescription = "Mã QR ủng hộ tác giả",
                                modifier = Modifier
                                    .size(260.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Cảm ơn bạn rất nhiều vì đã đồng hành và ủng hộ!",
                                fontSize = 12.5.sp,
                                color = Color(0xFF64748B),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showEnlargedQr = false },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDark) com.piggybite.app.ui.theme.DarkButtonBg else SkyBluePrimary,
                                    contentColor = if (isDark) DarkTitleBlue else Color.White
                                )
                            ) {
                                Text("Đóng", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Danger zone: Reset data
        item {
            Button(
                onClick = onResetData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) DarkExpenseBg else Color(0xFFFEF2F2),
                    contentColor = if (isDark) DarkExpenseText else Color(0xFFEF4444)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) DarkExpenseBorder else Color(0xFFFECACA)
                )
            ) {
                Text(
                    text = "Xóa toàn bộ dữ liệu & Đặt lại",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        item {
            Text(
                text = "PiggyBite Kotlin Native v2.2 • Heo Đất Thông Minh & AI On-Device",
                fontSize = 11.5.sp,
                color = TextMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
