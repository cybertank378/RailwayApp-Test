package com.rahman.railwayapp.domain.usecase

import com.rahman.railwayapp.domain.model.Product
import com.rahman.railwayapp.domain.repository.ProductRepository
import com.rahman.railwayapp.core.util.Result
import kotlinx.coroutines.flow.Flow

class GetProductsUseCase(
    private val repository: ProductRepository
) {


    operator fun invoke(): Flow<List<Product>> {
        return repository.observeProducts()
    }


    suspend fun refresh(
        page: Int,
        limit: Int
    ): Result<Unit> {
        return repository.refresh(page, limit)
    }
}
