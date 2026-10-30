package com.piggybite.app.ui.dialogs

import android.content.Context
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkButtonBorder
import com.piggybite.app.ui.theme.DarkIncomeBg
import com.piggybite.app.ui.theme.DarkIncomeBorder
import com.piggybite.app.ui.theme.DarkIncomeText
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.ui.theme.TextMuted

data class SampleNotification(val bank: String, val text: String)

@Composable
fun NotificationSimulatorDialog(
    onDismiss: () -> Unit,
    onProcessText: (String) -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkMode.current
    var customText by remember { mutableStateOf("") }

    val samples = remember {
        listOf(
            SampleNotification(
                "Vietcombank (Thu)",
                "VCB: TK 1029384756 +15,000,000 VND luc 10:35. So du 25,500,000 VND. ND: CTY ABC CHUYEN LUONG THANG 10"
            ),
            SampleNotification(
                "Techcombank (Chi)",
                "Techcombank: -55,000 VND luc 09:15. So du 4,945,000 VND. ND: Thanh toan Highlands Coffee Dong Khoi"
            ),
            SampleNotification(
                "MB Bank (Chi)",
                "MB Bank: TK 0988776655 -85,000VND luc 14:20. ND: Grab Chuyen xe GrabCar di lam"
            ),
            SampleNotification(
                "MoMo (Chi)",
                "MoMo: Thanh toán thành công 250.000đ tại Shopee. Đơn hàng quần áo thời trang"
            ),
            SampleNotification(
                "VPBank (Chi)",
                "VPBank: TK 970432xxx -850,000 VND luc 18:00. ND: EVN Thanh toan hoa don tien dien thang 10"
            ),
            SampleNotification(
                "Agribank (Thu)",
                "Agribank: TK 970405xxx +5,000,000VND ngay 28/10/2026. ND: Thuong du an quy 3 xuat sac"
            )
        )
    }

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
                    Column {
                        Text(
                            text = "Tự động nhận thông báo ngân hàng",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tự động nhận diện và cộng tiền vào ngân hàng tương ứng",
                            fontSize = 11.5.sp,
                            color = TextMuted
                        )
                    }

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

                // Service Status Box
                val statusBg = if (isDark) DarkIncomeBg else Color(0xFFF0FDF4)
                val statusBorder = if (isDark) DarkIncomeBorder else Color(0xFFBBF7D0)
                val statusTextColor = if (isDark) DarkIncomeText else Color(0xFF166534)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(statusBg)
                        .border(1.dp, statusBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isDark) DarkIncomeText else Color(0xFF16A34A))
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Trình lắng nghe thông báo: Đang sẵn sàng",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusTextColor
                        )
                        Text(
                            text = "Mỗi khi điện thoại nhận SMS hoặc thông báo từ app ngân hàng, PiggyBite sẽ tự động cộng/trừ tiền tương ứng.",
                            fontSize = 11.5.sp,
                            color = if (isDark) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f) else Color(0xFF15803D),
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Button to open Android Notification Access settings
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) Color(0xFF172033) else Color(0xFFDCFCE7))
                                .border(if (isDark) 1.dp else 0.dp, if (isDark) Color(0xFF2A364F) else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable {
                                    try {
                                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Mở Cài đặt > Quyền truy cập thông báo", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚙️ Cấp quyền đọc thông báo hệ thống",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkTitleBlue else Color(0xFF166534)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "MẪU THÔNG BÁO CHUYỂN TIỀN THỰC TẾ (NHẤP ĐỂ THỬ):",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    samples.take(3).forEach { sample ->
                        val sampleBg = if (isDark) DarkButtonBg else Color(0xFFFAFAF7)
                        val sampleBorder = if (isDark) DarkButtonBorder else MaterialTheme.colorScheme.outline

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(sampleBg)
                                .border(1.dp, sampleBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    onProcessText(sample.text)
                                    onDismiss()
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sample.bank,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = sample.text,
                                    fontSize = 10.5.sp,
                                    color = TextMuted,
                                    maxLines = 1
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDark) Color(0xFF1E293B) else SkyBluePrimary)
                                    .border(if (isDark) 1.dp else 0.dp, if (isDark) Color(0xFF334155) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Thử nhận",
                                    color = if (isDark) DarkTitleBlue else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "HOẶC DÁN NỘI DUNG THÔNG BÁO / TIN NHẮN SMS:",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = customText,
                    onValueChange = { customText = it },
                    placeholder = { Text("Dán thông báo biến động số dư ngân hàng vào đây...", fontSize = 12.sp, color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    minLines = 2,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (customText.isNotBlank()) {
                            onProcessText(customText)
                            onDismiss()
                        }
                    },
                    enabled = customText.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) DarkButtonBg else SkyBluePrimary,
                        contentColor = if (isDark) DarkTitleBlue else Color.White
                    ),
                    border = BorderStroke(1.dp, if (isDark) DarkButtonBorder else Color.Transparent)
                ) {
                    Text(
                        text = "Tự động xử lý & Cập nhật vào ngân hàng",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
