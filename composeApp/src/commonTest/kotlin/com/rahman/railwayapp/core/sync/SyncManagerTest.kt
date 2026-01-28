package com.rahman.railwayapp.core.sync

import com.rahman.railwayapp.data.queue.QueueDataSource
import com.rahman.railwayapp.data.queue.SyncTask
import com.rahman.railwayapp.data.remote.api.ProductsApi
import com.rahman.railwayapp.data.remote.dto.ProductDto
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SyncManagerTest {

    private val queue = mockk<QueueDataSource>(relaxed = true)
    private val api = mockk<ProductsApi>(relaxed = true)

    private val syncManager = SyncManager(queue, api)

    @Test
    fun `sync should execute and remove all tasks`() = runTest {
        // Arrange
        val tasks = listOf(SyncTask.Create("Title", null))
        coEvery { queue.getAll() } returns tasks

        val dummyProductDto = ProductDto(
            id = 1,
            title = "Title",
            description = null,
            userId = "cmkw683f7000yapattmw5f3f5",
            createdAt = "2026-01-28T00:00:00Z",
            updatedAt = "2026-01-28T00:00:00Z"
        )
        coEvery { api.createProduct(any(), any(), any()) } returns dummyProductDto
        coEvery { queue.remove(any()) } just Runs

        // Act
        syncManager.sync()

        // Assert
        coVerify { queue.remove(tasks.first()) }
    }




    @Test
    fun `sync should stop when api throws error`() = runTest {
        // Arrange
        val task = SyncTask.Create("Title", null)
        coEvery { queue.getAll() } returns listOf(task)
        coEvery { api.createProduct(any(), any(), any()) } throws RuntimeException()
        coEvery { queue.remove(any()) } just Runs

        // Act
        syncManager.sync()

        // Assert
        coVerify(exactly = 0) { queue.remove(any()) } // gunakan coVerify untuk suspend functions
    }

}
