package com.dashkin.netseeker.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dashkin.netseeker.database.entity.SpeedTestResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeedTestResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: SpeedTestResultEntity): Long

    @Query("SELECT * FROM speed_test_results WHERE wifiBssid = :bssid ORDER BY timestamp DESC")
    fun getByBssid(bssid: String): Flow<List<SpeedTestResultEntity>>

    @Query("SELECT * FROM speed_test_results ORDER BY timestamp DESC")
    fun getAll(): Flow<List<SpeedTestResultEntity>>

    @Query(
        "SELECT * FROM speed_test_results WHERE wifiBssid = :bssid ORDER BY timestamp DESC LIMIT 1",
    )
    suspend fun getLatestForBssid(bssid: String): SpeedTestResultEntity?
}
