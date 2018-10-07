package me.venko.presencetracker.data.tracker

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import me.venko.presencetracker.utils.logv

/**
 * @author Victor Kosenko
 *
 */
class WifiSsidTracker(
        private val appContext: Context,
        val onConnected: (String?) -> Unit,
        val onDisconnected: () -> Unit
) {

    private val connectivityCallback: ConnectivityManager.NetworkCallback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    onConnected(resolveCurrentSsid())
                }

                override fun onLost(network: Network?) {
                    super.onLost(network)
                    onDisconnected()
                }
            }

    fun register() {
        logv { "Registering tracker" }
        resolveConnectivityManager().registerNetworkCallback(NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build(), connectivityCallback)
    }

    @SuppressLint("WifiManagerPotentialLeak")
    private fun resolveCurrentSsid(): String? {
        val wifiManager = appContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager?
        wifiManager?.let {
            val wifiInfo = wifiManager.connectionInfo
            // SSID is wrapped by double quotes in UTF8, so we clearing them before returning
            // Details: https://developer.android.com/reference/android/net/wifi/WifiInfo#getSSID()
            return wifiInfo.ssid.replace(Regex("^\"(.*)\"$"), "$1")
        } ?: run {
            return null
        }
    }

    fun unregister() {
        logv { "Unregistering tracker" }
        resolveConnectivityManager().unregisterNetworkCallback(connectivityCallback)
    }

    private fun resolveConnectivityManager() = appContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

}