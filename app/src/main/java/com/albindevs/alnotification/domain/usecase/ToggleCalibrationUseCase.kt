package com.albindevs.alnotification.domain.usecase

import com.albindevs.alnotification.domain.repository.ShieldRepository

/**
 * Caso de Uso: Alternar o fijar el modo de calibración visual.
 *
 * Aplica el principio de Responsabilidad Única (SRP).
 */
class ToggleCalibrationUseCase(
    private val repository: ShieldRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setCalibrationMode(enabled)
    }
}
