package com.rahman.railwayapp.domain.usecase

import com.rahman.railwayapp.core.util.Result
import com.rahman.railwayapp.domain.model.Product
import com.rahman.railwayapp.domain.repository.ProductRepository
import com.rahman.railwayapp.domain.usecase.GetProductsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetProductsUseCaseTest {

    private val repository = mockk<ProductRepository>()
    private val useCase = GetProductsUseCase(repository)

    @Test
    fun `invoke should return products flow`() = runTest {
        // Arrange
        val products = listOf(
            Product(id = 1, title = "Title 1", description = "Desc 1", userId = "user123", isSynced = true),
            Product(id = 2, title = "Title 2", description = "Desc 2", userId = "user456", isSynced = false)
        )
        val flow = flow { emit(products) }
        coEvery { repository.observeProducts() } returns flow

        // Act
        val result = useCase().toList() // collect flow to list

        // Assert
        assertEquals(1, result.size)
        assertEquals(products, result[0])
    }

    @Test
    fun `refresh should return Success when repository returns Success`() = runTest {
        // Arrange
        val page = 1
        val limit = 10
        coEvery { repository.refresh(page, limit) } returns Result.Success(Unit)

        // Act
        val result = useCase.refresh(page, limit)

        // Assert
        assertTrue(result is Result.Success)
    }

    @Test
    fun `refresh should return Error when repository returns Error`() = runTest {
        // Arrange
        val page = 1
        val limit = 10
        val errorMessage = "Refresh failed"
        coEvery { repository.refresh(page, limit) } returns Result.Error(-1, errorMessage)

        // Act
        val result = useCase.refresh(page, limit)

        // Assert
        assertTrue(result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
    }
}
