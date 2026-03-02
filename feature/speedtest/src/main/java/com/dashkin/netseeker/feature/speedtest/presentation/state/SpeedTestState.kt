package com.dashkin.netseeker.feature.speedtest.presentation.state

data class SpeedTestState(
    val isRunning: Boolean = false,
    val errorMessage: String? = null,
)
