package com.dashkin.netseeker.database.di

import android.content.Context
import androidx.room.Room
import com.dashkin.netseeker.database.NetSeekerDatabase
import com.dashkin.netseeker.database.dao.SpeedTestResultDao
import com.dashkin.netseeker.database.dao.WifiSpotDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    private const val DATABASE_NAME = "netseeker.db"

    @Provides
    @Singleton
    fun provideDatabase(context: Context): NetSeekerDatabase =
        Room.databaseBuilder(context, NetSeekerDatabase::class.java, DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideWifiSpotDao(db: NetSeekerDatabase): WifiSpotDao = db.wifiSpotDao()

    @Provides
    @Singleton
    fun provideSpeedTestResultDao(db: NetSeekerDatabase): SpeedTestResultDao =
        db.speedTestResultDao()
}
