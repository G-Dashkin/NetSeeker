package com.dashkin.netseeker.core.wifi

 // Represents the current WiFi connection state and network metadata
data class WifiConnectionInfo(
    val ssid: String, // Network name without surrounding quotes.
    val bssid: String, // Access point MAC address.
    val rssi: Int, // Signal strength in dBm (typically -30 to -90).
    val linkSpeedMbps: Int, // Link speed reported by the driver in Mbps.
    val frequencyMhz: Int, // Channel frequency in MHz.
) {
    val band: String
        get() = when {
            frequencyMhz in FREQ_2GHZ_RANGE -> "2.4 GHz"
            frequencyMhz in FREQ_5GHZ_RANGE -> "5 GHz"
            frequencyMhz in FREQ_6GHZ_RANGE -> "6 GHz"
            else -> "Unknown"
        }

    companion object {
        private val FREQ_2GHZ_RANGE = 2400..2500
        private val FREQ_5GHZ_RANGE = 4900..5900
        private val FREQ_6GHZ_RANGE = 5925..7125
    }
}
