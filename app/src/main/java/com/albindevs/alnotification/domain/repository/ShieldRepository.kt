package com.albindevs.alnotification.domain.repository

import com.albindevs.alnotification.domain.model.ShieldConfig
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para la gestión de la configuración de TouchShield.
 *
 * Cumple con el Principio de Inversión de Dependencias (DIP) de SOLID:
 * la capa de Dominio define la interfaz abstracta y la capa de Datos la implementa.
 */
interface ShieldRepository {

    /**
     * Emite de forma continua y reactiva los cambios en la configuración del escudo.
     */
    fun getShieldConfig(): Flow<ShieldConfig>

    /**
     * Obtiene una instantánea sincrónica/suspendida de la configuración actual.
     */
    suspend fun getShieldConfigSync(): ShieldConfig

    /**
     * Actualiza y persiste la configuración completa del escudo.
     */
    suspend fun updateConfig(config: ShieldConfig)

    /**
     * Activa o desactiva el escudo maestro.
     */
    suspend fun setShieldEnabled(enabled: Boolean)

    /**
     * Activa o desactiva el modo de calibración visual.
     */
    suspend fun setCalibrationMode(enabled: Boolean)
}
