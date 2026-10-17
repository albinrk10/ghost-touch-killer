package com.albindevs.alnotification.infrastructure.repository

import com.albindevs.alnotification.infrastructure.datasource.ShieldLocalDataSource
import com.albindevs.alnotification.domain.model.ShieldConfig
import com.albindevs.alnotification.domain.repository.ShieldRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementación de [ShieldRepository].
 *
 * Aplica el Repository Pattern para abstraer la fuente de datos local.
 * Cumple con el Principio de Sustitución de Liskov (LSP): cualquier componente
 * que requiera [ShieldRepository] puede recibir esta implementación sin efectos secundarios.
 */
class ShieldRepositoryImpl(
    private val localDataSource: ShieldLocalDataSource
) : ShieldRepository {

    override fun getShieldConfig(): Flow<ShieldConfig> {
        return localDataSource.configFlow
    }

    override suspend fun getShieldConfigSync(): ShieldConfig {
        return localDataSource.getConfig()
    }

    override suspend fun updateConfig(config: ShieldConfig) {
        localDataSource.saveConfig(config)
    }

    override suspend fun setShieldEnabled(enabled: Boolean) {
        localDataSource.setShieldEnabled(enabled)
    }

    override suspend fun setCalibrationMode(enabled: Boolean) {
        localDataSource.setCalibrationMode(enabled)
    }
}
