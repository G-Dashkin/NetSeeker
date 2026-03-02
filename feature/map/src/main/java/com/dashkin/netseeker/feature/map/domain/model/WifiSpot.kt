package com.dashkin.netseeker.feature.map.domain.model

// Domain model representing a WiFi access point on the map.
data class WifiSpot(
    val id: String, // id Unique identifier (typically BSSID or a server-assigned ID from WiGLE)
    val ssid: String, // Network name; may be empty for hidden networks
    val bssid: String, // Hardware MAC address
    val latitude: Double, // Latitude in WGS84 decimal degrees
    val longitude: Double, // Longitude in WGS84 decimal degrees
    val quality: SpotQuality, // Speed quality derived from downloadMbps
    val downloadMbps: Float?, // Latest measured download speed in Mbps; null if not yet measured
    val isOpen: Boolean, // True if the network requires no authentication
)
