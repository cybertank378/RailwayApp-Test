package com.rahman.railwayapp.domain.usecase

import com.rahman.railwayapp.domain.repository.ProductRepository
import com.rahman.railwayapp.core.util.Result

class UpdateProductUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        id: Int,
        title: String,
        description: String?
    ): Result<Unit> {
        return repository.update(id, title, description)
    }
}
