package com.rahman.railwayapp.data.remote.dto


import kotlinx.serialization.Serializable

@Serializable
data class ProductListResponse(
    val data: List<ProductDto>,
    val pagination: PaginationDto
)