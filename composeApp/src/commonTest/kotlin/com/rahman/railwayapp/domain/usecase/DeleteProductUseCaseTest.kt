package com.rahman.railwayapp.domain.usecase

import com.rahman.railwayapp.core.util.Result
import com.rahman.railwayapp.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteProductUseCaseTest {

    private val repository = mockk<ProductRepository>()
    private val useCase = DeleteProductUseCase(repository)

    @Test
    fun `invoke should return Success when repository returns Success`() = runTest {
        // Arrange
        val productId = 1
        coEvery { repository.delete(productId) } returns Result.Success(Unit)

        // Act
        val result = useCase(productId)

        // Assert
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { repository.delete(productId) }
    }

    @Test
    fun `invoke should return Error when repository returns Error`() = runTest {
        // Arrange
        val productId = 1
        val errorMessage = "Delete failed"
        coEvery { repository.delete(productId) } returns Result.Error(-1, errorMessage)

        // Act
        val result = useCase(productId)

        // Assert
        assertTrue(result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
        coVerify(exactly = 1) { repository.delete(productId) }
    }
}
