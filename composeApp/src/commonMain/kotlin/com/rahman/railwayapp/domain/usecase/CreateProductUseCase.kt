package com.rahman.railwayapp.domain.usecase

import com.rahman.railwayapp.domain.repository.ProductRepository
import com.rahman.railwayapp.core.util.Result

class CreateProductUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String?

    ): Result<Unit> {
        return repository.create(title, description)
    }
}
