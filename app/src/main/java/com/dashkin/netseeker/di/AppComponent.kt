package com.dashkin.netseeker.di

import android.app.Application
import com.dashkin.netseeker.NetSeekerApp
import com.dashkin.netseeker.core.network.di.NetworkModule
import com.dashkin.netseeker.core.speedtest.di.SpeedTestModule
import com.dashkin.netseeker.database.di.DatabaseModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, SpeedTestModule::class, DatabaseModule::class])
interface AppComponent {

    fun inject(app: NetSeekerApp)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}
