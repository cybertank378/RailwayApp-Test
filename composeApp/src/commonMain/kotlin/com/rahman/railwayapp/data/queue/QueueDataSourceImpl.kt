package com.rahman.railwayapp.data.queue

class QueueDataSourceImpl: QueueDataSource {
    private val queue = mutableListOf<SyncTask>()

    override suspend fun enqueue(task: SyncTask) {
        queue += task
    }

    override suspend fun getAll(): List<SyncTask> = queue.toList()

    override suspend fun remove(task: SyncTask) {
        queue.remove(task)
    }
}