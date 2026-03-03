package com.dashkin.netseeker.core.network

import com.dashkin.netseeker.core.network.api.WigleApiService
import com.dashkin.netseeker.core.network.interceptor.WigleAuthInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

// Builds and holds Retrofit-based API clients for the application.
// Credentials are injected via constructor so the class stays testable without
// touching [BuildConfig] directly in tests.
class NetworkClient(
    private val apiName: String,
    private val apiToken: String,
) {

    // Lazily-created WigleApiService backed by OkHttp + Moshi + Retrofit.
    val wigleApiService: WigleApiService by lazy {
        buildRetrofit().create(WigleApiService::class.java)
    }

    private fun buildRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(WIGLE_BASE_URL)
        .client(buildOkHttpClient())
        .addConverterFactory(MoshiConverterFactory.create(buildMoshi()))
        .build()

    private fun buildOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(WigleAuthInterceptor(apiName, apiToken))
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .connectTimeout(TIMEOUT_CONNECT_SEC, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_READ_SEC, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_WRITE_SEC, TimeUnit.SECONDS)
        .build()

    // KotlinJsonAdapterFactory is added as a last-resort fallback for any
    // class that does not have a Moshi-codegen adapter. Codegen adapters (from
    // JsonClass are always preferred and resolved first.
    private fun buildMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private companion object {
        const val WIGLE_BASE_URL = "https://api.wigle.net/"
        const val TIMEOUT_CONNECT_SEC = 15L
        const val TIMEOUT_READ_SEC = 30L
        const val TIMEOUT_WRITE_SEC = 30L
    }
}
