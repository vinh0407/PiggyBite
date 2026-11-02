package com.piggybite.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * PiggyBite Premium Modern Minimalist Shapes & Dimensions
 * Material 3 standard hierarchy with rounded organic squircle radii
 */
val PiggyShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

object PiggyRadius {
    val Chip = 10.dp
    val Button = 14.dp
    val TextField = 14.dp
    val Card = 22.dp
    val HeroCard = 26.dp
    val FloatingBar = 36.dp
    val Modal = 28.dp
    val Pill = 999.dp
}

object PiggySpacing {
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
}
