package com.dashkin.netseeker.feature.nearby.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.dashkin.netseeker.feature.nearby.presentation.state.NearbyState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NearbyViewModel : ViewModel() {

    private val _state = MutableStateFlow(NearbyState())
    val state: StateFlow<NearbyState> = _state.asStateFlow()
}
