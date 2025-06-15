package values.storetheme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Replace with your own font assets if you have them
// val CustomFontFamily = FontFamily(...)

val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override as needed:
    headlineLarge = TextStyle(...),
    headlineMedium = TextStyle(...),
    headlineSmall = TextStyle(...),
    titleMedium = TextStyle(...),
    titleSmall = TextStyle(...),
    bodyMedium = TextStyle(...),
    bodySmall = TextStyle(...),
    labelMedium = TextStyle(...),
    labelLarge = TextStyle(...),
    */
)
