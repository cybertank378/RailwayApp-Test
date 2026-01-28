package com.rahman.railwayapp.core.util.id

class IdGeneratorImpl : IdGenerator {
    private var current = 0
    override fun nextId(): Int = ++current
}