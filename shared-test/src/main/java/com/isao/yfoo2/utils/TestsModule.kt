package com.isao.yfoo2.utils

import androidx.lifecycle.SavedStateHandle
import org.koin.dsl.module

val testsModule = module {
    factory<SavedStateHandle> { SavedStateHandle() }
}