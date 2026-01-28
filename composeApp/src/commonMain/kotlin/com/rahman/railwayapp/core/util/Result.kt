package com.rahman.railwayapp.core.util

/**
 * Generic result wrapper for domain & data layer.
 * Used for:
 * - Network responses
 * - Retry handling
 * - Logging & analytics
 */
sealed class Result<out T> {

    data class Success<T>(
        val data: T
    ) : Result<T>()

    data class Error(
        val code: Int,
        val message: String,
        val cause: Throwable? = null
    ) : Result<Nothing>()
}
