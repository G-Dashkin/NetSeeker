package com.dashkin.netseeker

import android.app.Application
import com.dashkin.netseeker.di.AppComponent
import com.dashkin.netseeker.di.DaggerAppComponent

class NetSeekerApp : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
    }
}
