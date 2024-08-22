package com.isao.yfoo2.utils

import android.app.Application
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider.getApplicationContext

fun getString(@StringRes resId: Int) = getApplicationContext<Application>().getString(resId)

fun getString(@StringRes resId: Int, vararg formatArgs: Any) =
    getApplicationContext<Application>().getString(resId, *formatArgs)