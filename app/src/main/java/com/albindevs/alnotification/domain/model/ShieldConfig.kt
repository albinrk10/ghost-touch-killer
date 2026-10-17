package com.albindevs.alnotification.domain.model

/**
 * Modelo de Dominio inmutable que representa la configuración del escudo táctil.
 *
 * Cumple con Clean Architecture al ser una clase pura de Kotlin sin dependencias
 * del framework de Android.
 *
 * @property isShieldEnabled Indica si el escudo protector maestro está activo.
 * @property isCalibrationMode Indica si las zonas se dibujan con colores para calibración visual.
 * @property blockTopBar Activa la zona de bloqueo para la barra superior de notificaciones.
 * @property topBarHeightPx Altura en píxeles de la franja superior protegida.
 * @property blockBottomBar Activa la zona de bloqueo para la barra inferior de navegación.
 * @property bottomBarHeightPx Altura en píxeles de la franja inferior protegida.
 * @property blockCustomPoint Activa el parche para anular el botón o coordenada con toque fantasma.
 * @property customPointX Coordenada horizontal X del parche fantasma.
 * @property customPointY Coordenada vertical Y del parche fantasma.
 * @property customPointWidth Ancho en píxeles del parche fantasma.
 * @property customPointHeight Alto en píxeles del parche fantasma.
 * @property showFloatingToggle Muestra la burbuja flotante para pausar el escudo por 15 segundos.
 */
data class ShieldConfig(
    val isShieldEnabled: Boolean = false,
    val isCalibrationMode: Boolean = false,
    val blockTopBar: Boolean = true,
    val topBarHeightPx: Int = DEFAULT_TOP_BAR_HEIGHT_PX,
    val blockBottomBar: Boolean = true,
    val bottomBarHeightPx: Int = DEFAULT_BOTTOM_BAR_HEIGHT_PX,
    val blockCustomPoint: Boolean = false,
    val customPointX: Int = DEFAULT_CUSTOM_X_PX,
    val customPointY: Int = DEFAULT_CUSTOM_Y_PX,
    val customPointWidth: Int = DEFAULT_CUSTOM_SIZE_PX,
    val customPointHeight: Int = DEFAULT_CUSTOM_SIZE_PX,
    val showFloatingToggle: Boolean = true
) {
    companion object {
        const val DEFAULT_TOP_BAR_HEIGHT_PX = 36
        const val DEFAULT_BOTTOM_BAR_HEIGHT_PX = 48
        const val DEFAULT_CUSTOM_X_PX = 400
        const val DEFAULT_CUSTOM_Y_PX = 250
        const val DEFAULT_CUSTOM_SIZE_PX = 120
    }
}
