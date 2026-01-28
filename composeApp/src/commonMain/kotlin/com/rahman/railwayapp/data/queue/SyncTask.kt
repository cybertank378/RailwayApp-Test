package com.rahman.railwayapp.data.queue

sealed class SyncTask {

    data class Create(val title: String, val description: String?) : SyncTask()
    data class Update(val id: Int, val title: String, val description: String?) : SyncTask()
    data class Delete(val id: Int) : SyncTask()
}