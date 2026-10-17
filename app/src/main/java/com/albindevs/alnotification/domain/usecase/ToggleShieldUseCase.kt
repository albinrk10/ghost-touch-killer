package com.albindevs.alnotification.domain.usecase

import com.albindevs.alnotification.domain.repository.ShieldRepository

/**
 * Caso de Uso: Alternar o fijar el estado del escudo maestro.
 *
 * Aplica el principio de Responsabilidad Única (SRP).
 */
class ToggleShieldUseCase(
    private val repository: ShieldRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setShieldEnabled(enabled)
    }
}
