package com.dashkin.netseeker.feature.map.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dashkin.netseeker.feature.map.domain.model.SpotQuality
import com.dashkin.netseeker.feature.map.domain.model.WifiSpot
import com.dashkin.netseeker.feature.map.presentation.state.MapState
import com.dashkin.netseeker.feature.map.presentation.state.SpotFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MapViewModel : ViewModel() {

    private val _state = MutableStateFlow(MapState(spots = STUB_SPOTS))
    val state: StateFlow<MapState> = _state.asStateFlow()

    // Spots filtered by the currently active MapState.filter
    val filteredSpots: StateFlow<List<WifiSpot>> = _state
        .map { s -> s.spots.applyFilter(s.filter) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), STUB_SPOTS)

    fun setFilter(spotFilter: SpotFilter) {
        _state.update { it.copy(filter = spotFilter) }
    }

    private fun List<WifiSpot>.applyFilter(spotFilter: SpotFilter): List<WifiSpot> =
        when (spotFilter) {
            SpotFilter.ALL -> this
            SpotFilter.FAST -> filter { it.quality == SpotQuality.FAST }
            SpotFilter.MODERATE -> filter { it.quality == SpotQuality.MODERATE }
            SpotFilter.SLOW -> filter { it.quality == SpotQuality.SLOW }
        }

    companion object {
        // Stub data centered around central Moscow for demo purposes.
        private val STUB_SPOTS = listOf(
            WifiSpot("1", "CoffeeHouse_WiFi",  "AA:BB:CC:DD:EE:01", 55.7558, 37.6176, SpotQuality.FAST,     78.5f,  isOpen = true),
            WifiSpot("2", "Library_Network",   "AA:BB:CC:DD:EE:02", 55.7565, 37.6192, SpotQuality.MODERATE, 25.3f,  isOpen = false),
            WifiSpot("3", "Park_FreeWiFi",     "AA:BB:CC:DD:EE:03", 55.7550, 37.6158, SpotQuality.SLOW,     4.2f,   isOpen = true),
            WifiSpot("4", "Hotel_Guest",       "AA:BB:CC:DD:EE:04", 55.7572, 37.6148, SpotQuality.FAST,     120.0f, isOpen = false),
            WifiSpot("5", "Metro_Public",      "AA:BB:CC:DD:EE:05", 55.7543, 37.6203, SpotQuality.UNKNOWN,  null,   isOpen = true),
            WifiSpot("6", "Mall_Wireless",     "AA:BB:CC:DD:EE:06", 55.7580, 37.6170, SpotQuality.MODERATE, 35.8f,  isOpen = false),
            WifiSpot("7", "Office_Corp",       "AA:BB:CC:DD:EE:07", 55.7555, 37.6143, SpotQuality.SLOW,     8.9f,   isOpen = false),
            WifiSpot("8", "Cafe_Nemo",         "AA:BB:CC:DD:EE:08", 55.7562, 37.6215, SpotQuality.FAST,     65.0f,  isOpen = true),
        )
    }
}
