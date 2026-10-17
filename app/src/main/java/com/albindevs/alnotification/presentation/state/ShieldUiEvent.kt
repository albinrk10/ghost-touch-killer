package com.albindevs.alnotification.presentation.state

import com.albindevs.alnotification.domain.model.ShieldConfig

/**
 * Eventos e intenciones del usuario emitidos desde la interfaz de usuario.
 *
 * Sigue el patrón Unidirectional Data Flow (UDF) de MVI/MVVM:
 * la UI emite eventos y el ViewModel reacciona actualizando el estado.
 */
sealed interface ShieldUiEvent {
    data class ToggleMasterShield(val enabled: Boolean) : ShieldUiEvent
    data class ToggleCalibrationMode(val enabled: Boolean) : ShieldUiEvent
    data class ToggleFloatingBubble(val enabled: Boolean) : ShieldUiEvent
    data class UpdateTopBar(val enabled: Boolean, val heightPx: Int) : ShieldUiEvent
    data class UpdateBottomBar(val enabled: Boolean, val heightPx: Int) : ShieldUiEvent
    data class UpdateCustomPoint(
        val enabled: Boolean,
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int
    ) : ShieldUiEvent
    data class UpdateConfig(val config: ShieldConfig) : ShieldUiEvent
    data class UpdatePermissions(val hasOverlay: Boolean, val hasAccessibility: Boolean) : ShieldUiEvent
}
