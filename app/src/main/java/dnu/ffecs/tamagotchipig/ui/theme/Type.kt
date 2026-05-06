package dnu.ffecs.tamagotchipig.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ))

val TitleText = TextStyle(
    fontSize = 40.sp,
    fontWeight = FontWeight.Bold,
    color = TextDark
)

val ButtonText = TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.SemiBold,
    color = TextDark
)

val LightText = TextStyle(
    fontSize = 15.sp,
    color = TextLight
)
