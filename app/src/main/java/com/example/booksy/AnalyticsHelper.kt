package com.example.booksy

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val firebaseCrashlytics = FirebaseCrashlytics.getInstance()

    /**
     * Registra un evento personalizado en Google Analytics.
     */
    fun logEvent(eventName: String, params: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    /**
     * Registra una pantalla vista en Analytics.
     */
    fun logScreenView(screenName: String, screenClass: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    /**
     * Establece el ID de usuario para el seguimiento en Analytics y Crashlytics.
     */
    fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
        userId?.let { firebaseCrashlytics.setUserId(it) }
    }

    /**
     * Registra un error no fatal en Firebase Crashlytics.
     */
    fun logNonFatalException(throwable: Throwable, customMessage: String? = null) {
        customMessage?.let { firebaseCrashlytics.log(it) }
        firebaseCrashlytics.recordException(throwable)
    }

    /**
     * Registra una clave/valor personalizada en Crashlytics para dar contexto a los errores.
     */
    fun setCustomKey(key: String, value: String) {
        firebaseCrashlytics.setCustomKey(key, value)
    }
}
