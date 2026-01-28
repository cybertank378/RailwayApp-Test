package com.rahman.railwayapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaginationDto(
    val page: Int,
    val limit: Int,
    val total: Int,
    val pages: Int
)