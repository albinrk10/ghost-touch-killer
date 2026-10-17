package com.albindevs.alnotification.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.albindevs.alnotification.presentation.state.ShieldUiEvent
import com.albindevs.alnotification.presentation.state.ShieldUiState

@Composable
fun MasterControlsCard(
    uiState: ShieldUiState,
    onEvent: (ShieldUiEvent) -> Unit,
    onRequestAccessibilityPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Control Maestro Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Control Maestro",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Switch Escudo General
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Activar Escudo",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Bloquea los toques fantasma de forma invisible",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = uiState.config.isShieldEnabled,
                        enabled = uiState.hasOverlayPermission,
                        onCheckedChange = { checked ->
                            onEvent(ShieldUiEvent.ToggleMasterShield(checked))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF4CAF50),
                            checkedTrackColor = Color(0xFF1B5E20)
                        )
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF2C2C2C))

                // Switch Calibración Visual
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Modo Calibración Visual",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Pinta las zonas en colores para ver dónde bloquea",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = uiState.config.isCalibrationMode,
                        enabled = uiState.hasOverlayPermission,
                        onCheckedChange = { checked ->
                            onEvent(ShieldUiEvent.ToggleCalibrationMode(checked))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFA000),
                            checkedTrackColor = Color(0xFF7F5000)
                        )
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF2C2C2C))

                // Switch Burbuja Flotante
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Burbuja de Pausa Rápida",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Botón flotante en pantalla para pausar 15 seg",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = uiState.config.showFloatingToggle,
                        onCheckedChange = { checked ->
                            onEvent(ShieldUiEvent.ToggleFloatingBubble(checked))
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Accessibility Guardian Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.hasAccessibilityPermission) Color(0xFF1B3022) else Color(0xFF332014)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Anti-Cortina de Notificaciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = if (uiState.hasAccessibilityPermission) "ACTIVO" else "PENDIENTE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (uiState.hasAccessibilityPermission) Color(0xFF4CAF50) else Color(0xFFFFB74D)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (uiState.hasAccessibilityPermission) {
                        "✅ Servicio Activo: En cuanto la cortina intente bajar, se cierra sola automáticamente en 0 milisegundos."
                    } else {
                        "⚠️ Necesario para bloquear el arrastre superior: Activa TouchShield en Ajustes de Accesibilidad para repliegue instantáneo."
                    },
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
                if (!uiState.hasAccessibilityPermission) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onRequestAccessibilityPermission,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB8C00))
                    ) {
                        Text(
                            text = "Activar en Accesibilidad",
                            fontSize = 12.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
