package com.isao.yfoo2

import androidx.lifecycle.SavedStateHandle
import org.koin.dsl.module

val testsModule = module {
    factory<SavedStateHandle> { SavedStateHandle() }
}