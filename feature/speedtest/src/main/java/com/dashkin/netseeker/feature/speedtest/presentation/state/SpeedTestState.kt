package com.dashkin.netseeker.feature.speedtest.presentation.state

import com.dashkin.netseeker.core.speedtest.SpeedTestResult
import com.dashkin.netseeker.core.wifi.WifiConnectionInfo

// Represents all possible UI states of the SpeedTest screen.
data class SpeedTestState(
    val wifiInfo: WifiConnectionInfo? = null,
    val phase: TestPhase = TestPhase.IDLE,
    val progress: Int = 0,
    val currentSpeedMbps: Float = 0f,
    val result: SpeedTestResult? = null,
    val error: String? = null,
) {
    val isRunning: Boolean get() = phase.isActive
    val isWifiConnected: Boolean get() = wifiInfo != null
}

enum class TestPhase(val isActive: Boolean) {
    IDLE(false),
    PINGING(true),
    DOWNLOADING(true),
    UPLOADING(true),
    COMPLETED(false),
    ERROR(false),
}
