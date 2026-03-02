package com.dashkin.netseeker.feature.speedtest.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dashkin.netseeker.core.speedtest.SpeedTestEngine
import com.dashkin.netseeker.core.speedtest.SpeedTestEngineFactory
import com.dashkin.netseeker.core.speedtest.SpeedTestProgress
import com.dashkin.netseeker.core.wifi.WifiObserver
import com.dashkin.netseeker.core.wifi.WifiObserverImpl
import com.dashkin.netseeker.feature.speedtest.presentation.state.SpeedTestState
import com.dashkin.netseeker.feature.speedtest.presentation.state.TestPhase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpeedTestViewModel(application: Application) : AndroidViewModel(application) {

    private val wifiObserver: WifiObserver = WifiObserverImpl(application)
    private val speedTestEngine: SpeedTestEngine = SpeedTestEngineFactory.create()

    private val _state = MutableStateFlow(SpeedTestState())
    val state: StateFlow<SpeedTestState> = _state.asStateFlow()

    private var testJob: Job? = null

    init {
        observeWifiState()
    }

    private fun observeWifiState() {
        viewModelScope.launch {
            wifiObserver.connectionInfo.collect { info ->
                _state.update { it.copy(wifiInfo = info) }
            }
        }
    }

    fun startTest() {
        if (_state.value.isRunning) return
        testJob?.cancel()
        testJob = viewModelScope.launch {
            val wifiSsid = _state.value.wifiInfo?.ssid
            speedTestEngine.runTest(wifiSsid = wifiSsid).collect { progress ->
                handleProgress(progress)
            }
        }
    }

    fun cancelTest() {
        testJob?.cancel()
        testJob = null
        _state.update { it.copy(phase = TestPhase.IDLE, progress = 0, currentSpeedMbps = 0f) }
    }

    private fun handleProgress(progress: SpeedTestProgress) {
        _state.update { current ->
            when (progress) {
                is SpeedTestProgress.Pinging -> current.copy(
                    phase = TestPhase.PINGING,
                    progress = (progress.attempt * 100) / progress.totalAttempts,
                    currentSpeedMbps = 0f,
                )
                is SpeedTestProgress.Downloading -> current.copy(
                    phase = TestPhase.DOWNLOADING,
                    progress = progress.percent,
                    currentSpeedMbps = progress.currentSpeedMbps,
                )
                is SpeedTestProgress.Uploading -> current.copy(
                    phase = TestPhase.UPLOADING,
                    progress = progress.percent,
                    currentSpeedMbps = progress.currentSpeedMbps,
                )
                is SpeedTestProgress.Completed -> current.copy(
                    phase = TestPhase.COMPLETED,
                    progress = 100,
                    currentSpeedMbps = progress.result.downloadMbps,
                    result = progress.result,
                    error = null,
                )
                is SpeedTestProgress.Error -> current.copy(
                    phase = TestPhase.ERROR,
                    progress = 0,
                    currentSpeedMbps = 0f,
                    error = progress.message,
                )
            }
        }
    }

}
