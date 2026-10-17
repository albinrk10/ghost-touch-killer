package com.albindevs.alnotification.presentation.overlay

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.albindevs.alnotification.TouchShieldApp
import com.albindevs.alnotification.domain.model.ShieldConfig
import com.albindevs.alnotification.domain.repository.ShieldRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Servicio de Accesibilidad que actúa como guardián de seguridad del sistema.
 *
 * Si un deslizamiento físico desde el bisel exterior activa el [GestureMonitor] de Android
 * y comienza a desplegar la barra de notificaciones, este servicio detecta el evento
 * de ventana de `com.android.systemui` y fuerza de inmediato el repliegue instantáneo.
 */
class ShieldAccessibilityService : AccessibilityService() {

    private lateinit var shieldRepository: ShieldRepository
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var currentConfig: ShieldConfig = ShieldConfig()

    override fun onCreate() {
        super.onCreate()
        shieldRepository = (application as TouchShieldApp).container.shieldRepository

        serviceScope.launch {
            shieldRepository.getShieldConfig().collectLatest { config ->
                currentConfig = config
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (!currentConfig.isShieldEnabled || !currentConfig.blockTopBar) return

            val packageName = event.packageName?.toString() ?: ""

            // System UI notification shade / panel
            if (packageName.contains("systemui", ignoreCase = true)) {
                Log.d(TAG, "🔒 [ACCESIBILIDAD] Intento de desplegar notificaciones detectado -> Forzando repliegue instantáneo")
                dismissNotificationShade()
            }
        }
    }

    private fun dismissNotificationShade() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 (API 31+) native dismiss action
            performGlobalAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)
        } else {
            performGlobalAction(GLOBAL_ACTION_BACK)
        }
    }

    override fun onInterrupt() {
        // Callback requerido por el framework
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "TouchShield"
    }
}
