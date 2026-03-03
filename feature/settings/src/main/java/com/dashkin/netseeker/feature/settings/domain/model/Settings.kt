package com.dashkin.netseeker.feature.settings.domain.model

//  User-configurable application settings.
data class Settings(
    val fastThresholdMbps: Float = DEFAULT_FAST_THRESHOLD, // Download speed (Mbps) at or above which a spot is considered fast (green).
    val slowThresholdMbps: Float = DEFAULT_SLOW_THRESHOLD, // Download speed (Mbps) below which a spot is considered slow (red).
    val searchRadiusMeters: Int = DEFAULT_SEARCH_RADIUS, // Radius (metres) used to load nearby WiFi spots.
    val speedUnit: SpeedUnit = SpeedUnit.MBPS, // Unit of measurement displayed for speed values.
) {
    companion object {
        const val DEFAULT_FAST_THRESHOLD = 50f
        const val DEFAULT_SLOW_THRESHOLD = 10f
        const val DEFAULT_SEARCH_RADIUS = 500
    }
}
