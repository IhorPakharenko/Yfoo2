package com.isao.yfoo2.core

import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import timber.log.Timber

class TimberKoinLogger(level: Level = Level.INFO) : Logger(level) {

    override fun display(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> Timber.d(KOIN_TAG, msg)
            Level.INFO -> Timber.i(KOIN_TAG, msg)
            Level.WARNING -> Timber.w(KOIN_TAG, msg)
            Level.ERROR -> Timber.e(KOIN_TAG, msg)
            else -> Timber.e(KOIN_TAG, msg)
        }
    }
}