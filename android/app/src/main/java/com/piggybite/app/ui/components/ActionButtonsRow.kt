package com.piggybite.app.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.piggybite.app.ui.theme.CardBackground
import com.piggybite.app.ui.theme.CardBorder
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkButtonBorder
import com.piggybite.app.ui.theme.DarkButtonIconBg
import com.piggybite.app.ui.theme.DarkButtonText
import com.piggybite.app.ui.theme.DarkCardBackground
import com.piggybite.app.ui.theme.DarkCardBorder
import com.piggybite.app.ui.theme.DarkHighlightNavyBg
import com.piggybite.app.ui.theme.DarkHighlightNavyBorder
import com.piggybite.app.ui.theme.DarkHighlightNavyText
import com.piggybite.app.ui.theme.DarkTextMain
import com.piggybite.app.ui.theme.DarkTextMuted
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.HighlightBlueLightBg
import com.piggybite.app.ui.theme.HighlightBlueLightBorder
import com.piggybite.app.ui.theme.HighlightBlueLightText
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.ui.theme.SkyBlueSoft
import com.piggybite.app.ui.theme.TextDeepNavy
import com.piggybite.app.ui.theme.TextMuted

@Composable
fun ActionButtonsRow(
    onScanQr: () -> Unit,
    onPasteStk: () -> Unit,
    onAddManual: () -> Unit,
    onMyQr: () -> Unit,
    onOpenNotificationSimulator: () -> Unit = {}
) {
    val isDark = LocalIsDarkMode.current
    var showMethodChoiceDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Nút chính: "Chuyển khoản ngân hàng" (Nổi bật: Xanh dương đen ở Dark mode, Xanh dương ở Light mode)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(6.dp, RoundedCornerShape(22.dp), spotColor = if (isDark) Color(0xFF0F1B29) else SkyBluePrimary.copy(alpha = 0.35f))
                .clip(RoundedCornerShape(22.dp))
                .background(
                    if (isDark) {
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0F1B29), Color(0xFF1E3A5F))
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0284C7), Color(0xFF0369A1))
                        )
                    }
                )
                .border(
                    1.dp,
                    if (isDark) Color(0xFF2563EB) else Color.Transparent,
                    RoundedCornerShape(22.dp)
                )
                .clickable { showMethodChoiceDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFF38BDF8) else Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Chuyển khoản ngân hàng",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hàng nút phụ: "+ Ghi tay" & "Mã QR nhận"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // + Ghi tay
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDark) DarkButtonBg else CardBackground)
                    .border(1.dp, if (isDark) DarkButtonBorder else CardBorder, RoundedCornerShape(18.dp))
                    .clickable { onAddManual() }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isDark) DarkButtonIconBg else SkyBlueSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ghi tay",
                    color = if (isDark) DarkButtonText else TextDeepNavy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // QR nhận
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDark) DarkButtonBg else CardBackground)
                    .border(1.dp, if (isDark) DarkButtonBorder else CardBorder, RoundedCornerShape(18.dp))
                    .clickable { onMyQr() }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isDark) DarkButtonIconBg else SkyBlueSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mã QR nhận",
                    color = if (isDark) DarkButtonText else TextDeepNavy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    // Modal chọn 2 option: "Quét mã QR" hoặc "Dán STK"
    if (showMethodChoiceDialog) {
        Dialog(onDismissRequest = { showMethodChoiceDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = if (isDark) DarkCardBackground else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkCardBorder else CardBorder),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(22.dp)
                ) {
                    Text(
                        text = "Chuyển khoản ngân hàng",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Chọn phương thức nhận diện thông tin người nhận",
                        fontSize = 12.5.sp,
                        color = if (isDark) DarkTextMuted else TextMuted
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Option 1: Quét mã QR
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) DarkButtonBg else Color(0xFFF9FAFB))
                            .border(1.dp, if (isDark) DarkButtonBorder else CardBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                showMethodChoiceDialog = false
                                onScanQr()
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isDark) DarkHighlightNavyBg else SkyBlueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Quét mã QR",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Hướng camera vào mã VietQR hoặc tải từ thư viện",
                                fontSize = 11.5.sp,
                                color = if (isDark) DarkTextMuted else TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option 2: Dán số tài khoản (STK)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) DarkButtonBg else Color(0xFFF9FAFB))
                            .border(1.dp, if (isDark) DarkButtonBorder else CardBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                showMethodChoiceDialog = false
                                onPasteStk()
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isDark) DarkHighlightNavyBg else SkyBlueSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = null,
                                tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Dán số tài khoản",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tự động nhận diện STK và ngân hàng ngay khi dán",
                                fontSize = 11.5.sp,
                                color = if (isDark) DarkTextMuted else TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
