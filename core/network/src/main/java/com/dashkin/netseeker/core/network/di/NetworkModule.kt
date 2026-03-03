package com.dashkin.netseeker.core.network.di

import com.dashkin.netseeker.core.network.BuildConfig
import com.dashkin.netseeker.core.network.NetworkClient
import com.dashkin.netseeker.core.network.api.WigleApiService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

// Dagger module that provides network-layer singletons.
// Include this module in the application [dagger.Component] to make
// [WigleApiService] available for injection across the app.
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideWigleApiService(): WigleApiService =
        NetworkClient(
            apiName = BuildConfig.WIGLE_API_NAME,
            apiToken = BuildConfig.WIGLE_API_TOKEN,
        ).wigleApiService
}
