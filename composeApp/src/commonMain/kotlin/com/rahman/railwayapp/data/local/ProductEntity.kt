package com.rahman.railwayapp.data.local


data class ProductEntity(
    val id: Int,
    val title: String,
    val description: String?,
    val userId: String,
    val createdAt: kotlin.time.Instant,
    val updatedAt: kotlin.time.Instant,
    val synced: Boolean
)