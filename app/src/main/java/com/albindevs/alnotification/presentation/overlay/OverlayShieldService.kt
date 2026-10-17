package com.albindevs.alnotification.presentation.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.albindevs.alnotification.presentation.ui.MainActivity
import com.albindevs.alnotification.R
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
 * Servicio en primer plano (Foreground Service) que gestiona el ciclo de vida de los escudos
 * táctiles sobre el sistema operativo mediante [WindowManager].
 *
 * Aplica Clean Architecture:
 * - Se comunica con la capa de Dominio a través de [ShieldRepository].
 * - Delega la construcción y estilo de vistas al [OverlayViewFactory] (Factory Pattern / SRP).
 */
class OverlayShieldService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayFactory: OverlayViewFactory
    private lateinit var shieldRepository: ShieldRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var currentConfig: ShieldConfig = ShieldConfig()

    private var topShieldView: View? = null
    private var bottomShieldView: View? = null
    private var customShieldView: View? = null
    private var toggleBubbleView: View? = null

    private var isTemporarilyPaused: Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private val resumeRunnable = Runnable {
        isTemporarilyPaused = false
        applyOverlays()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayFactory = OverlayViewFactory(this)
        shieldRepository = (application as TouchShieldApp).container.shieldRepository

        createNotificationChannel()

        // Observa reactivamente los cambios de configuración desde el Repositorio de Dominio
        serviceScope.launch {
            shieldRepository.getShieldConfig().collectLatest { config ->
                currentConfig = config
                if (!isTemporarilyPaused) {
                    applyOverlays()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_START

        when (action) {
            ACTION_STOP -> {
                removeAllOverlays()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_PAUSE_TEMP -> {
                pauseTemporary(PAUSE_DURATION_MS)
            }
            ACTION_RESUME -> {
                handler.removeCallbacks(resumeRunnable)
                isTemporarilyPaused = false
                applyOverlays()
            }
            ACTION_START, ACTION_REFRESH -> {
                startForeground(NOTIFICATION_ID, buildNotification())
                applyOverlays()
            }
        }

        return START_STICKY
    }

    private fun pauseTemporary(durationMs: Long) {
        isTemporarilyPaused = true
        removeAllOverlays()
        showPausedBubble()
        startForeground(NOTIFICATION_ID, buildNotification("Escudo en pausa (15s)..."))

        handler.removeCallbacks(resumeRunnable)
        handler.postDelayed(resumeRunnable, durationMs)
    }

    private fun applyOverlays() {
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        removeAllOverlays()

        if (!currentConfig.isShieldEnabled && !currentConfig.isCalibrationMode) {
            stopSelf()
            return
        }

        if (isTemporarilyPaused) return

        // 1. Escudo Franja Superior (Anti-Notificaciones)
        if (currentConfig.blockTopBar) {
            topShieldView = overlayFactory.createEdgeShieldView(
                isTop = true,
                isCalibration = currentConfig.isCalibrationMode,
                label = "ESCUDO SUPERIOR (Notificaciones bloqueadas)",
                onTouchBlocked = { x, y ->
                    Log.d(TAG, "🛡️ [BLOQUEADO] Toque absorbido en Franja Superior (Notificaciones) (X=$x, Y=$y)")
                }
            )
            val topParams = overlayFactory.createEdgeLayoutParams(
                heightPx = currentConfig.topBarHeightPx,
                isTop = true
            )
            windowManager.addView(topShieldView, topParams)
        }

        // 2. Escudo Franja Inferior (Anti-Navegación)
        if (currentConfig.blockBottomBar) {
            bottomShieldView = overlayFactory.createEdgeShieldView(
                isTop = false,
                isCalibration = currentConfig.isCalibrationMode,
                label = "ESCUDO INFERIOR (Barra de Navegación bloqueada)",
                onTouchBlocked = { x, y ->
                    Log.d(TAG, "🛡️ [BLOQUEADO] Toque absorbido en Franja Inferior (Navegación) (X=$x, Y=$y)")
                }
            )
            val bottomParams = overlayFactory.createEdgeLayoutParams(
                heightPx = currentConfig.bottomBarHeightPx,
                isTop = false
            )
            windowManager.addView(bottomShieldView, bottomParams)
        }

        // 3. Parche para Botón Fantasma Específico
        if (currentConfig.blockCustomPoint) {
            customShieldView = overlayFactory.createCustomShieldView(
                isCalibration = currentConfig.isCalibrationMode,
                windowManager = windowManager,
                onPositionChanged = { newX, newY ->
                    serviceScope.launch {
                        val updated = currentConfig.copy(
                            customPointX = newX,
                            customPointY = newY
                        )
                        shieldRepository.updateConfig(updated)
                    }
                },
                onTouchBlocked = { x, y ->
                    Log.d(TAG, "🛡️ [BLOQUEADO] Toque fantasma absorbido en Zona Específica (X=$x, Y=$y)")
                }
            )
            val customParams = overlayFactory.createCustomLayoutParams(currentConfig)
            windowManager.addView(customShieldView, customParams)
        }

        // 4. Burbuja Flotante de Pausa Rápida
        if (currentConfig.showFloatingToggle) {
            createFloatingBubble()
        }
    }

    private fun createFloatingBubble() {
        if (toggleBubbleView != null) return

        toggleBubbleView = overlayFactory.createFloatingBubbleView(
            windowManager = windowManager,
            onBubbleClicked = {
                pauseTemporary(PAUSE_DURATION_MS)
            }
        )
        val bubbleParams = overlayFactory.createBubbleLayoutParams()
        windowManager.addView(toggleBubbleView, bubbleParams)
    }

    private fun showPausedBubble() {
        toggleBubbleView = overlayFactory.createPausedBubbleView(
            onResumeClicked = {
                handler.removeCallbacks(resumeRunnable)
                isTemporarilyPaused = false
                applyOverlays()
            }
        )
        val bubbleParams = overlayFactory.createBubbleLayoutParams(width = 80, height = 80)
        windowManager.addView(toggleBubbleView, bubbleParams)
    }

    private fun removeAllOverlays() {
        topShieldView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
            topShieldView = null
        }
        bottomShieldView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
            bottomShieldView = null
        }
        customShieldView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
            customShieldView = null
        }
        toggleBubbleView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
            toggleBubbleView = null
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Protección de Pantalla Táctil",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mantiene activo el escudo contra toques fantasma"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(statusText: String = "Escudo Activo (Bloqueando toques fantasma)"): Notification {
        val mainIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val pauseIntent = Intent(this, OverlayShieldService::class.java).apply {
            action = ACTION_PAUSE_TEMP
        }
        val pausePendingIntent = PendingIntent.getService(
            this,
            1,
            pauseIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, OverlayShieldService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            2,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("TouchShield - Autorradio")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(R.drawable.ic_launcher_foreground, "Pausar 15s", pausePendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Detener", stopPendingIntent)
            .build()
    }

    override fun onDestroy() {
        handler.removeCallbacks(resumeRunnable)
        removeAllOverlays()
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "TouchShield"
        const val CHANNEL_ID = "touch_shield_channel"
        const val NOTIFICATION_ID = 1001
        private const val PAUSE_DURATION_MS = 15000L

        const val ACTION_START = "com.albindevs.alnotification.START"
        const val ACTION_STOP = "com.albindevs.alnotification.STOP"
        const val ACTION_REFRESH = "com.albindevs.alnotification.REFRESH"
        const val ACTION_PAUSE_TEMP = "com.albindevs.alnotification.PAUSE_TEMP"
        const val ACTION_RESUME = "com.albindevs.alnotification.RESUME"

        fun startOrUpdate(context: Context) {
            val intent = Intent(context, OverlayShieldService::class.java).apply {
                action = ACTION_REFRESH
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, OverlayShieldService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
