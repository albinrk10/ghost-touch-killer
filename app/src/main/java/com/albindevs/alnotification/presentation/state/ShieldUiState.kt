package com.albindevs.alnotification.presentation.state

import com.albindevs.alnotification.domain.model.ShieldConfig

/**
 * Estado inmutable de la interfaz de usuario (MVI / UDF).
 *
 * Representa de manera declarativa todo lo que la pantalla debe renderizar en
 * un momento dado.
 */
data class ShieldUiState(
    val config: ShieldConfig = ShieldConfig(),
    val hasOverlayPermission: Boolean = false,
    val hasAccessibilityPermission: Boolean = false,
    val isLoading: Boolean = false
) {
    val isShieldActive: Boolean
        get() = config.isShieldEnabled && hasOverlayPermission

    val isCalibrationActive: Boolean
        get() = config.isCalibrationMode && hasOverlayPermission
}
