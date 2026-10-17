package com.albindevs.alnotification.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.albindevs.alnotification.presentation.state.ShieldUiEvent
import com.albindevs.alnotification.presentation.state.ShieldUiState
import com.albindevs.alnotification.presentation.ui.components.MasterControlsCard
import com.albindevs.alnotification.presentation.ui.components.PermissionsCard
import com.albindevs.alnotification.presentation.ui.components.ShieldHeader
import com.albindevs.alnotification.presentation.ui.components.ZoneControlsCard

/**
 * Pantalla principal Dashboard de TouchShield estructurada de forma modular.
 *
 * Diseñada para pantallas de autorradio horizontales (1280x720) mediante
 * una distribución de 2 columnas de alto contraste.
 */
@Composable
fun ShieldDashboardScreen(
    uiState: ShieldUiState,
    onEvent: (ShieldUiEvent) -> Unit,
    onRequestOverlayPermission: () -> Unit,
    onRequestAccessibilityPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado con estado dinámico
        ShieldHeader(uiState = uiState)

        Spacer(modifier = Modifier.height(12.dp))

        // Alerta de permiso si está pendiente
        PermissionsCard(
            uiState = uiState,
            onRequestOverlayPermission = onRequestOverlayPermission
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Distribución horizontal de 2 columnas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Columna Izquierda: Controles Maestros y Accesibilidad
            MasterControlsCard(
                uiState = uiState,
                onEvent = onEvent,
                onRequestAccessibilityPermission = onRequestAccessibilityPermission,
                modifier = Modifier.weight(1f)
            )

            // Columna Derecha: Configuración de Zonas de Bloqueo
            ZoneControlsCard(
                uiState = uiState,
                onEvent = onEvent,
                modifier = Modifier.weight(1.2f)
            )
        }
    }
}
