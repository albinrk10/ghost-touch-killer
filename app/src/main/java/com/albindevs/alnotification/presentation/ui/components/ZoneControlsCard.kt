package com.albindevs.alnotification.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
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
fun ZoneControlsCard(
    uiState: ShieldUiState,
    onEvent: (ShieldUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val config = uiState.config

    Column(modifier = modifier) {
        // Zona Superior Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔴 Franja Superior (Anti-Notificaciones)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Switch(
                        checked = config.blockTopBar,
                        onCheckedChange = { checked ->
                            onEvent(ShieldUiEvent.UpdateTopBar(checked, config.topBarHeightPx))
                        }
                    )
                }

                if (config.blockTopBar) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Grosor del bloqueo superior: ${config.topBarHeightPx} px",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    Slider(
                        value = config.topBarHeightPx.toFloat(),
                        onValueChange = { value ->
                            onEvent(ShieldUiEvent.UpdateTopBar(config.blockTopBar, value.toInt()))
                        },
                        valueRange = 20f..250f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Zona Inferior Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔵 Franja Inferior (Anti-Navegación)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Switch(
                        checked = config.blockBottomBar,
                        onCheckedChange = { checked ->
                            onEvent(ShieldUiEvent.UpdateBottomBar(checked, config.bottomBarHeightPx))
                        }
                    )
                }

                if (config.blockBottomBar) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Grosor del bloqueo inferior: ${config.bottomBarHeightPx} px",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    Slider(
                        value = config.bottomBarHeightPx.toFloat(),
                        onValueChange = { value ->
                            onEvent(ShieldUiEvent.UpdateBottomBar(config.blockBottomBar, value.toInt()))
                        },
                        valueRange = 20f..250f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Zona Específica (Botón Fantasma) Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🟠 Parche Fantasma Específico",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Bloquea el botón que se pulsa solo",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = config.blockCustomPoint,
                        onCheckedChange = { checked ->
                            onEvent(
                                ShieldUiEvent.UpdateCustomPoint(
                                    enabled = checked,
                                    x = config.customPointX,
                                    y = config.customPointY,
                                    width = config.customPointWidth,
                                    height = config.customPointHeight
                                )
                            )
                        }
                    )
                }

                if (config.blockCustomPoint) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Posición: X=${config.customPointX}px, Y=${config.customPointY}px | Tamaño: ${config.customPointWidth}x${config.customPointHeight}px",
                        fontSize = 11.sp,
                        color = Color(0xFFFFB74D)
                    )
                    Text(
                        text = "💡 En Modo Calibración puedes arrastrarlo directamente con el dedo en la pantalla.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider X
                    Text("Posición Horizontal (X): ${config.customPointX} px", fontSize = 11.sp, color = Color.White)
                    Slider(
                        value = config.customPointX.toFloat(),
                        onValueChange = { value ->
                            onEvent(
                                ShieldUiEvent.UpdateCustomPoint(
                                    enabled = config.blockCustomPoint,
                                    x = value.toInt(),
                                    y = config.customPointY,
                                    width = config.customPointWidth,
                                    height = config.customPointHeight
                                )
                            )
                        },
                        valueRange = 0f..1200f
                    )

                    // Slider Y
                    Text("Posición Vertical (Y): ${config.customPointY} px", fontSize = 11.sp, color = Color.White)
                    Slider(
                        value = config.customPointY.toFloat(),
                        onValueChange = { value ->
                            onEvent(
                                ShieldUiEvent.UpdateCustomPoint(
                                    enabled = config.blockCustomPoint,
                                    x = config.customPointX,
                                    y = value.toInt(),
                                    width = config.customPointWidth,
                                    height = config.customPointHeight
                                )
                            )
                        },
                        valueRange = 0f..650f
                    )

                    // Slider Tamaño
                    Text("Tamaño del Parche: ${config.customPointWidth} px", fontSize = 11.sp, color = Color.White)
                    Slider(
                        value = config.customPointWidth.toFloat(),
                        onValueChange = { value ->
                            val size = value.toInt()
                            onEvent(
                                ShieldUiEvent.UpdateCustomPoint(
                                    enabled = config.blockCustomPoint,
                                    x = config.customPointX,
                                    y = config.customPointY,
                                    width = size,
                                    height = size
                                )
                            )
                        },
                        valueRange = 40f..300f
                    )
                }
            }
        }
    }
}
