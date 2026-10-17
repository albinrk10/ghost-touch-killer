package com.albindevs.alnotification.domain.usecase

import com.albindevs.alnotification.domain.model.ShieldConfig
import com.albindevs.alnotification.domain.repository.ShieldRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de Uso: Obtener el flujo reactivo de la configuración del escudo.
 *
 * Aplica el principio de Responsabilidad Única (SRP): solo se encarga
 * de exponer el stream de datos de configuración hacia las capas superiores.
 */
class GetShieldConfigUseCase(
    private val repository: ShieldRepository
) {
    operator fun invoke(): Flow<ShieldConfig> {
        return repository.getShieldConfig()
    }
}
