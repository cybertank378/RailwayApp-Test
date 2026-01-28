package com.rahman.railwayapp.data.repository

import com.rahman.railwayapp.core.config.AppConfig
import com.rahman.railwayapp.core.network.NetworkMonitor
import com.rahman.railwayapp.core.sync.RetryPolicy
import com.rahman.railwayapp.core.util.Result
import com.rahman.railwayapp.data.local.ProductLocalDataSource
import com.rahman.railwayapp.data.mapper.toDomain
import com.rahman.railwayapp.data.queue.QueueDataSource
import com.rahman.railwayapp.data.queue.SyncTask
import com.rahman.railwayapp.data.remote.api.ProductsApi
import com.rahman.railwayapp.data.remote.dto.*
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class ProductRepositoryImplTest {

    private val local = mockk<ProductLocalDataSource>(relaxed = true)
    private val remote = mockk<ProductsApi>()
    private val queue = mockk<QueueDataSource>(relaxed = true)
    private val network = mockk<NetworkMonitor>()
    private val retryPolicy = RetryPolicy()

    private lateinit var repository: ProductRepositoryImpl

    @BeforeTest
    fun setup() {
        repository = ProductRepositoryImpl(
            local = local,
            remote = remote,
            queue = queue,
            network = network,
            retryPolicy = retryPolicy
        )
    }

    // ------------------------------------------------------------------------
    // OBSERVE PRODUCTS
    // ------------------------------------------------------------------------

    @Test
    fun `observeProducts should emit mapped domain products`() = runTest {
        // Arrange
        val entity = mockk<com.rahman.railwayapp.data.local.ProductEntity>()
        every { local.observe() } returns flowOf(listOf(entity))
        every { entity.toDomain() } returns mockk()

        // Act
        val result = repository.observeProducts()

        // Assert
        result.collect {
            assertEquals(1, it.size)
        }
    }

    // ------------------------------------------------------------------------
    // REFRESH
    // ------------------------------------------------------------------------

    @Test
    fun `refresh should fetch remote data when online`() = runTest {
        // Arrange
        every { network.isOnline() } returns true

        val dto = ProductDto(
            id = 1,
            title = "Title",
            description = "Desc",
            userId = AppConfig.USER_ID,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        coEvery {
            remote.getProducts(AppConfig.USER_ID, 1, 10)
        } returns ProductListResponse(
            data = listOf(dto),
            pagination = PaginationDto(1, 1, 1, pages = 12 )
        )

        // Act
        val result = repository.refresh(1, 10)

        // Assert
        assertTrue(result is Result.Success)
        coVerify { remote.getProducts(AppConfig.USER_ID, 1, 10) }
        verify { local.replaceAll(any()) }
    }

    @Test
    fun `refresh should return success without calling remote when offline`() = runTest {
        // Arrange
        every { network.isOnline() } returns false

        // Act
        val result = repository.refresh(1, 10)

        // Assert
        assertTrue(result is Result.Success)
        coVerify(exactly = 0) { remote.getProducts(any(), any(), any()) }
    }

    // ------------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------------

    @Test
    fun `create should enqueue sync task when offline`() = runTest {
        // Arrange
        every { network.isOnline() } returns false

        coEvery {
            local.insertPending("Title", "Desc", AppConfig.USER_ID)
        } just Runs

        coEvery {
            queue.enqueue(any())
        } just Runs

        // Act
        val result = repository.create("Title", "Desc")

        // Assert
        assertTrue(result is Result.Success)

        coVerify {
            local.insertPending("Title", "Desc", AppConfig.USER_ID)
        }

        coVerify {
            queue.enqueue(SyncTask.Create("Title", "Desc"))
        }
    }

    @Test
    fun `create should not enqueue sync task when online`() = runTest {
        // Arrange
        every { network.isOnline() } returns true

        coEvery {
            local.insertPending("Title", null, AppConfig.USER_ID)
        } just Runs

        // Act
        val result = repository.create("Title", null)

        // Assert
        assertTrue(result is Result.Success)

        coVerify {
            local.insertPending("Title", null, AppConfig.USER_ID)
        }

        coVerify(exactly = 0) {
            queue.enqueue(any())
        }
    }


    // ------------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------------

    @Test
    fun `update should call remote when online`() = runTest {
        // Arrange
        every { network.isOnline() } returns true

        coEvery {
            local.update(1, "New", "Desc")
        } just Runs

        coEvery {
            remote.updateProduct(any(), any(), any(), any())
        } just Awaits

        // Act
        val result = repository.update(1, "New", "Desc")

        // Assert
        assertTrue(result is Result.Success)

        coVerify {
            local.update(1, "New", "Desc")
        }

        coVerify {
            remote.updateProduct(AppConfig.USER_ID, 1, "New", "Desc")
        }

        coVerify(exactly = 0) {
            queue.enqueue(any())
        }
    }


    @Test
    fun `update should enqueue sync task when offline`() = runTest {
        // Arrange
        every { network.isOnline() } returns false

        coEvery {
            local.update(1, "New", "Desc")
        } just Runs

        coEvery {
            queue.enqueue(any())
        } just Runs

        // Act
        val result = repository.update(1, "New", "Desc")

        // Assert
        assertTrue(result is Result.Success)

        coVerify {
            local.update(1, "New", "Desc")
        }

        coVerify {
            queue.enqueue(SyncTask.Update(1, "New", "Desc"))
        }

        coVerify(exactly = 0) {
            remote.updateProduct(any(), any(), any(), any())
        }
    }


    // ------------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------------

    @Test
    fun `delete should call remote when online`() = runTest {
        // Arrange
        every { network.isOnline() } returns true
        coEvery { remote.deleteProduct(any(), any()) } just Awaits

        // Act
        val result = repository.delete(1)

        // Assert
        assertTrue(result is Result.Success)
        coVerify { remote.deleteProduct(AppConfig.USER_ID, 1) }
    }

    @Test
    fun `delete should enqueue sync task when offline`() = runTest {
        // Arrange
        every { network.isOnline() } returns false

        coEvery {
            local.delete(1)
        } just Runs

        coEvery {
            queue.enqueue(any())
        } just Runs

        // Act
        val result = repository.delete(1)

        // Assert
        assertTrue(result is Result.Success)

        coVerify {
            local.delete(1)
        }

        coVerify {
            queue.enqueue(SyncTask.Delete(1))
        }

        coVerify(exactly = 0) {
            remote.deleteProduct(any(), any())
        }
    }

}
