package com.example.kerklytv2.clases

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import android.widget.Toast

class NetworkSpeedChecker(private val context: Context) {

    private val TAG = "NetworkSpeedChecker"
    private val MIN_DOWNLOAD_SPEED = 1_000_000 // 1 Mbps

    fun checkNetworkSpeed() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                val networkCapabilities =
                    connectivityManager.getNetworkCapabilities(network)

                val downloadSpeed =
                    networkCapabilities?.linkDownstreamBandwidthKbps ?: 0

                if (downloadSpeed < MIN_DOWNLOAD_SPEED) {
                    Log.d(TAG, "La conexión a Internet es lenta.")
                    // Aquí puedes notificar al usuario sobre la conexión lenta
                    Toast.makeText(context, "La conexión a Internet es lenta.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }
}
