package com.dashkin.netseeker.core.speedtest

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

// Creates a pre-configured SpeedTestEngine with sensible default timeouts.
object SpeedTestEngineFactory {

    fun create(): SpeedTestEngine = SpeedTestEngineImpl(buildClient())

    private fun buildClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_CONNECT_SEC, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_READ_SEC, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_WRITE_SEC, TimeUnit.SECONDS)
        .build()

    private const val TIMEOUT_CONNECT_SEC = 15L
    private const val TIMEOUT_READ_SEC = 60L
    private const val TIMEOUT_WRITE_SEC = 60L
}
