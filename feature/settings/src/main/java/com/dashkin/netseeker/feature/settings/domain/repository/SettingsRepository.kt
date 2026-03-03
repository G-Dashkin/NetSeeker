package com.dashkin.netseeker.feature.settings.domain.repository

import com.dashkin.netseeker.feature.settings.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    // Emits the current settings immediately and on every subsequent change.
    fun observe(): Flow<Settings>

    // Persists settings to storage.
    suspend fun save(settings: Settings)
}
