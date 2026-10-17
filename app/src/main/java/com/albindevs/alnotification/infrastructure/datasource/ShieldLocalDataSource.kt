package com.albindevs.alnotification.infrastructure.datasource

import android.content.Context
import android.content.SharedPreferences
import com.albindevs.alnotification.domain.model.ShieldConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Fuente de datos local para la persistencia de configuración en SharedPreferences.
 *
 * Utiliza un [MutableStateFlow] para emitir cambios de forma reactiva cada vez
 * que se persiste una nueva configuración, asegurando un flujo de datos asíncrono
 * y reactivo hacia el Repositorio.
 */
class ShieldLocalDataSource(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _configFlow = MutableStateFlow(readFromPrefs())
    val configFlow: Flow<ShieldConfig> = _configFlow.asStateFlow()

    suspend fun getConfig(): ShieldConfig = withContext(ioDispatcher) {
        _configFlow.value
    }

    suspend fun saveConfig(config: ShieldConfig) = withContext(ioDispatcher) {
        prefs.edit()
            .putBoolean(KEY_SHIELD_ENABLED, config.isShieldEnabled)
            .putBoolean(KEY_CALIBRATION_MODE, config.isCalibrationMode)
            .putBoolean(KEY_BLOCK_TOP, config.blockTopBar)
            .putInt(KEY_TOP_HEIGHT, config.topBarHeightPx)
            .putBoolean(KEY_BLOCK_BOTTOM, config.blockBottomBar)
            .putInt(KEY_BOTTOM_HEIGHT, config.bottomBarHeightPx)
            .putBoolean(KEY_BLOCK_CUSTOM, config.blockCustomPoint)
            .putInt(KEY_CUSTOM_X, config.customPointX)
            .putInt(KEY_CUSTOM_Y, config.customPointY)
            .putInt(KEY_CUSTOM_WIDTH, config.customPointWidth)
            .putInt(KEY_CUSTOM_HEIGHT, config.customPointHeight)
            .putBoolean(KEY_SHOW_TOGGLE, config.showFloatingToggle)
            .apply()

        _configFlow.value = config
    }

    suspend fun setShieldEnabled(enabled: Boolean) = withContext(ioDispatcher) {
        val updated = _configFlow.value.copy(isShieldEnabled = enabled)
        saveConfig(updated)
    }

    suspend fun setCalibrationMode(calibration: Boolean) = withContext(ioDispatcher) {
        val updated = _configFlow.value.copy(isCalibrationMode = calibration)
        saveConfig(updated)
    }

    private fun readFromPrefs(): ShieldConfig {
        return ShieldConfig(
            isShieldEnabled = prefs.getBoolean(KEY_SHIELD_ENABLED, false),
            isCalibrationMode = prefs.getBoolean(KEY_CALIBRATION_MODE, false),
            blockTopBar = prefs.getBoolean(KEY_BLOCK_TOP, true),
            topBarHeightPx = prefs.getInt(KEY_TOP_HEIGHT, ShieldConfig.DEFAULT_TOP_BAR_HEIGHT_PX),
            blockBottomBar = prefs.getBoolean(KEY_BLOCK_BOTTOM, true),
            bottomBarHeightPx = prefs.getInt(KEY_BOTTOM_HEIGHT, ShieldConfig.DEFAULT_BOTTOM_BAR_HEIGHT_PX),
            blockCustomPoint = prefs.getBoolean(KEY_BLOCK_CUSTOM, false),
            customPointX = prefs.getInt(KEY_CUSTOM_X, ShieldConfig.DEFAULT_CUSTOM_X_PX),
            customPointY = prefs.getInt(KEY_CUSTOM_Y, ShieldConfig.DEFAULT_CUSTOM_Y_PX),
            customPointWidth = prefs.getInt(KEY_CUSTOM_WIDTH, ShieldConfig.DEFAULT_CUSTOM_SIZE_PX),
            customPointHeight = prefs.getInt(KEY_CUSTOM_HEIGHT, ShieldConfig.DEFAULT_CUSTOM_SIZE_PX),
            showFloatingToggle = prefs.getBoolean(KEY_SHOW_TOGGLE, true)
        )
    }

    companion object {
        private const val PREFS_NAME = "touch_shield_prefs"
        private const val KEY_SHIELD_ENABLED = "key_shield_enabled"
        private const val KEY_CALIBRATION_MODE = "key_calibration_mode"
        private const val KEY_BLOCK_TOP = "key_block_top"
        private const val KEY_TOP_HEIGHT = "key_top_height"
        private const val KEY_BLOCK_BOTTOM = "key_block_bottom"
        private const val KEY_BOTTOM_HEIGHT = "key_bottom_height"
        private const val KEY_BLOCK_CUSTOM = "key_block_custom"
        private const val KEY_CUSTOM_X = "key_custom_x"
        private const val KEY_CUSTOM_Y = "key_custom_y"
        private const val KEY_CUSTOM_WIDTH = "key_custom_width"
        private const val KEY_CUSTOM_HEIGHT = "key_custom_height"
        private const val KEY_SHOW_TOGGLE = "key_show_toggle"
    }
}
