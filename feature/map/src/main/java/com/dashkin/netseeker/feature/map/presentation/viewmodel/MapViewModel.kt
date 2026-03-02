package com.dashkin.netseeker.feature.map.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.dashkin.netseeker.feature.map.presentation.state.MapState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()
}
