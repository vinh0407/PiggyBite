package com.piggybite.app.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piggybite.app.model.BankAccount
import com.piggybite.app.ui.theme.DarkCardBackground
import com.piggybite.app.ui.theme.DarkCardBorder
import com.piggybite.app.ui.theme.HeroGradientEnd
import com.piggybite.app.ui.theme.HeroGradientMid
import com.piggybite.app.ui.theme.HeroGradientStart
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.PiggyBorderDark
import com.piggybite.app.ui.theme.PiggyBorderLight
import com.piggybite.app.ui.theme.PiggyGreen
import com.piggybite.app.ui.theme.PiggyPrimary
import com.piggybite.app.ui.theme.PiggyRed
import com.piggybite.app.ui.theme.PiggySurfaceDark
import com.piggybite.app.ui.theme.PiggySurfaceLight
import com.piggybite.app.ui.theme.TextMuted
import com.piggybite.app.util.Formatters

@Composable
fun WalletSummaryCard(
    accounts: List<BankAccount>,
    totalBalance: Long,
    isBalanceHidden: Boolean,
    monthIncome: Long = 0L,
    monthExpense: Long = 0L,
    onToggleHideBalance: () -> Unit,
    onAddBank: () -> Unit,
    onEditAccount: (BankAccount) -> Unit,
    onManageAll: () -> Unit,
    onScanQr: () -> Unit = {}
) {
    val isDark = LocalIsDarkMode.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // 1. HERO BALANCE CARD (Figma: balance-card kính mờ trong suốt - Glassmorphic)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(26.dp),
                    spotColor = Color(0xFF071C3D).copy(alpha = 0.45f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF071C3D).copy(alpha = 0.90f),
                            Color(0xFF0B3E70).copy(alpha = 0.86f),
                            Color(0xFF087CBD).copy(alpha = 0.90f)
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    color = Color.White.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(24.dp)
        ) {
            // Decorative Glowing Orbs in background (Hiệu ứng ánh sáng kính mờ)
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                // Orb 1 top-right
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = size.width * 0.45f,
                    center = Offset(size.width * 1.05f, -size.height * 0.2f),
                    style = Stroke(width = 1.2.dp.toPx())
                )
                // Radial soft glow top-right
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width * 0.95f, -size.height * 0.1f),
                        radius = size.width * 0.4f
                    )
                )
                // Orb 2 bottom-right
                drawCircle(
                    color = Color(0xFF15A7EA).copy(alpha = 0.12f),
                    radius = size.width * 0.32f,
                    center = Offset(size.width * 0.85f, size.height * 1.1f)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top row: Kicker + Balance + Eye Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "TỔNG SỐ DƯ KHẢ DỤNG",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA8C2DC),
                            letterSpacing = 1.15.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isBalanceHidden) "•••••••• ₫" else Formatters.formatMoney(totalBalance),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-1.2).sp
                        )
                    }

                    // Eye Toggle Button (Kính mờ trong suốt)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.16f))
                            .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(12.dp))
                            .clickable { onToggleHideBalance() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isBalanceHidden) "Hiện số dư" else "Ẩn số dư",
                            tint = Color(0xFFD2E5F5),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Bottom row: Thu nhập tháng vs Chi tiêu tháng + Nút Quét để thanh toán
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Thu nhập tháng
                    Column {
                        Text(
                            text = "Thu nhập tháng",
                            fontSize = 10.5.sp,
                            color = Color(0xFF9FB8D1)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowUpward,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isBalanceHidden) "•••••• ₫" else Formatters.formatMoney(monthIncome),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Vertical divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(Color.White.copy(alpha = 0.16f))
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    // Chi tiêu tháng
                    Column {
                        Text(
                            text = "Chi tiêu tháng",
                            fontSize = 10.5.sp,
                            color = Color(0xFF9FB8D1)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowDownward,
                                contentDescription = null,
                                tint = Color(0xFFFFBBC2),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isBalanceHidden) "•••••• ₫" else Formatters.formatMoney(monthExpense),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFBBC2)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Nút "Quét để thanh toán" (Kính mờ trong suốt / Frosted Glass Button)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .border(1.dp, Color.White.copy(alpha = 0.38f), RoundedCornerShape(14.dp))
                            .clickable { onScanQr() }
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Quét QR",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 2. VÍ CỦA TÔI (Figma: section-heading + wallet-grid / horizontal scroll)
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Heading
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TÀI KHOẢN",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF8997AA) else TextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Ví của tôi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(
                    modifier = Modifier
                        .clickable { onManageAll() }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quản lý",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = PiggyPrimary
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = PiggyPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal Scroll of Wallet Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Configured Wallets
                accounts.forEach { acc ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
                            .border(
                                1.dp,
                                if (isDark) PiggyBorderDark else PiggyBorderLight,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onEditAccount(acc) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BankLogoView(
                            bankId = acc.bankId,
                            size = 36.dp
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = acc.bankName,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBalanceHidden) "•••••• ₫" else Formatters.formatMoney(acc.balance),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Add Wallet Dashed Button (Figma: wallet-add)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) PiggySurfaceDark.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.7f))
                        .border(
                            1.dp,
                            if (isDark) PiggyBorderDark else PiggyBorderLight,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onAddBank() }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF142E50) else Color(0xFFEAF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Thêm ví",
                            tint = PiggyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(9.dp))

                    Text(
                        text = "Thêm ví",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PiggyPrimary
                    )
                }
            }
        }
    }
}
