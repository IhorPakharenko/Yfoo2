package com.isao.yfoo2.presentation.core.utils

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.koin.dsl.module

val contextModule = module {
    single<Context> { InstrumentationRegistry.getInstrumentation().targetContext }
}