package com.rahman.railwayapp.data.queue

interface QueueDataSource {
    suspend fun enqueue(task: SyncTask)
    suspend fun getAll(): List<SyncTask>
    suspend fun remove(task: SyncTask)
}