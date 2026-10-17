package com.albindevs.alnotification.presentation.ui

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.albindevs.alnotification.TouchShieldApp
import com.albindevs.alnotification.presentation.overlay.OverlayShieldService
import com.albindevs.alnotification.presentation.overlay.ShieldAccessibilityService
import com.albindevs.alnotification.presentation.state.ShieldUiEvent
import com.albindevs.alnotification.presentation.ui.theme.AlNotificationTheme
import com.albindevs.alnotification.presentation.viewmodel.ShieldViewModel
import com.albindevs.alnotification.presentation.viewmodel.ShieldViewModelFactory

/**
 * Punto de entrada de la interfaz de usuario.
 *
 * Cumple con Single Responsibility (SRP): solo se encarga de inicializar
 * el ciclo de vida de la Activity, conectar el [ShieldViewModel] e invocar
 * la interfaz declarativa en Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: ShieldViewModel by viewModels {
        ShieldViewModelFactory((application as TouchShieldApp).container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AlNotificationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212)
                ) {
                    val uiState by viewModel.uiState.collectAsState()

                    // Sincroniza el servicio de superposición cada vez que cambia el estado del escudo
                    LaunchedEffect(uiState.config.isShieldEnabled, uiState.config.isCalibrationMode) {
                        if (uiState.config.isShieldEnabled || uiState.config.isCalibrationMode) {
                            OverlayShieldService.startOrUpdate(this@MainActivity)
                        } else {
                            OverlayShieldService.stop(this@MainActivity)
                        }
                    }

                    // Verifica y sincroniza los permisos de Android al volver a primer plano
                    DisposableEffect(this) {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_RESUME) {
                                viewModel.onEvent(
                                    ShieldUiEvent.UpdatePermissions(
                                        hasOverlay = Settings.canDrawOverlays(this@MainActivity),
                                        hasAccessibility = isAccessibilityServiceEnabled(
                                            this@MainActivity,
                                            ShieldAccessibilityService::class.java
                                        )
                                    )
                                )
                            }
                        }
                        lifecycle.addObserver(observer)
                        onDispose { lifecycle.removeObserver(observer) }
                    }

                    ShieldDashboardScreen(
                        uiState = uiState,
                        onEvent = viewModel::onEvent,
                        onRequestOverlayPermission = {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:$packageName")
                            )
                            startActivity(intent)
                        },
                        onRequestAccessibilityPermission = {
                            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        }
                    )
                }
            }
        }
    }

    private fun isAccessibilityServiceEnabled(
        context: Context,
        serviceClass: Class<out AccessibilityService>
    ): Boolean {
        val expectedComponentName = ComponentName(context, serviceClass)
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)
        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledComponent = ComponentName.unflattenFromString(componentNameString)
            if (enabledComponent != null && enabledComponent == expectedComponentName) {
                return true
            }
        }
        return false
    }
}