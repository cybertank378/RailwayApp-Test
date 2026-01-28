package com.rahman.railwayapp.domain.usecase

import com.rahman.railwayapp.domain.repository.ProductRepository
import com.rahman.railwayapp.core.util.Result

class DeleteProductUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return repository.delete(id)
    }
}
