package com.dashkin.netseeker.feature.nearby.presentation.state

data class NearbyState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
