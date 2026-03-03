package com.dashkin.netseeker.core.network.api

import com.dashkin.netseeker.core.network.dto.WigleSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit service for the WiGLE v2 network search API.
interface WigleApiService {

    // Searches for WiFi networks within a geographic bounding box.
    @GET("api/v2/network/search")
    suspend fun searchNetworks(
        @Query("latrange1") latMin: Double, // Southern boundary of the search area (latitude).
        @Query("latrange2") latMax: Double, // Northern boundary of the search area (latitude).
        @Query("longrange1") lngMin: Double, // Western boundary of the search area (longitude).
        @Query("longrange2") lngMax: Double, // Eastern boundary of the search area (longitude).
        @Query("resultsPerPage") resultsPerPage: Int, // Maximum number of results to return (max 1000).
        @Query("ssid") ssid: String?, // Optional SSID filter; pass null to return all networks.
        @Query("freenet") onlyOpen: String?, // Pass "true" to return only open (password-free) networks.
    ): WigleSearchResponseDto
}
