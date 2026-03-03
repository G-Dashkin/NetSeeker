package com.dashkin.netseeker.feature.settings.data.repository

import android.content.SharedPreferences
import com.dashkin.netseeker.feature.settings.domain.model.Settings
import com.dashkin.netseeker.feature.settings.domain.model.SpeedUnit
import com.dashkin.netseeker.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

class SettingsRepositoryImpl(
    private val prefs: SharedPreferences,
) : SettingsRepository {

    override fun observe(): Flow<Settings> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(readSettings())
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.onStart { emit(readSettings()) }

    override suspend fun save(settings: Settings) {
        prefs.edit()
            .putFloat(KEY_FAST_THRESHOLD, settings.fastThresholdMbps)
            .putFloat(KEY_SLOW_THRESHOLD, settings.slowThresholdMbps)
            .putInt(KEY_SEARCH_RADIUS, settings.searchRadiusMeters)
            .putString(KEY_SPEED_UNIT, settings.speedUnit.name)
            .apply()
    }

    private fun readSettings(): Settings = Settings(
        fastThresholdMbps = prefs.getFloat(KEY_FAST_THRESHOLD, Settings.DEFAULT_FAST_THRESHOLD),
        slowThresholdMbps = prefs.getFloat(KEY_SLOW_THRESHOLD, Settings.DEFAULT_SLOW_THRESHOLD),
        searchRadiusMeters = prefs.getInt(KEY_SEARCH_RADIUS, Settings.DEFAULT_SEARCH_RADIUS),
        speedUnit = prefs.getString(KEY_SPEED_UNIT, SpeedUnit.MBPS.name)
            ?.let { runCatching { SpeedUnit.valueOf(it) }.getOrNull() }
            ?: SpeedUnit.MBPS,
    )

    companion object {
        const val PREFS_NAME = "netseeker_settings"
        private const val KEY_FAST_THRESHOLD = "fast_threshold_mbps"
        private const val KEY_SLOW_THRESHOLD = "slow_threshold_mbps"
        private const val KEY_SEARCH_RADIUS = "search_radius_meters"
        private const val KEY_SPEED_UNIT = "speed_unit"
    }
}
