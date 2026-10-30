package com.piggybite.app.ui.dialogs

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.compose.foundation.BorderStroke
import com.piggybite.app.model.BankAccount
import com.piggybite.app.ui.components.BankLogoView
import com.piggybite.app.ui.theme.CardBorder
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkButtonBorder
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.ui.theme.SkyBlueSoft
import com.piggybite.app.ui.theme.TextDeepNavy
import com.piggybite.app.ui.theme.TextMuted
import java.io.File
import java.io.FileOutputStream

@Composable
fun MyQrDialog(
    accounts: List<BankAccount>,
    onSaveAccountQr: (accountId: String, qrPath: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkMode.current
    var selectedAccount by remember { mutableStateOf(accounts.firstOrNull()) }

    // Launcher to pick image from photo gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && selectedAccount != null) {
            val acc = selectedAccount!!
            val savedPath = saveImageToInternalStorage(context, uri, acc.id)
            if (savedPath != null) {
                onSaveAccountQr(acc.id, savedPath)
                // Refresh local state
                selectedAccount = acc.copy(qrImagePath = savedPath)
                Toast.makeText(context, "Đã lưu ảnh mã QR cho ${acc.bankName} vĩnh viễn!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Mã QR nhận tiền của tôi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Chọn ngân hàng để xem hoặc thêm ảnh QR",
                            fontSize = 12.sp,
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

                if (accounts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🏦", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Chưa có tài khoản ngân hàng nào",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Vui lòng thêm tài khoản ngân hàng tại trang Ví trước khi cài đặt ảnh mã QR nhận tiền.",
                                fontSize = 12.5.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Account Tabs with Official Bank Logos
                    Text(
                        text = "NGÂN HÀNG BẠN ĐANG CÓ:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(accounts) { acc ->
                            val isSelected = selectedAccount?.id == acc.id
                            val tabBg = if (isSelected) {
                                if (isDark) DarkButtonBg else SkyBlueSoft
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                            val tabBorder = if (isSelected) {
                                if (isDark) DarkTitleBlue else SkyBluePrimary
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
                            val tabText = if (isSelected) {
                                if (isDark) DarkTitleBlue else SkyBluePrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(tabBg)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = tabBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedAccount = acc }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BankLogoView(bankId = acc.bankId, size = 22.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = acc.bankName,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = tabText
                                )
                            }
                        }
                    }

                Spacer(modifier = Modifier.height(16.dp))

                val currentAcc = selectedAccount
                val savedQrFile = currentAcc?.qrImagePath?.let { File(it) }

                // QR Display Box
                if (savedQrFile != null && savedQrFile.exists()) {
                    // Show Saved Permanent QR Photo
                    Box(
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = savedQrFile,
                            contentDescription = "Ảnh mã QR ngân hàng",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Change Photo Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) DarkButtonBg else SkyBlueSoft)
                            .border(
                                if (isDark) 1.dp else 0.dp,
                                if (isDark) DarkButtonBorder else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { galleryLauncher.launch("image/*") }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Đổi ảnh mã QR khác",
                            color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // No photo saved yet -> Prompt to choose from photo gallery
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                            .clickable { galleryLauncher.launch("image/*") }
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) Color(0xFF1E293B) else SkyBlueSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Thêm ảnh",
                                    tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Tải ảnh mã QR ${currentAcc?.bankName ?: ""}",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Chọn ảnh mã QR từ kho ảnh trên máy.\nLần sau mở app sẽ tự động hiển thị sẵn vĩnh viễn.",
                                fontSize = 12.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
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
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Chọn ảnh mã QR từ kho ảnh", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (currentAcc != null && currentAcc.accountNumber.isNotBlank()) {
                    Text(
                        text = "STK: ${currentAcc.accountNumber} • ${currentAcc.bankName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                }
            }
        }
    }
}

private fun saveImageToInternalStorage(context: Context, uri: Uri, accountId: String): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val targetFile = File(context.filesDir, "qr_bank_${accountId}.jpg")
        val outputStream = FileOutputStream(targetFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        targetFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
