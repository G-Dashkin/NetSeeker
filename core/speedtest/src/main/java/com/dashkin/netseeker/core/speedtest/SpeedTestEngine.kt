package com.dashkin.netseeker.core.speedtest

import kotlinx.coroutines.flow.Flow

// Measures internet speed via download, upload, and ping phases.
// [runTest] returns a cold [Flow] that emits [SpeedTestProgress] events.
// Collection starts the test; cancelling the collector cancels the test.
interface SpeedTestEngine {

    // Runs a full speed test (ping → download → upload) and emits progress events.
    fun runTest(
        wifiSsid: String? = null, // wifiSsid SSID of the current network, embedded in the final SpeedTestResult
        latitude: Double? = null, // Optional device latitude for the result record.
        longitude: Double? = null, // Optional device longitude for the result record.
    ): Flow<SpeedTestProgress>
}
