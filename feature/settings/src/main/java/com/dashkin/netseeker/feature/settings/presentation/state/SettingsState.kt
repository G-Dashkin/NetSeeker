package com.dashkin.netseeker.feature.settings.presentation.state

import com.dashkin.netseeker.feature.settings.domain.model.Settings
import com.dashkin.netseeker.feature.settings.domain.model.SpeedUnit

data class SettingsState(
    val fastThresholdMbps: Float = Settings.DEFAULT_FAST_THRESHOLD,
    val slowThresholdMbps: Float = Settings.DEFAULT_SLOW_THRESHOLD,
    val searchRadiusMeters: Int = Settings.DEFAULT_SEARCH_RADIUS,
    val speedUnit: SpeedUnit = SpeedUnit.MBPS,
)
