package com.dashkin.netseeker.feature.speedtest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.dashkin.netseeker.feature.speedtest.presentation.state.SpeedTestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SpeedTestViewModel : ViewModel() {

    private val _state = MutableStateFlow(SpeedTestState())
    val state: StateFlow<SpeedTestState> = _state.asStateFlow()
}
