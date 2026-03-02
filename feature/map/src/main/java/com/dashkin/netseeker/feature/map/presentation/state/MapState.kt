package com.dashkin.netseeker.feature.map.presentation.state

data class MapState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
