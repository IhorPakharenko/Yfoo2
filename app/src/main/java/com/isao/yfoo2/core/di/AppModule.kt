package com.isao.yfoo2.core.di

import com.isao.yfoo2.core.database.databaseModule
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule

val appModule = module {
    includes(databaseModule, defaultModule)
}