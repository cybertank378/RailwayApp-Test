package com.rahman.railwayapp.core.logging

interface AppLogger {
    fun info(message: String)
    fun error(message: String, throwable: Throwable? = null)
}

object ConsoleLogger : AppLogger {
    override fun info(message: String) {
        println("[INFO] $message")
    }

    override fun error(message: String, throwable: Throwable?) {
        println("[ERROR] $message")
        throwable?.printStackTrace()
    }
}
