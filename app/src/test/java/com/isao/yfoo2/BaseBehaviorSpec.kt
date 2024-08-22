package com.isao.yfoo2

import com.isao.yfoo2.core.di.appModule
import com.isao.yfoo2.utils.testsModule
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.ksp.generated.defaultModule
import org.koin.test.KoinTest

open class BaseBehaviorSpec : BehaviorSpec(), KoinTest {

    private val modulesToLoad: List<Module> = appModule + defaultModule + testsModule

    override fun isolationMode() = IsolationMode.InstancePerLeaf

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        Dispatchers.setMain(StandardTestDispatcher())
        startKoin {
            modules(modulesToLoad)
        }
    }

    override suspend fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        Dispatchers.resetMain()
        stopKoin()
    }
}
