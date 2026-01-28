package com.rahman.railwayapp.domain.usecase

import com.rahman.railwayapp.core.util.Result
import com.rahman.railwayapp.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateProductUseCaseTest {

    private val repository = mockk<ProductRepository>()
    private val useCase = UpdateProductUseCase(repository)

    @Test
    fun `invoke should call repository update and return success`() = runTest {
        // Arrange
        val id = 1
        val title = "New Title"
        val description = "New Desc"
        coEvery { repository.update(id, title, description) } returns Result.Success(Unit)

        // Act
        val result = useCase(id, title, description)

        // Assert
        coVerify(exactly = 1) { repository.update(id, title, description) }
        assert(result is Result.Success)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Arrange
        val id = 1
        val title = "New Title"
        val description = "New Desc"
        val exception = Exception("Update failed")
        coEvery { repository.update(id, title, description) } returns Result.Error(-1, "Update failed", exception)

        // Act
        val result = useCase(id, title, description)

        // Assert
        coVerify(exactly = 1) { repository.update(id, title, description) }
        assert(result is Result.Error)
        assertEquals("Update failed", (result as Result.Error).message)
    }
}
