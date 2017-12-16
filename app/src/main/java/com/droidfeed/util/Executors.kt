package com.droidfeed.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by Dogan Gulcan on 11/30/17.
 */
private val IO_EXECUTOR by lazy { Executors.newSingleThreadExecutor() }
private val UI_EXECUTOR by lazy { MainThreadExecutor() }

fun workerThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}

fun uiThread(f: () -> Unit) {
    UI_EXECUTOR.execute(f)
}

private class MainThreadExecutor : Executor {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        mainThreadHandler.post(command)
    }

}