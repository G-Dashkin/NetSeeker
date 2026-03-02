package com.dashkin.netseeker.core.wifi

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

// Production implementation of WifiObserver backed by ConnectivityManager network callbacks.
// On API 31+ uses [NetworkCapabilities.transportInfo] to read [android.net.wifi.WifiInfo].
// On API 29–30 falls back to the deprecated [WifiManager.connectionInfo].
// Note: returning a non-null [WifiConnectionInfo.ssid] requires ACCESS_FINE_LOCATION permission
// to be granted at runtime (Android 10+). Without it, the SSID field reads as "<unknown ssid>"
// and [getCurrentConnectionInfo] returns `null`.
class WifiObserverImpl(private val context: Context) : WifiObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Suppress("DEPRECATION")
    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    override val connectionInfo: Flow<WifiConnectionInfo?> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities,
            ) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    trySend(getCurrentConnectionInfo())
                }
            }

            override fun onLost(network: Network) {
                trySend(null)
            }
        }

        // Emit the current state immediately before waiting for callbacks.
        trySend(getCurrentConnectionInfo())

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun isWifiConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun getCurrentConnectionInfo(): WifiConnectionInfo? {
        if (!isWifiConnected()) return null
        val wifiInfo = resolveWifiInfo() ?: return null

        @Suppress("DEPRECATION")
        val rawSsid = wifiInfo.ssid ?: return null
        val ssid = rawSsid.removeSurrounding("\"")
        if (ssid == UNKNOWN_SSID || ssid.isBlank()) return null

        @Suppress("DEPRECATION")
        return WifiConnectionInfo(
            ssid = ssid,
            bssid = wifiInfo.bssid.orEmpty(),
            rssi = wifiInfo.rssi,
            linkSpeedMbps = wifiInfo.linkSpeed,
            frequencyMhz = wifiInfo.frequency,
        )
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @Suppress("DEPRECATION")
    private fun resolveWifiInfo(): WifiInfo? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val network = connectivityManager.activeNetwork ?: return null
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return null
            capabilities.transportInfo as? WifiInfo
        } else {
            wifiManager.connectionInfo
        }
    }

    companion object {
        private const val UNKNOWN_SSID = "<unknown ssid>"
    }
}
