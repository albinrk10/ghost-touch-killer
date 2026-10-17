package com.albindevs.alnotification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.albindevs.alnotification.di.AppContainer

/**
 * Fábrica (Factory Pattern) para la instanciación controlada de [ShieldViewModel],
 * inyectando los casos de uso provistos por el contenedor de dependencias.
 */
class ShieldViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShieldViewModel::class.java)) {
            return ShieldViewModel(
                getShieldConfigUseCase = container.getShieldConfigUseCase,
                updateShieldConfigUseCase = container.updateShieldConfigUseCase,
                toggleShieldUseCase = container.toggleShieldUseCase,
                toggleCalibrationUseCase = container.toggleCalibrationUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
