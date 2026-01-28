package com.rahman.railwayapp.core.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Abstraction for network connectivity.
 * Platform-agnostic (commonMain).
 */
interface NetworkMonitor {

    /**
     * One-shot check (used by Repository)
     */
    fun isOnline(): Boolean

    /**
     * Reactive network state (used by ViewModel/UI)
     */
    fun observe(): Flow<Boolean>
}


