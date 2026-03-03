package com.dashkin.netseeker.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// DTO representing a single WiFi network entry from the WiGLE search API.
@JsonClass(generateAdapter = true)
data class WigleNetworkDto(
    @Json(name = "netid") val bssid: String, // BSSID (MAC address) of the access point.
    @Json(name = "ssid") val ssid: String?, // Human-readable network name; null for hidden networks.
    @Json(name = "trilat") val latitude: Double, // Latitude of the last recorded position.
    @Json(name = "trilong") val longitude: Double, // Longitude of the last recorded position.
    @Json(name = "qos") val qos: Int?, // Quality-of-service indicator provided by WiGLE (higher = better).
    @Json(name = "freenet") val isOpenNetwork: String?, // "Y" if the network has no password; empty string or "N" otherwise.
    @Json(name = "encryption") val encryption: String?, // Encryption type string (e.g., "wpa2", "wep", "none").
    @Json(name = "lastupdt") val lastUpdated: String?, // ISO-8601 timestamp of the last recorded sighting.
    @Json(name = "type") val networkType: String?, // Network type, typically "WIFI".
    @Json(name = "channel") val channel: Int?, // WiFi channel number.
    @Json(name = "country") val country: String?, // ISO country code of the network's location.
)