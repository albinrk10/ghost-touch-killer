package com.albindevs.alnotification

import android.app.Application
import com.albindevs.alnotification.di.AppContainer
import com.albindevs.alnotification.di.DefaultAppContainer

/**
 * Clase de Aplicación base para TouchShield.
 *
 * Mantiene la instancia del contenedor de dependencias [AppContainer] viva
 * durante todo el ciclo de vida del proceso de la aplicación.
 */
class TouchShieldApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }
}
