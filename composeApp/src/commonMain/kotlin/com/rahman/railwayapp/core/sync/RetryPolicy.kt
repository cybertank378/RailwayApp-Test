package com.rahman.railwayapp.core.sync

import kotlinx.coroutines.delay

class RetryPolicy(
    private val maxRetries: Int = 3,
    private val baseDelayMs: Long = 100
) {
    suspend fun <T> execute(block: suspend () -> T): T {
        var attempt = 0
        var lastError: Throwable? = null

        while (attempt <= maxRetries) {
            try {
                return block()
            } catch (e: Throwable) {
                lastError = e
                if (attempt == maxRetries) break
                delay(baseDelayMs * (1 shl attempt)) // exponential
                attempt++
            }
        }
        throw lastError ?: IllegalStateException("Unknown retry error")
    }
}
