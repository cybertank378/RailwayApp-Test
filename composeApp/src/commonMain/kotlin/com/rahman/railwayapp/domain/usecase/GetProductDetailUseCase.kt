package com.rahman.railwayapp.domain.usecase

import com.rahman.railwayapp.domain.model.Product
import com.rahman.railwayapp.domain.repository.ProductRepository
import com.rahman.railwayapp.core.util.Result

class GetProductDetailUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: Int): Result<Product> {
        return repository.getProductDetail(id)
    }
}
