package com.piggybite.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// LIGHT MODE PALETTE (Nền trắng, chữ đen, các ô chữ trắng, nổi bật xanh dương nhạt sáng)
// ==========================================
val PureWhite = Color(0xFFFFFFFF)
val ModernBackground = Color(0xFFFFFFFF) // Nền trắng tinh
val CardBackground = Color(0xFFFFFFFF)   // Các ô chữ trắng
val CardBorder = Color(0xFFE5E7EB)       // Viền ô xám nhạt
val CardBorderLight = Color(0xFFF3F4F6)

// Text Light Mode: Chữ đen
val TextDeepNavy = Color(0xFF111827)     // Chữ đen đậm
val TextMain = Color(0xFF111827)         // Chữ đen
val TextMuted = Color(0xFF6B7280)        // Chữ phụ xám
val TextSub = Color(0xFF9CA3AF)

// Nổi bật trong Light Mode: Xanh dương nhạt sáng
val HighlightBlueLightBg = Color(0xFFE0F2FE)     // Nền xanh dương nhạt sáng
val HighlightBlueLightBorder = Color(0xFFBAE6FD) // Viền xanh dương nhạt
val HighlightBlueLightText = Color(0xFF0284C7)   // Chữ xanh dương nổi bật
val SkyBluePrimary = Color(0xFF0284C7)          // Xanh dương sáng
val SkyBlueHover = Color(0xFF0369A1)
val SkyBlueVibrant = Color(0xFF38BDF8)
val SkyBlueSoft = Color(0xFFE0F2FE)
val SkyBlueUltraLight = Color(0xFFF0F9FF)

// ==========================================
// DARK MODE PALETTE: Nền đen (#000000), chữ xám, các ô chữ xám, ô nổi bật màu xanh dương đen
// ==========================================
val DarkAppBackground = Color(0xFF000000) // Nền chế độ tối màu đen tuyệt đối
val DarkCardBackground = Color(0xFF12161A) // Các ô xám đen
val DarkCardBorder = Color(0xFF21262D)     // Viền xám đen

// Text Dark Mode: Chữ xám
val DarkTextMain = Color(0xFFEDEDED)      // Chữ chính xám sáng
val DarkTextMuted = Color(0xFFA1A1AA)     // Chữ xám
val DarkTextSub = Color(0xFF71717A)       // Chữ phụ xám trầm

// Nổi bật trong Dark Mode: Xanh dương đen (Dark Navy Blue)
val DarkHighlightNavyBg = Color(0xFF0F1B29)     // Nền ô nổi bật màu xanh dương đen
val DarkHighlightNavyBorder = Color(0xFF1E3A5F) // Viền xanh dương đen
val DarkHighlightNavyText = Color(0xFF38BDF8)   // Điểm nhấn chữ xanh dương sáng
val DarkTitleBlue = Color(0xFF38BDF8)           // Chữ nổi bật xanh dương

// Nút màu trong Dark Mode
val DarkButtonBg = Color(0xFF161B22)            // Ô nút xám đen
val DarkButtonBorder = Color(0xFF2A313C)        // Viền nút xám
val DarkButtonIconBg = Color(0xFF1F242C)        // Nền icon
val DarkButtonText = Color(0xFFEDEDED)          // Chữ xám sáng

// Nút nổi bật chính (Chuyển khoản / Quét QR)
val DarkHeroBtnBg = Color(0xFF0C1929)           // Nền nút Hero xanh dương đen
val DarkHeroBtnBorder = Color(0xFF1E3A8A)       // Viền nút Hero xanh dương
val DarkHeroBtnText = Color(0xFFFFFFFF)

// Accent Colors
val IncomeGreen = Color(0xFF10B981)
val IncomeGreenSoft = Color(0xFFECFDF5)
val ExpenseRed = Color(0xFFEF4444)
val ExpenseRedSoft = Color(0xFFFEF2F2)
val GoldAccent = Color(0xFFF59E0B)

val DarkExpenseBg = Color(0xFF1F1315)
val DarkExpenseBorder = Color(0xFF3B1C22)
val DarkExpenseText = Color(0xFFF87171)

val DarkIncomeBg = Color(0xFF0D1E19)
val DarkIncomeBorder = Color(0xFF183B32)
val DarkIncomeText = Color(0xFF34D399)

// Backward compatibility aliases
val NepGreenPrimary = SkyBluePrimary
val NepGreenHover = SkyBlueHover
val NepGreenAccent = SkyBlueVibrant
val NepGreenSoft = SkyBlueSoft
val NepGreenUltraLight = SkyBlueUltraLight
val ForestGreen = SkyBluePrimary
val ForestGreenDark = Color(0xFF000000)
val ForestGreenHover = SkyBlueHover
val MintLight = SkyBlueSoft
val MintSoft = SkyBlueSoft
val CreamBackground = ModernBackground
