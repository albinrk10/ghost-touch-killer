package com.albindevs.alnotification.di

import com.albindevs.alnotification.domain.repository.ShieldRepository
import com.albindevs.alnotification.domain.usecase.GetShieldConfigUseCase
import com.albindevs.alnotification.domain.usecase.ToggleCalibrationUseCase
import com.albindevs.alnotification.domain.usecase.ToggleShieldUseCase
import com.albindevs.alnotification.domain.usecase.UpdateShieldConfigUseCase

/**
 * Contenedor de Inyección de Dependencias (Dependency Injection Container).
 *
 * Aplica el Principio de Inversión de Control (IoC) y facilita el desacoplamiento
 * y la testeabilidad sin requerir librerías externas complejas.
 */
interface AppContainer {
    val shieldRepository: ShieldRepository
    val getShieldConfigUseCase: GetShieldConfigUseCase
    val updateShieldConfigUseCase: UpdateShieldConfigUseCase
    val toggleShieldUseCase: ToggleShieldUseCase
    val toggleCalibrationUseCase: ToggleCalibrationUseCase
}
