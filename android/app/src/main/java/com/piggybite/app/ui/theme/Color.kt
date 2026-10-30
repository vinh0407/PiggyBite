package com.piggybite.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// LIGHT MODE PALETTE (Toned down, elegant)
// ==========================================
val PureWhite = Color(0xFFFFFFFF)
val ModernBackground = Color(0xFFF7F8F7) // Nền ngà sáng phong cách Nếp
val CardBackground = Color(0xFFFFFFFF)
val CardBorder = Color(0xFFE5E7EB)
val CardBorderLight = Color(0xFFF3F4F6)

// Text Light Mode: Chữ xám đen đậm nét
val TextDeepNavy = Color(0xFF111827)
val TextMain = Color(0xFF1F2937)
val TextMuted = Color(0xFF6B7280) // Phông xám
val TextSub = Color(0xFF9CA3AF)

// Primary Light Mode: Xanh rêu Nếp đặc trưng
val NepGreenPrimary = Color(0xFF1B4332) // Forest Deep Green
val NepGreenHover = Color(0xFF143326)
val NepGreenAccent = Color(0xFF2D6A4F)
val NepGreenSoft = Color(0xFFE8F5E9)
val NepGreenUltraLight = Color(0xFFF1F8F5)

// Backward compatibility alias
val SkyBluePrimary = NepGreenPrimary
val SkyBlueHover = NepGreenHover
val SkyBlueVibrant = NepGreenAccent
val SkyBlueSoft = NepGreenSoft
val SkyBlueUltraLight = NepGreenUltraLight

// ==========================================
// DARK MODE PALETTE: Nền đen tuyền (#000000), còn lại màu xanh đen (#0E1A15), chữ thường & phông màu xám
// ==========================================
val DarkAppBackground = Color(0xFF000000) // Nền chế độ tối hoàn toàn màu đen (True Black)
val DarkCardBackground = Color(0xFF0D1713) // Thẻ & surface màu xanh đen
val DarkCardBorder = Color(0xFF1A2F25)     // Viền màu xanh đen

val DarkTitleBlue = Color(0xFF52B788)     // Màu điểm nhấn xanh ngọc trên nền đen
val DarkTextMain = Color(0xFFEDEDED)      // Chữ chính xám sáng dịu mắt
val DarkTextMuted = Color(0xFFA1A1AA)     // Chữ phụ phông màu xám
val DarkTextSub = Color(0xFF71717A)       // Chữ chú thích phông màu xám trầm

// Accent colors (Light Mode)
val IncomeGreen = Color(0xFF2E7D32)
val IncomeGreenSoft = Color(0xFFE8F5E9)
val ExpenseRed = Color(0xFFDC2626)
val ExpenseRedSoft = Color(0xFFFEF2F2)
val GoldAccent = Color(0xFFD97706)

// ==========================================
// DARK MODE ACCENTS: Xanh đen, chữ xám và đỏ/xanh dịu
// ==========================================
val DarkExpenseBg = Color(0xFF211315)
val DarkExpenseBorder = Color(0xFF3D1C21)
val DarkExpenseText = Color(0xFFE57373) // Đỏ dịu mắt

val DarkIncomeBg = Color(0xFF0C2419)
val DarkIncomeBorder = Color(0xFF18452F)
val DarkIncomeText = Color(0xFF4EBA7E) // Xanh lá dịu mắt

// Nút màu xanh đen trong Dark Mode (theo yêu cầu còn lại màu xanh đen)
val DarkButtonBg = Color(0xFF13281E)       // Nút màu xanh đen
val DarkButtonBorder = Color(0xFF224534)   // Viền nút xanh đen
val DarkButtonIconBg = Color(0xFF1A382A)   // Nền icon xanh đen
val DarkButtonText = Color(0xFFEDEDED)     // Chữ xám sáng

// Backward compatibility aliases
val ForestGreen = NepGreenPrimary
val ForestGreenDark = Color(0xFF0B1914)
val ForestGreenHover = NepGreenHover
val MintLight = NepGreenSoft
val MintSoft = NepGreenSoft
val CreamBackground = ModernBackground
