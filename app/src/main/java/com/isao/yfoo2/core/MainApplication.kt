package com.isao.yfoo2.core

import android.app.Application
import com.isao.yfoo2.BuildConfig
import com.isao.yfoo2.core.database.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule
import timber.log.Timber


class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            logger(TimberKoinLogger())
            androidContext(this@MainApplication)
            modules(
                databaseModule,
                defaultModule
            )
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}