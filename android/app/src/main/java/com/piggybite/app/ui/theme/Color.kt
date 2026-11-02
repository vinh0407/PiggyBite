package com.piggybite.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// PIGGYBITE REFINED DESIGN SYSTEM PALETTE
// Inspired by Figma UI: Soft Slate & Ocean Blue / Deep OLED Navy
// ==========================================

// LIGHT MODE PALETTE (--bg: #f4f7fb, --surface: #ffffff, --primary: #0e85d4)
val PiggyBgLight = Color(0xFFF4F7FB)             // Nền xám xanh nhạt siêu sạch
val PiggySurfaceLight = Color(0xFFFFFFFF)        // Nền thẻ trắng tinh
val PiggySurfaceSoftLight = Color(0xFFF7F9FC)    // Ô nhập / Thẻ phụ mềm mại
val PiggySurfaceHoverLight = Color(0xFFF0F4F9)
val PiggyBorderLight = Color(0xFFE5EAF1)         // Viền thẻ xám thanh lịch
val PiggyTextLight = Color(0xFF142139)           // Chữ chính xanh đen đậm (Deep Slate)
val PiggyMutedLight = Color(0xFF7C899E)          // Chữ phụ xám xanh trung tính
val PiggySubtleLight = Color(0xFFA4AFBF)         // Chữ ghi chú xám nhạt

// DARK MODE PALETTE (--bg: #050a12, --surface: #0d1522, --border: #1e2b3e)
val PiggyBgDark = Color(0xFF050A12)              // Nền OLED đen xanh thẫm
val PiggySurfaceDark = Color(0xFF0D1522)         // Nền thẻ xanh đen sâu
val PiggySurfaceSoftDark = Color(0xFF111C2B)     // Ô phụ xanh đen
val PiggySurfaceHoverDark = Color(0xFF172335)
val PiggyBorderDark = Color(0xFF1E2B3E)          // Viền thẻ tối tinh tế
val PiggyTextDark = Color(0xFFEDF4FD)            // Chữ chính trắng sáng
val PiggyMutedDark = Color(0xFF8997AA)           // Chữ phụ xám nhạt
val PiggySubtleDark = Color(0xFF5D6C80)          // Chữ phụ trầm

// BRAND ACCENTS
val PiggyPrimary = Color(0xFF0E85D4)             // Xanh dương đại dương chủ đạo
val PiggyPrimaryDark = Color(0xFF0871BD)
val PiggyPrimarySoftLight = Color(0xFFEAF6FF)
val PiggyPrimarySoftDark = Color(0xFF0E2940)
val PiggyNavy = Color(0xFF0B2347)
val PiggyNavyDark = Color(0xFF08172E)

// SEMANTIC COLORS
val PiggyGreen = Color(0xFF17A673)               // Thu nhập / Thành công
val PiggyGreenSoftLight = Color(0xFFE9F8F1)
val PiggyGreenSoftDark = Color(0xFF14352B)

val PiggyRed = Color(0xFFE45062)                 // Chi tiêu / Lỗi cảnh báo
val PiggyRedSoftLight = Color(0xFFFFEAEE)
val PiggyRedSoftDark = Color(0xFF3C1F29)

val PiggyOrange = Color(0xFFDC7A2C)              // Cà phê / Ăn uống / Cảnh báo
val PiggyOrangeSoftLight = Color(0xFFFFF0E4)
val PiggyOrangeSoftDark = Color(0xFF3B281C)

val PiggyViolet = Color(0xFF7B54D9)              // Mua sắm / AI Magic
val PiggyVioletSoftLight = Color(0xFFF0EAFF)
val PiggyVioletSoftDark = Color(0xFF261E42)

val PiggyAmber = Color(0xFFF59E0B)
val PiggyAmberSoftLight = Color(0xFFFFF3D9)
val PiggyAmberSoftDark = Color(0xFF392D15)

// HERO CARD GRADIENT
val HeroGradientStart = Color(0xFF071C3D)
val HeroGradientMid = Color(0xFF0B3E70)
val HeroGradientEnd = Color(0xFF087CBD)

// BACKWARD COMPATIBILITY ALIASES
val PureWhite = Color(0xFFFFFFFF)
val ModernBackground = PiggyBgLight
val CardBackground = PiggySurfaceLight
val CardBorder = PiggyBorderLight
val CardBorderLight = PiggySurfaceSoftLight

val TextDeepNavy = PiggyTextLight
val TextMain = PiggyTextLight
val TextMuted = PiggyMutedLight
val TextSub = PiggySubtleLight

val HighlightBlueLightBg = PiggyPrimarySoftLight
val HighlightBlueLightBorder = Color(0xFFBAE6FD)
val HighlightBlueLightText = PiggyPrimary
val SkyBluePrimary = PiggyPrimary
val SkyBlueHover = PiggyPrimaryDark
val SkyBlueVibrant = Color(0xFF38BDF8)
val SkyBlueSoft = PiggyPrimarySoftLight
val SkyBlueUltraLight = Color(0xFFF0F9FF)

val DarkAppBackground = PiggyBgDark
val DarkCardBackground = PiggySurfaceDark
val DarkCardBorder = PiggyBorderDark

val DarkTextMain = PiggyTextDark
val DarkTextMuted = PiggyMutedDark
val DarkTextSub = PiggySubtleDark

val DarkHighlightNavyBg = PiggyPrimarySoftDark
val DarkHighlightNavyBorder = Color(0xFF1E3A5F)
val DarkHighlightNavyText = Color(0xFF38BDF8)
val DarkTitleBlue = Color(0xFF38BDF8)

val DarkButtonBg = PiggySurfaceSoftDark
val DarkButtonBorder = PiggyBorderDark
val DarkButtonIconBg = Color(0xFF1F242C)
val DarkButtonText = PiggyTextDark

val DarkHeroBtnBg = Color(0xFF0C1929)
val DarkHeroBtnBorder = Color(0xFF1E3A8A)
val DarkHeroBtnText = Color(0xFFFFFFFF)

val IncomeGreen = PiggyGreen
val IncomeGreenSoft = PiggyGreenSoftLight
val ExpenseRed = PiggyRed
val ExpenseRedSoft = PiggyRedSoftLight
val GoldAccent = PiggyAmber

val DarkExpenseBg = PiggyRedSoftDark
val DarkExpenseBorder = Color(0xFF4C1D27)
val DarkExpenseText = PiggyRed

val DarkIncomeBg = PiggyGreenSoftDark
val DarkIncomeBorder = Color(0xFF183B32)
val DarkIncomeText = PiggyGreen

val NepGreenPrimary = PiggyPrimary
val NepGreenHover = PiggyPrimaryDark
val NepGreenAccent = SkyBlueVibrant
val NepGreenSoft = PiggyPrimarySoftLight
val NepGreenUltraLight = SkyBlueUltraLight
val ForestGreen = PiggyPrimary
val ForestGreenDark = Color(0xFF000000)
val ForestGreenHover = PiggyPrimaryDark
val MintLight = PiggyPrimarySoftLight
val MintSoft = PiggyPrimarySoftLight
val CreamBackground = ModernBackground

