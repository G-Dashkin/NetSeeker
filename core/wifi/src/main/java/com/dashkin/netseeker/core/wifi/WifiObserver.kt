package com.dashkin.netseeker.core.wifi

import kotlinx.coroutines.flow.Flow

// Observes the current WiFi connection state.
// Implementations must be lifecycle-aware: the underlying [ConnectivityManager]
// callback is registered when [connectionInfo] is collected and unregistered on cancellation.
interface WifiObserver {

    // Emits the current [WifiConnectionInfo] whenever the WiFi state changes,
    // or `null` when the device is disconnected from WiFi.
    // Emits the current state immediately upon collection.
    val connectionInfo: Flow<WifiConnectionInfo?>

    // Returns `true` if the device is currently connected to a WiFi network.
    fun isWifiConnected(): Boolean

    // Returns a snapshot of the current connection info,
    // or `null` if not connected or if SSID is unavailable (missing location permission).
    fun getCurrentConnectionInfo(): WifiConnectionInfo?
}
