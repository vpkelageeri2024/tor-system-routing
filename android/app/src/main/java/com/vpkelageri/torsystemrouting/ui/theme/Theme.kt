package com.vpkelageri.torsystemrouting.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = OnionPurple,
    secondary = NeonGreen,
    tertiary = TextGray,
    background = DeepGray,
    surface = SurfaceGray,
    onPrimary = TextWhite,
    onSecondary = DarkOnion,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
)

@Composable
fun TorSystemRoutingTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
