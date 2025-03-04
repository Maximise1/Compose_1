package com.example.tp_mvvm.view.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

val DarkColorScheme = darkColorScheme(
    primary = BarsBackgroundColor, // create new chat button
    secondary = SomethingColor, // ??
    tertiary = SomethingColor, // ??
    background = BackgroundColor, // bg
    onBackground = TextColor, // text
    surface = ItemBackgroundColor, // topAppBar
    onPrimary = TitlesAndIconsColor, // Create new chat icon color
    onSurface = SomethingColor,

    primaryContainer = SomethingColor,
    onPrimaryContainer = SomethingColor,
    onSecondary = SomethingColor,
    secondaryContainer = SomethingColor,
    onSecondaryContainer = SomethingColor,
    onTertiary = SomethingColor,
    tertiaryContainer = SomethingColor,
    onTertiaryContainer = SomethingColor,
    error = SomethingColor,
    errorContainer = SomethingColor,
    onError = SomethingColor,
    onErrorContainer = SomethingColor,
    surfaceVariant = SomethingColor,
    onSurfaceVariant = SomethingColor,
    outline = ButtonDisabledColor, // textFieldOutline
    inverseOnSurface = SomethingColor,
    inverseSurface = SomethingColor,
    inversePrimary = SomethingColor,
    surfaceTint = SomethingColor,
    outlineVariant = ButtonDisabledColor,
    scrim = SomethingColor,
)

val LightColorScheme = lightColorScheme(
    primary = BarsBackgroundColorLight, // create new chat button
    secondary = SomethingColorLight, // ??
    tertiary = SomethingColorLight, // ??
    background = BackgroundColorLight, // bg
    onBackground = TextColorLight, // text
    surface = ItemBackgroundColorLight, // topAppBar
    onPrimary = TitlesAndIconsColorLight, // Create new chat icon color
    onSurface = SomethingColorLight,

    primaryContainer = SomethingColorLight,
    onPrimaryContainer = SomethingColorLight,
    onSecondary = SomethingColorLight,
    secondaryContainer = SomethingColorLight,
    onSecondaryContainer = SomethingColorLight,
    onTertiary = SomethingColorLight,
    tertiaryContainer = SomethingColorLight,
    onTertiaryContainer = SomethingColorLight,
    error = SomethingColorLight,
    errorContainer = SomethingColorLight,
    onError = SomethingColorLight,
    onErrorContainer = SomethingColorLight,
    surfaceVariant = TitlesAndIconsColorLight,
    onSurfaceVariant = SomethingColorLight,
    outline = ButtonDisabledColorLight, // textFieldOutline
    inverseOnSurface = SomethingColorLight,
    inverseSurface = SomethingColorLight,
    inversePrimary = SomethingColorLight,
    surfaceTint = SomethingColorLight,
    outlineVariant = ButtonDisabledColorLight,
    scrim = SomethingColorLight,
)