package com.rahman.railwayapp.core.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Default implementation for Desktop/JVM.
 * Real platform detection can update this.
 */
class DefaultNetworkMonitor(
    initialState: Boolean = true
) : NetworkMonitor {

    private val networkState =
        MutableStateFlow(initialState)

    override fun isOnline(): Boolean =
        networkState.value

    override fun observe(): Flow<Boolean> =
        networkState.asStateFlow()

    /**
     * Used by platform code (jvmMain) or tests
     */
    fun update(isOnline: Boolean) {
        networkState.value = isOnline
    }
}