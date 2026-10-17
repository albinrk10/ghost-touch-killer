package com.albindevs.alnotification.domain.usecase

import com.albindevs.alnotification.domain.model.ShieldConfig
import com.albindevs.alnotification.domain.repository.ShieldRepository

/**
 * Caso de Uso: Actualizar y persistir la configuración completa o modificada.
 *
 * Aplica el principio de Responsabilidad Única (SRP).
 */
class UpdateShieldConfigUseCase(
    private val repository: ShieldRepository
) {
    suspend operator fun invoke(config: ShieldConfig) {
        repository.updateConfig(config)
    }
}
