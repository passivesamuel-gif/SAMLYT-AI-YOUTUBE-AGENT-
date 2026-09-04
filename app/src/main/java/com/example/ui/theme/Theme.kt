package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SamlytDarkColorScheme = darkColorScheme(
  primary = SamlytOrange,
  onPrimary = Color.White,
  primaryContainer = Color(0xFF3B1D0E),
  onPrimaryContainer = SamlytOrange,
  secondary = SamlytGreen,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFF0F301B),
  onSecondaryContainer = SamlytGreen,
  tertiary = SamlytCyan,
  onTertiary = Color.Black,
  background = SamlytBackground,
  onBackground = SamlytTextPrimary,
  surface = SamlytCard,
  onSurface = SamlytTextPrimary,
  surfaceVariant = SamlytCardElevated,
  onSurfaceVariant = SamlytTextSecondary,
  outline = SamlytCardBorder,
)

private val SamlytLightColorScheme = lightColorScheme(
  primary = SamlytLightOrange,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFFFEDD5),
  onPrimaryContainer = Color(0xFF9A3412),
  secondary = SamlytLightGreen,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFDCFCE7),
  onSecondaryContainer = Color(0xFF166534),
  tertiary = SamlytLightCyan,
  onTertiary = Color.White,
  background = SamlytLightBackground,
  onBackground = SamlytLightTextPrimary,
  surface = SamlytLightCard,
  onSurface = SamlytLightTextPrimary,
  surfaceVariant = SamlytLightCardElevated,
  onSurfaceVariant = SamlytLightTextSecondary,
  outline = SamlytLightCardBorder,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) SamlytDarkColorScheme else SamlytLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

