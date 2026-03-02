package com.dashkin.netseeker.feature.wifidetail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.dashkin.netseeker.feature.wifidetail.presentation.state.WifiDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WifiDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow(WifiDetailState())
    val state: StateFlow<WifiDetailState> = _state.asStateFlow()
}
