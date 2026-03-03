package com.dashkin.netseeker.core.speedtest.di

import com.dashkin.netseeker.core.speedtest.SpeedTestEngine
import com.dashkin.netseeker.core.speedtest.SpeedTestEngineFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Dagger module that provides the [SpeedTestEngine] singleton.
 *
 * Include this module in the application [dagger.Component] to enable
 * speed-test functionality across the app.
 */
@Module
object SpeedTestModule {

    @Provides
    @Singleton
    fun provideSpeedTestEngine(): SpeedTestEngine = SpeedTestEngineFactory.create()
}
