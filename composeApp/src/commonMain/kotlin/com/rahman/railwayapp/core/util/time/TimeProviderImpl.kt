package com.rahman.railwayapp.core.util.time

import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TimeProviderImpl : TimeProvider {

    override fun now(): Instant = Clock.System.now()
}
