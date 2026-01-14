package com.fittrack.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Observes network connectivity changes.
 * Used to trigger sync when network becomes available.
 */
@Singleton
class ConnectivityObserver @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    /**
     * Observe network connectivity as a Flow.
     * Emits CONNECTED or DISCONNECTED states.
     */
    fun observe(): Flow<ConnectionState> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(ConnectionState.CONNECTED)
            }
            
            override fun onLost(network: Network) {
                trySend(ConnectionState.DISCONNECTED)
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
                trySend(if (hasInternet) ConnectionState.CONNECTED else ConnectionState.DISCONNECTED)
            }
        }
        
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(request, callback)
        
        // Emit initial state
        trySend(getCurrentState())
        
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
    
    /**
     * Check current connectivity state synchronously.
     */
    fun getCurrentState(): ConnectionState {
        val network = connectivityManager.activeNetwork ?: return ConnectionState.DISCONNECTED
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return ConnectionState.DISCONNECTED
        
        return if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            ConnectionState.CONNECTED
        } else {
            ConnectionState.DISCONNECTED
        }
    }
    
    /**
     * Check if currently connected.
     */
    fun isConnected(): Boolean = getCurrentState() == ConnectionState.CONNECTED
}

/**
 * Represents the network connection state.
 */
enum class ConnectionState {
    CONNECTED,
    DISCONNECTED
}
