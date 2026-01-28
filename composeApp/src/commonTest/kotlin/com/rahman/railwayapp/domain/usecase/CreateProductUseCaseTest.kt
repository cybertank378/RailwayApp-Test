package com.rahman.railwayapp.domain.usecase

import com.rahman.railwayapp.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import com.rahman.railwayapp.core.util.Result

class CreateProductUseCaseTest {

    private val repository = mockk<ProductRepository>()
    private val useCase = CreateProductUseCase(repository)

    @Test
    fun `invoke should return success when repository succeeds`() = runTest {
        // Arrange
        coEvery { repository.create(any(), any()) } returns Result.Success(Unit)

        // Act
        val result = useCase("Title", "Desc")

        // Assert
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { repository.create("Title", "Desc") }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Arrange
        coEvery { repository.create(any(), any()) } returns Result.Error(-1, "error")

        // Act
        val result = useCase("Title", null)

        // Assert
        assertTrue(result is Result.Error)
    }
}
