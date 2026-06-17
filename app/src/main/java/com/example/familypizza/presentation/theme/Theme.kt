package com.example.familypizza.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val FamilyRed        = Color(0xFFD71920)
val FamilyYellow     = Color(0xFFFFC928)
val FamilyDark       = Color(0xFF1F1F1F)
val FamilyMuted      = Color(0xFF6E6E6E)
val FamilyBackground = Color(0xFFFFF7EF)

@Composable
fun FamilyPizzaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary      = FamilyRed,
            secondary    = FamilyYellow,
            background   = FamilyBackground,
            surface      = Color.White,
            error        = Color(0xFFD32F2F),
            onPrimary    = Color.White,
            onSecondary  = FamilyDark,
            onBackground = FamilyDark,
            onSurface    = FamilyDark
        ),
        content = content
    )
}