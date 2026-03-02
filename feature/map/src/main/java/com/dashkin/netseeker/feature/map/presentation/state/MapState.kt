package com.dashkin.netseeker.feature.map.presentation.state

import com.dashkin.netseeker.feature.map.domain.model.WifiSpot

// Active speed-quality filter applied to the map markers.
enum class SpotFilter { ALL, FAST, MODERATE, SLOW }

data class MapState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val spots: List<WifiSpot> = emptyList(),
    val filter: SpotFilter = SpotFilter.ALL,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
)
