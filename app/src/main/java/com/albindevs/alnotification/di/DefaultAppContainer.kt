package com.albindevs.alnotification.di

import android.content.Context
import com.albindevs.alnotification.infrastructure.datasource.ShieldLocalDataSource
import com.albindevs.alnotification.infrastructure.repository.ShieldRepositoryImpl
import com.albindevs.alnotification.domain.repository.ShieldRepository
import com.albindevs.alnotification.domain.usecase.GetShieldConfigUseCase
import com.albindevs.alnotification.domain.usecase.ToggleCalibrationUseCase
import com.albindevs.alnotification.domain.usecase.ToggleShieldUseCase
import com.albindevs.alnotification.domain.usecase.UpdateShieldConfigUseCase
import kotlinx.coroutines.Dispatchers

/**
 * Implementación predeterminada del contenedor de dependencias [AppContainer].
 *
 * Utiliza instanciación perezosa ([lazy]) para inicializar los componentes solo
 * cuando son requeridos, manteniendo referencias singleton a las fuentes de datos
 * y repositorios.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    private val localDataSource: ShieldLocalDataSource by lazy {
        ShieldLocalDataSource(
            context = context,
            ioDispatcher = Dispatchers.IO
        )
    }

    override val shieldRepository: ShieldRepository by lazy {
        ShieldRepositoryImpl(localDataSource = localDataSource)
    }

    override val getShieldConfigUseCase: GetShieldConfigUseCase by lazy {
        GetShieldConfigUseCase(repository = shieldRepository)
    }

    override val updateShieldConfigUseCase: UpdateShieldConfigUseCase by lazy {
        UpdateShieldConfigUseCase(repository = shieldRepository)
    }

    override val toggleShieldUseCase: ToggleShieldUseCase by lazy {
        ToggleShieldUseCase(repository = shieldRepository)
    }

    override val toggleCalibrationUseCase: ToggleCalibrationUseCase by lazy {
        ToggleCalibrationUseCase(repository = shieldRepository)
    }
}
