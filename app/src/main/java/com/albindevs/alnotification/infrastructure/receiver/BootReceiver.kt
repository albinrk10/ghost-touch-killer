package com.albindevs.alnotification.infrastructure.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.albindevs.alnotification.TouchShieldApp
import com.albindevs.alnotification.presentation.overlay.OverlayShieldService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receptor de arranque (BroadcastReceiver) para iniciar el servicio automáticamente
 * cada vez que se enciende el vehículo o reinicia el sistema Android.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            val repository = (context.applicationContext as TouchShieldApp).container.shieldRepository

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val config = repository.getShieldConfigSync()
                    if (config.isShieldEnabled) {
                        OverlayShieldService.startOrUpdate(context)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
