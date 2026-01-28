package com.rahman.railwayapp.core.sync

import com.rahman.railwayapp.core.config.AppConfig
import com.rahman.railwayapp.data.queue.QueueDataSource
import com.rahman.railwayapp.data.queue.SyncTask
import com.rahman.railwayapp.data.remote.api.ProductsApi

class SyncManager(
    private val queue: QueueDataSource,
    private val api: ProductsApi
) {

    suspend fun sync() {
        val userId = AppConfig.USER_ID
        val tasks = queue.getAll()

        for (task in tasks) {
            try {
                when (task) {
                    is SyncTask.Create ->
                        api.createProduct(userId, task.title, task.description)

                    is SyncTask.Update ->
                        api.updateProduct(userId, task.id, task.title, task.description)

                    is SyncTask.Delete ->
                        api.deleteProduct(userId, task.id)
                }

                queue.remove(task)

            } catch (_: Exception) {
                return // stop sync jika gagal
            }
        }
    }
}
