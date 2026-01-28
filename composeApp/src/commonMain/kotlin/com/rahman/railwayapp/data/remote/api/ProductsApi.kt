package com.rahman.railwayapp.data.remote.api

import com.rahman.railwayapp.core.logging.AppLogger
import com.rahman.railwayapp.data.remote.dto.ProductDto
import com.rahman.railwayapp.data.remote.dto.ProductListResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ProductsApi(private val client: HttpClient, private val baseUrl: String) {

    suspend fun getProducts(userId: String, page: Int = 1, limit: Int = 20): ProductListResponse {

        return client.get("$baseUrl/products/$userId") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    suspend fun getProductById(userId: String, id: Int): ProductDto {
        return client.get("$baseUrl/products/$userId/$id").body()
    }

    suspend fun createProduct(userId: String, title: String, description: String?): ProductDto {
        return client.post("$baseUrl/products/$userId") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("title" to title, "description" to description))
        }.body()
    }

    suspend fun updateProduct(userId: String, id: Int, title: String, description: String?): ProductDto {
        return client.put("$baseUrl/products/$userId/$id") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("title" to title, "description" to description))
        }.body()
    }

    suspend fun deleteProduct(userId: String, id: Int): Boolean {
        val response: HttpResponse = client.delete("$baseUrl/products/$userId/$id")
        return response.status.value in 200..299
    }
}
