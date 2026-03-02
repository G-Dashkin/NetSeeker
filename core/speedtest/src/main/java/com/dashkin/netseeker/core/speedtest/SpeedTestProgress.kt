package com.dashkin.netseeker.core.speedtest

// Represents a step in an ongoing speed test session.
sealed class SpeedTestProgress {

    // Download phase is active. percent is 0–100, currentSpeedMbps is live throughput.
    data class Downloading(
        val percent: Int,
        val currentSpeedMbps: Float,
    ) : SpeedTestProgress()

    // Upload phase is active. percent is 0–100, currentSpeedMbps is live throughput.
    data class Uploading(
        val percent: Int,
        val currentSpeedMbps: Float,
    ) : SpeedTestProgress()

    // Ping phase is active. attempt is 1-based, totalAttempts is the configured count.
    data class Pinging(
        val attempt: Int,
        val totalAttempts: Int,
    ) : SpeedTestProgress()

    // All phases finished successfully. result holds the aggregated measurements.
    data class Completed(val result: SpeedTestResult) : SpeedTestProgress()

    // A non-recoverable error terminated the test. message is human-readable.
    data class Error(val message: String) : SpeedTestProgress()
}
