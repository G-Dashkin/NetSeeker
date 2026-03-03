package com.dashkin.netseeker.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Top-level response from the WiGLE network search endpoint.
@JsonClass(generateAdapter = true)
data class WigleSearchResponseDto(
    // Whether the request was processed successfully by WiGLE.
    @Json(name = "success") val success: Boolean,
    // Total number of matching records in the WiGLE database.
    @Json(name = "totalResults") val totalResults: Int,
    // Page of network results matching the search criteria.
    @Json(name = "results") val results: List<WigleNetworkDto>
)
