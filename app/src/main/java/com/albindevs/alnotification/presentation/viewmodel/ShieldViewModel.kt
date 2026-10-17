package com.albindevs.alnotification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albindevs.alnotification.domain.usecase.GetShieldConfigUseCase
import com.albindevs.alnotification.domain.usecase.ToggleCalibrationUseCase
import com.albindevs.alnotification.domain.usecase.ToggleShieldUseCase
import com.albindevs.alnotification.domain.usecase.UpdateShieldConfigUseCase
import com.albindevs.alnotification.presentation.state.ShieldUiEvent
import com.albindevs.alnotification.presentation.state.ShieldUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la lógica de presentación del escudo táctil.
 *
 * Implementa el patrón MVVM y Unidirectional Data Flow (UDF):
 * - Observa la configuración reactiva de Dominio.
 * - Expone un [StateFlow] inmutable hacia la UI.
 * - Recibe intenciones del usuario mediante [onEvent].
 */
class ShieldViewModel(
    private val getShieldConfigUseCase: GetShieldConfigUseCase,
    private val updateShieldConfigUseCase: UpdateShieldConfigUseCase,
    private val toggleShieldUseCase: ToggleShieldUseCase,
    private val toggleCalibrationUseCase: ToggleCalibrationUseCase
) : ViewModel() {

    private val _permissionsState = MutableStateFlow(Pair(false, false)) // (overlay, accessibility)

    val uiState: StateFlow<ShieldUiState> = combine(
        getShieldConfigUseCase(),
        _permissionsState
    ) { config, (hasOverlay, hasAccessibility) ->
        ShieldUiState(
            config = config,
            hasOverlayPermission = hasOverlay,
            hasAccessibilityPermission = hasAccessibility,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShieldUiState(isLoading = true)
    )

    fun onEvent(event: ShieldUiEvent) {
        viewModelScope.launch {
            when (event) {
                is ShieldUiEvent.ToggleMasterShield -> {
                    toggleShieldUseCase(event.enabled)
                }
                is ShieldUiEvent.ToggleCalibrationMode -> {
                    toggleCalibrationUseCase(event.enabled)
                }
                is ShieldUiEvent.ToggleFloatingBubble -> {
                    val current = uiState.value.config
                    updateShieldConfigUseCase(current.copy(showFloatingToggle = event.enabled))
                }
                is ShieldUiEvent.UpdateTopBar -> {
                    val current = uiState.value.config
                    updateShieldConfigUseCase(
                        current.copy(
                            blockTopBar = event.enabled,
                            topBarHeightPx = event.heightPx
                        )
                    )
                }
                is ShieldUiEvent.UpdateBottomBar -> {
                    val current = uiState.value.config
                    updateShieldConfigUseCase(
                        current.copy(
                            blockBottomBar = event.enabled,
                            bottomBarHeightPx = event.heightPx
                        )
                    )
                }
                is ShieldUiEvent.UpdateCustomPoint -> {
                    val current = uiState.value.config
                    updateShieldConfigUseCase(
                        current.copy(
                            blockCustomPoint = event.enabled,
                            customPointX = event.x,
                            customPointY = event.y,
                            customPointWidth = event.width,
                            customPointHeight = event.height
                        )
                    )
                }
                is ShieldUiEvent.UpdateConfig -> {
                    updateShieldConfigUseCase(event.config)
                }
                is ShieldUiEvent.UpdatePermissions -> {
                    _permissionsState.value = Pair(event.hasOverlay, event.hasAccessibility)
                }
            }
        }
    }
}
