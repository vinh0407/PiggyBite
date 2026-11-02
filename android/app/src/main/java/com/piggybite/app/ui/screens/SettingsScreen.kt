package com.piggybite.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.piggybite.app.R
import com.piggybite.app.ui.theme.DarkExpenseBg
import com.piggybite.app.ui.theme.DarkExpenseBorder
import com.piggybite.app.ui.theme.DarkExpenseText
import com.piggybite.app.ui.theme.DarkTextMain
import com.piggybite.app.ui.theme.DarkTextMuted
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.PiggyBorderDark
import com.piggybite.app.ui.theme.PiggyBorderLight
import com.piggybite.app.ui.theme.PiggyGreen
import com.piggybite.app.ui.theme.PiggyGreenSoftDark
import com.piggybite.app.ui.theme.PiggyGreenSoftLight
import com.piggybite.app.ui.theme.PiggyPrimary
import com.piggybite.app.ui.theme.PiggyPrimaryDark
import com.piggybite.app.ui.theme.PiggyPrimarySoftDark
import com.piggybite.app.ui.theme.PiggyPrimarySoftLight
import com.piggybite.app.ui.theme.PiggyRed
import com.piggybite.app.ui.theme.PiggySurfaceDark
import com.piggybite.app.ui.theme.PiggySurfaceLight
import com.piggybite.app.ui.theme.PiggySurfaceSoftDark
import com.piggybite.app.ui.theme.PiggySurfaceSoftLight
import com.piggybite.app.ui.theme.TextMuted

@Composable
fun SettingsScreen(
    onOpenNotificationSimulator: () -> Unit = {},
    onResetData: () -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkMode.current

    var hideBalanceOnOpen by remember { mutableStateOf(false) }
    var autoNotifyAlert by remember { mutableStateOf(true) }
    var showEnlargedQr by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Cài đặt & dữ liệu",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Quyền riêng tư và tùy chỉnh trải nghiệm PiggyBite",
                    fontSize = 12.sp,
                    color = if (isDark) DarkTextMuted else TextMuted
                )
            }
        }

        // Direct Download APK Button (Figma: quick download)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(PiggyPrimary)
                    .clickable {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/vinh0407/PiggyBite/raw/main/APK/PiggyBite.apk")
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Không thể mở liên kết: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Tải bản APK Release mới nhất",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Cập nhật trực tiếp từ GitHub LFS",
                            fontSize = 10.5.sp,
                            color = Color(0xFFDCEFFF)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Section 1: Ứng dụng (Figma: settings-section "Ứng dụng")
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
                    .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(22.dp))
                    .padding(18.dp)
            ) {
                // Section Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (isDark) PiggyPrimarySoftDark else PiggyPrimarySoftLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = null,
                            tint = PiggyPrimary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Ứng dụng",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tùy chỉnh giao diện và nhận diện",
                            fontSize = 11.sp,
                            color = if (isDark) DarkTextMuted else TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(if (isDark) PiggyBorderDark else PiggyBorderLight))
                Spacer(modifier = Modifier.height(10.dp))

                // Row 1: Thông báo biến động số dư
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Nhận diện biến động số dư",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Hỗ trợ học và phân tích từ thông báo ngân hàng",
                            fontSize = 11.sp,
                            color = if (isDark) DarkTextMuted else TextMuted
                        )
                    }

                    Switch(
                        checked = autoNotifyAlert,
                        onCheckedChange = { autoNotifyAlert = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PiggyPrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = if (isDark) Color(0xFF2A3A50) else Color(0xFFCBD4DF)
                        )
                    )
                }

                // Row 2: Ẩn số dư khi mở ứng dụng
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ẩn số dư khi mở ứng dụng",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Bảo vệ thông tin tài chính ở nơi công cộng",
                            fontSize = 11.sp,
                            color = if (isDark) DarkTextMuted else TextMuted
                        )
                    }

                    Switch(
                        checked = hideBalanceOnOpen,
                        onCheckedChange = { hideBalanceOnOpen = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PiggyPrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = if (isDark) Color(0xFF2A3A50) else Color(0xFFCBD4DF)
                        )
                    )
                }
            }
        }

        // Section 2: Dữ liệu & Quyền riêng tư (Figma: settings-section "Dữ liệu & quyền riêng tư")
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
                    .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(22.dp))
                    .padding(18.dp)
            ) {
                // Section Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (isDark) PiggyGreenSoftDark else PiggyGreenSoftLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = PiggyGreen,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Dữ liệu & quyền riêng tư",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Toàn bộ dữ liệu được lưu an toàn trên máy",
                            fontSize = 11.sp,
                            color = if (isDark) DarkTextMuted else TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(if (isDark) PiggyBorderDark else PiggyBorderLight))

                // Link: Ủng hộ tác giả ☕
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEnlargedQr = true }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Ủng hộ tác giả một ly cà phê ☕",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Chạm để xem mã QR nhận ủng hộ duy trì app",
                            fontSize = 11.sp,
                            color = PiggyPrimary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = if (isDark) DarkTextMuted else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(0.6.dp).background(if (isDark) PiggyBorderDark else PiggyBorderLight))

                // Link: Đặt lại toàn bộ dữ liệu (Danger Red)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onResetData() }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = PiggyRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Đặt lại toàn bộ dữ liệu",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = PiggyRed
                            )
                            Text(
                                text = "Xóa vĩnh viễn tất cả ví và giao dịch trên máy",
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFFD48B94) else Color(0xFF991B1B)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = PiggyRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Section 3: Offline Banner (Figma: offline-banner)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDark) Color(0xFF0F2923) else Color(0xFFEDF9F4))
                    .border(
                        1.dp,
                        if (isDark) Color(0xFF1A493D) else Color(0xFFCDEAD0),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = PiggyGreen,
                    modifier = Modifier.size(26.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "100% Offline · 100% của bạn",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PiggyGreen
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "PiggyBite không có máy chủ và không thu thập bất kỳ dữ liệu cá nhân nào.",
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF15803D)
                    )
                }
            }
        }

        item {
            Text(
                text = "PiggyBite Kotlin Native v2.2 • Thiết kế tinh tế theo chuẩn Figma",
                fontSize = 11.sp,
                color = if (isDark) DarkTextMuted else TextMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Modal phóng to mã QR ủng hộ
    if (showEnlargedQr) {
        Dialog(onDismissRequest = { showEnlargedQr = false }) {
            Surface(
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
                    Image(
                        painter = painterResource(id = R.drawable.author_qr_card),
                        contentDescription = "Mã QR ủng hộ tác giả",
                        modifier = Modifier
                            .size(260.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Cảm ơn bạn rất nhiều vì đã đồng hành và ủng hộ!",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showEnlargedQr = false },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PiggyPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Đóng", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
