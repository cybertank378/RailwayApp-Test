package com.rahman.railwayapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val description: String?,
    val userId: String,
    val createdAt: String,
    val updatedAt: String

)