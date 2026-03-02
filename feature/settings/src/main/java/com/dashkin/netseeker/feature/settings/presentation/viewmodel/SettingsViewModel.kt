package com.dashkin.netseeker.feature.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.dashkin.netseeker.feature.settings.presentation.state.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()
}
