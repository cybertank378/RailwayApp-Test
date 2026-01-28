package com.rahman.railwayapp.data.local

import com.rahman.railwayapp.core.util.id.IdGenerator
import com.rahman.railwayapp.core.util.time.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ProductLocalDataSource(
    private val idGenerator: IdGenerator,
    private val timeProvider: TimeProvider
) {

    private val store = MutableStateFlow<List<ProductEntity>>(emptyList())

    fun observe(): Flow<List<ProductEntity>> = store


    fun insertOrUpdate(entity: ProductEntity) {
        val existing = store.value.indexOfFirst { it.id == entity.id }
        store.value = if (existing >= 0) {
            store.value.toMutableList().apply { set(existing, entity) }
        } else {
            store.value + entity
        }
    }

    fun replaceAll(items: List<ProductEntity>) {
        store.value = items
    }

    // RETURN ProductEntity supaya bisa pakai id untuk sync
    fun insertPending(title: String, description: String?, userId: String): ProductEntity {
        val now = timeProvider.now()
        val product = ProductEntity(
            id = idGenerator.nextId(),
            title = title,
            description = description,
            userId = userId,
            createdAt = now,
            updatedAt = now,
            synced = false
        )
        store.value += product
        return product
    }

    fun updateSynced(id: Int) {
        store.value = store.value.map {
            if (it.id == id) it.copy(synced = true) else it
        }
    }

    fun update(id: Int, title: String, description: String?) {
        val now = timeProvider.now()
        store.value = store.value.map {
            if (it.id == id) it.copy(title = title, description = description, updatedAt = now)
            else it
        }
    }

    fun getById(id: Int): ProductEntity? = store.value.find { it.id == id }



    fun delete(id: Int) {
        store.value = store.value.filterNot { it.id == id }
    }
}
