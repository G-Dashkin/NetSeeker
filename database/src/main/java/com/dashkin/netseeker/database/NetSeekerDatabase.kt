package com.dashkin.netseeker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dashkin.netseeker.database.dao.SpeedTestResultDao
import com.dashkin.netseeker.database.dao.WifiSpotDao
import com.dashkin.netseeker.database.entity.SpeedTestResultEntity
import com.dashkin.netseeker.database.entity.WifiSpotEntity

@Database(
    entities = [WifiSpotEntity::class, SpeedTestResultEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class NetSeekerDatabase : RoomDatabase() {
    abstract fun wifiSpotDao(): WifiSpotDao
    abstract fun speedTestResultDao(): SpeedTestResultDao
}
