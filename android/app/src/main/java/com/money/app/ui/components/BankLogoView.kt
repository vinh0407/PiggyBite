package com.money.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.money.app.model.Bank
import com.money.app.model.BankDirectory
import com.money.app.ui.theme.CardBorder
import com.money.app.ui.theme.SkyBluePrimary

@Composable
fun BankLogoView(
    bankId: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val bank = BankDirectory.findBankById(bankId)
    val bankColor = try {
        Color(android.graphics.Color.parseColor(bank?.colorHex ?: "#0284C7"))
    } catch (e: Exception) {
        SkyBluePrimary
    }

    var isError by remember(bankId) { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size / 3))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(size / 3)),
        contentAlignment = Alignment.Center
    ) {
        if (bank != null && !isError) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    // Logo được đóng gói sẵn trong app (assets/bank_logos) -> không cần mạng
                    .data("file:///android_asset/bank_logos/${bank.id}.png")
                    .crossfade(true)
                    .build(),
                contentDescription = bank.shortName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp),
                onError = {
                    isError = true
                }
            )
        } else {
            // Elegant fallback styled badge with bank brand color and icon
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bankColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (bankId == "momo" || bankId == "zalopay" || bankId == "viettelmoney" || bankId == "vnpay") {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = bank?.shortName ?: "Wallet",
                        tint = bankColor,
                        modifier = Modifier.size(size * 0.55f)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = bank?.shortName ?: "Bank",
                        tint = bankColor,
                        modifier = Modifier.size(size * 0.55f)
                    )
                }
            }
        }
    }
}
