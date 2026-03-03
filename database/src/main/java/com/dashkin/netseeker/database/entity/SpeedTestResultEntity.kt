package com.dashkin.netseeker.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "speed_test_results",
    indices = [
        Index(value = ["wifiBssid"]),
        Index(value = ["timestamp"]),
    ],
)
data class SpeedTestResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val downloadMbps: Float,
    val uploadMbps: Float,
    val pingMs: Int,
    val timestamp: Long,
    val wifiSsid: String?,
    val wifiBssid: String?, // BSSID of the access point during the test; nullable when tested without WiFi.
    val latitude: Double?,
    val longitude: Double?,
)
