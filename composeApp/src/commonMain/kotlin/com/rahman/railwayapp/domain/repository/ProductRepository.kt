package com.rahman.railwayapp.domain.repository

import com.rahman.railwayapp.domain.model.Product
import com.rahman.railwayapp.core.util.Result
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProducts(): Flow<List<Product>>
    suspend fun refresh(page: Int, limit: Int): Result<Unit>
    suspend fun create(title: String, description: String?): Result<Unit>
    suspend fun update(id: Int, title: String, description: String?): Result<Unit>
    suspend fun delete(id: Int) : Result<Unit>
    suspend fun getProductDetail(id: Int): Result<Product>
}
