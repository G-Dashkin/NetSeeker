package com.dashkin.netseeker.core.speedtest

 // Final result of a completed speed test session.
data class SpeedTestResult(
    val downloadMbps: Float, // Measured download throughput in Megabits per second.
    val uploadMbps: Float, // Measured upload throughput in Megabits per second.
    val pingMs: Int, // Median round-trip latency in milliseconds.
    val timestamp: Long, // Unix epoch milliseconds when the test completed.
    val wifiSsid: String?, // SSID of the connected network, or `null` if unavailable.
    val latitude: Double?, // Device latitude at time of test, or `null` if location is unavailable.
    val longitude: Double?, // Device longitude at time of test, or `null` if location is unavailable.
)
