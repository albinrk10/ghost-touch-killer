package com.albindevs.alnotification.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.albindevs.alnotification.presentation.state.ShieldUiState

@Composable
fun ShieldHeader(
    uiState: ShieldUiState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "🛡️ TouchShield",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Protección contra toques fantasma • Clean Architecture MVVM",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        val badgeColor = when {
            !uiState.hasOverlayPermission -> Color(0xFFE53935)
            uiState.isCalibrationActive -> Color(0xFFFFA000)
            uiState.isShieldActive -> Color(0xFF4CAF50)
            else -> Color(0xFF757575)
        }
        val badgeText = when {
            !uiState.hasOverlayPermission -> "PERMISOS PENDIENTES"
            uiState.isCalibrationActive -> "CALIBRACIÓN VISUAL"
            uiState.isShieldActive -> "ESCUDO ACTIVO"
            else -> "ESCUDO INACTIVO"
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = badgeColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = badgeText,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}
