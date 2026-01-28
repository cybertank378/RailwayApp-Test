package com.rahman.railwayapp.data.repository

import com.rahman.railwayapp.core.config.AppConfig
import com.rahman.railwayapp.core.network.NetworkMonitor
import com.rahman.railwayapp.core.sync.RetryPolicy
import com.rahman.railwayapp.core.util.Result
import com.rahman.railwayapp.data.local.ProductLocalDataSource
import com.rahman.railwayapp.data.mapper.toDomain
import com.rahman.railwayapp.data.mapper.toEntity
import com.rahman.railwayapp.data.queue.QueueDataSource
import com.rahman.railwayapp.data.queue.SyncTask
import com.rahman.railwayapp.data.remote.api.ProductsApi
import com.rahman.railwayapp.domain.model.Product
import com.rahman.railwayapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val local: ProductLocalDataSource,
    private val remote: ProductsApi,
    private val queue: QueueDataSource,
    private val network: NetworkMonitor,
    private val retryPolicy: RetryPolicy
) : ProductRepository {

    private val userId = AppConfig.USER_ID

    override fun observeProducts(): Flow<List<Product>> =
        local.observe().map { it.map { entity -> entity.toDomain() } }

    override suspend fun refresh(page: Int, limit: Int): Result<Unit> {
        if (!network.isOnline()) return Result.Success(Unit)

        return try {
            retryPolicy.execute {
                val response = remote.getProducts(userId, page, limit)
                local.replaceAll(response.data.map { it.toEntity() })
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(-1, "Refresh failed", e)
        }
    }

    override suspend fun create(title: String, description: String?): Result<Unit> {
        return runCatching {
            // 1. Cek apakah produk sudah ada
            val existingProducts = local.observe().firstOrNull() ?: emptyList()
            if (existingProducts.isNotEmpty()) {
                return@runCatching Result.Success(Unit)
            }

            // 2. Insert ke local offline-first dan dapatkan entity
            val localProduct = local.insertPending(title, description, userId)

            // 3. Jika online, kirim ke API
            if (network.isOnline()) {
                remote.createProduct(userId, title, description)
                // Tandai synced = true di local
                local.updateSynced(localProduct.id)
            } else {
                // Offline → simpan di queue
                queue.enqueue(SyncTask.Create(title, description))
            }

            Result.Success(Unit)
        }.getOrElse {
            Result.Error(-1, it.message ?: "Create failed", it)
        }
    }



    override suspend fun update(id: Int, title: String, description: String?) =
        runCatching {
            local.update(id, title, description)
            if (network.isOnline()) {
                remote.updateProduct(userId, id, title, description)
            } else {
                queue.enqueue(SyncTask.Update(id, title, description))
            }
            Result.Success(Unit)
        }.getOrElse {
            Result.Error(-1, it.message ?: "Update failed", it)
        }

    override suspend fun delete(id: Int) =
        runCatching {
            local.delete(id)
            if (network.isOnline()) {
                remote.deleteProduct(userId, id)
            } else {
                queue.enqueue(SyncTask.Delete(id))
            }
            Result.Success(Unit)
        }.getOrElse {
            Result.Error(-1, it.message ?: "Delete failed", it)
        }

    override suspend fun getProductDetail(id: Int): Result<Product> {
        return runCatching {
            val localProduct = local.getById(id)
            if (localProduct != null) {
                return@runCatching Result.Success(localProduct.toDomain())
            }

            if (!network.isOnline()) return Result.Error(-1, "Offline and product not found locally")

            val response = remote.getProductById(userId, id)
            local.insertOrUpdate(response.toEntity())
            Result.Success(response.toDomain())
        }.getOrElse { e ->
            Result.Error(-1, e.message ?: "Failed to get product", e)
        }
    }
}
