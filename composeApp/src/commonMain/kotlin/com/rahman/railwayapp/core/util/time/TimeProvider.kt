package com.rahman.railwayapp.core.util.time

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
interface TimeProvider {
    fun now(): Instant
}
