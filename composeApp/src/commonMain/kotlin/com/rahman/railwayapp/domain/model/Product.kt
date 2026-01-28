package com.rahman.railwayapp.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String?,
    val userId: String,
    val isSynced: Boolean
)
