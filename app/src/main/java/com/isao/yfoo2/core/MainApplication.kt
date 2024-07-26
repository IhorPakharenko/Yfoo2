package com.isao.yfoo2.core

import android.app.Application
import com.isao.yfoo2.BuildConfig
import com.isao.yfoo2.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            logger(TimberKoinLogger())
            androidContext(this@MainApplication)
            modules(appModule)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}