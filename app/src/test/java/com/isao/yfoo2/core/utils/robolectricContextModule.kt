package com.isao.yfoo2.core.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.koin.dsl.module

val robolectricContextModule = module {
    single { ApplicationProvider.getApplicationContext<Context>() }
}