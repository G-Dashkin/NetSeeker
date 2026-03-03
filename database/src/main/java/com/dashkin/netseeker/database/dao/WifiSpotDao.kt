package com.dashkin.netseeker.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dashkin.netseeker.database.entity.WifiSpotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WifiSpotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(spots: List<WifiSpotEntity>)

    @Query("SELECT * FROM wifi_spots WHERE bssid = :bssid")
    fun getByBssid(bssid: String): Flow<WifiSpotEntity?>

    @Query(
        """
        SELECT * FROM wifi_spots
        WHERE latitude BETWEEN :latMin AND :latMax
        AND longitude BETWEEN :lngMin AND :lngMax
        """,
    )
    fun getInBounds(
        latMin: Double,
        latMax: Double,
        lngMin: Double,
        lngMax: Double,
    ): Flow<List<WifiSpotEntity>>

    @Query(
        "UPDATE wifi_spots SET downloadMbps = :downloadMbps, quality = :quality WHERE bssid = :bssid",
    )
    suspend fun updateSpeed(bssid: String, downloadMbps: Float, quality: String)

    @Query("DELETE FROM wifi_spots WHERE cachedAt < :cutoffTimestamp")
    suspend fun deleteOlderThan(cutoffTimestamp: Long)
}
