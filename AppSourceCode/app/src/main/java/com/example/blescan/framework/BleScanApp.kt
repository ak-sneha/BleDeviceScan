package com.example.blescan.framework

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import org.jetbrains.anko.appcompatV7.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class BleScanApp : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: BleScanApp? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}