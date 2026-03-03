package com.dashkin.netseeker.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "wifi_spots",
    indices = [
        Index(value = ["bssid"], unique = true),
        Index(value = ["latitude", "longitude"]),
    ],
)
data class WifiSpotEntity(
    @PrimaryKey val bssid: String,
    val ssid: String,
    val latitude: Double,
    val longitude: Double,
    val isOpen: Boolean,
    val downloadMbps: Float?,
    val quality: String, // SpotQuality.name — stored as string to avoid migration on enum renames.
    val cachedAt: Long, // Unix epoch millis when this record was fetched from the WiGLE API.
)
