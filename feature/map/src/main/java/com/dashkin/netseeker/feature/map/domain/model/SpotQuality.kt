package com.dashkin.netseeker.feature.map.domain.model

// Speed quality category for a WiFi spot, derived from the latest speed measurement.
// Thresholds are defined in [SpotQuality.Thresholds] and can later be user-configured
// via the Settings screen.
enum class SpotQuality {
    FAST, // Download speed exceeds Thresholds.FAST_MBPS Mbps. Displayed as green marker.
    MODERATE, // Download speed is between Thresholds.MODERATE_MBPS and Thresholds.FAST_MBPS Mbps. Displayed as yellow.
    SLOW, // Download speed is below Thresholds.MODERATE_MBPS Mbps. Displayed as red marker.
    UNKNOWN; // No speed measurement available for this spot. Displayed as gray marker.

    object Thresholds {
        const val FAST_MBPS = 50f
        const val MODERATE_MBPS = 10f
    }

    companion object {
        fun fromSpeed(downloadMbps: Float?): SpotQuality = when {
            downloadMbps == null -> UNKNOWN
            downloadMbps >= Thresholds.FAST_MBPS -> FAST
            downloadMbps >= Thresholds.MODERATE_MBPS -> MODERATE
            else -> SLOW
        }
    }
}
