package su.afk.l4d2.presenter.view.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.Roboto_Bold
import kotlinproject.composeapp.generated.resources.Roboto_Medium
import org.jetbrains.compose.resources.Font


private val appColorScheme = darkColorScheme(
    primary = SteamCyan,
    onPrimary = SteamInk,
    primaryContainer = SteamCyanDim,
    onPrimaryContainer = SteamText,
    secondary = SteamGreen,
    onSecondary = SteamInk,
    tertiary = SteamAmber,
    error = SteamRed,
    onError = SteamInk,
    background = SteamInk,
    onBackground = SteamText,
    surface = SteamPanel,
    onSurface = SteamText,
    surfaceVariant = SteamPanelHigh,
    onSurfaceVariant = SteamTextMuted,
    outline = SteamLine,
)


@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val appFontFamily = FontFamily(
        Font(Res.font.Roboto_Medium, FontWeight.Normal),
        Font(Res.font.Roboto_Bold, FontWeight.Bold)
    )

    MaterialTheme(
        colorScheme = appColorScheme,
        content = content,
        typography = Typography(
            displaySmall = TextStyle(
                fontFamily = appFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 34.sp
            ),
            titleLarge = TextStyle(
                fontFamily = appFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 26.sp
            ),
            titleMedium = TextStyle(
                fontFamily = appFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 22.sp
            ),
            bodyLarge = TextStyle(
                fontFamily = appFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 22.sp
            ),
            bodyMedium = TextStyle(
                fontFamily = appFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            labelLarge = TextStyle(
                fontFamily = appFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
        ),
    )
}
