package com.dashkin.netseeker.feature.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dashkin.netseeker.feature.settings.domain.model.Settings
import com.dashkin.netseeker.feature.settings.domain.model.SpeedUnit
import com.dashkin.netseeker.feature.settings.domain.repository.SettingsRepository
import com.dashkin.netseeker.feature.settings.presentation.state.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            repository.observe().collect { settings ->
                _state.update {
                    it.copy(
                        fastThresholdMbps = settings.fastThresholdMbps,
                        slowThresholdMbps = settings.slowThresholdMbps,
                        searchRadiusMeters = settings.searchRadiusMeters,
                        speedUnit = settings.speedUnit,
                    )
                }
            }
        }
    }

    fun onFastThresholdChanged(mbps: Float) {
        val clamped = mbps.coerceAtLeast(_state.value.slowThresholdMbps + MIN_THRESHOLD_GAP)
        _state.update { it.copy(fastThresholdMbps = clamped) }
        saveSettings()
    }

    fun onSlowThresholdChanged(mbps: Float) {
        val clamped = mbps.coerceAtMost(_state.value.fastThresholdMbps - MIN_THRESHOLD_GAP)
        _state.update { it.copy(slowThresholdMbps = clamped) }
        saveSettings()
    }

    fun onSearchRadiusChanged(meters: Int) {
        _state.update { it.copy(searchRadiusMeters = meters) }
        saveSettings()
    }

    fun onSpeedUnitChanged(unit: SpeedUnit) {
        _state.update { it.copy(speedUnit = unit) }
        saveSettings()
    }

    private fun saveSettings() {
        viewModelScope.launch {
            val s = _state.value
            repository.save(
                Settings(
                    fastThresholdMbps = s.fastThresholdMbps,
                    slowThresholdMbps = s.slowThresholdMbps,
                    searchRadiusMeters = s.searchRadiusMeters,
                    speedUnit = s.speedUnit,
                )
            )
        }
    }

    companion object {
        private const val MIN_THRESHOLD_GAP = 5f

        fun factory(repository: SettingsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    SettingsViewModel(repository) as T
            }
    }
}
