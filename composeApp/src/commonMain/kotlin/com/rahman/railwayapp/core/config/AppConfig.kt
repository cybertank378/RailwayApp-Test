package com.rahman.railwayapp.core.config

import com.rahman.railwayapp.core.logging.AppLogger
import com.rahman.railwayapp.core.logging.ConsoleLogger
import com.rahman.railwayapp.core.network.DefaultNetworkMonitor
import com.rahman.railwayapp.core.sync.RetryPolicy
import com.rahman.railwayapp.core.sync.SyncManager
import com.rahman.railwayapp.core.util.id.IdGeneratorImpl
import com.rahman.railwayapp.core.util.time.TimeProviderImpl
import com.rahman.railwayapp.data.local.ProductLocalDataSource
import com.rahman.railwayapp.data.queue.QueueDataSourceImpl
import com.rahman.railwayapp.data.remote.api.ProductsApi
import com.rahman.railwayapp.data.repository.ProductRepositoryImpl
import com.rahman.railwayapp.domain.usecase.*
import com.rahman.railwayapp.presentation.product.ProductViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object AppConfig {

    const val BASE_URL = "https://multitenant-apis-production.up.railway.app"
    const val USER_ID = "cmkw683f7000yapattmw5f3f5"

    val logger: AppLogger = ConsoleLogger

    // Data sources
    val localDataSource by lazy {
        ProductLocalDataSource(
            idGenerator = IdGeneratorImpl(),
            timeProvider = TimeProviderImpl()
        )
    }

    val networkMonitor by lazy { DefaultNetworkMonitor() }
    val queue by lazy { QueueDataSourceImpl() }

    val productsApi by lazy {
        ProductsApi(
            client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    })
                }
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            },
            baseUrl = BASE_URL
        )
    }

    // Repository
    val repository by lazy {
        ProductRepositoryImpl(
            local = localDataSource,
            remote = productsApi,
            queue = queue,
            network = networkMonitor,
            retryPolicy = RetryPolicy()
        )
    }

    // Use cases
    val getProductsUseCase by lazy { GetProductsUseCase(repository) }
    val getProductDetailUseCase by lazy { GetProductDetailUseCase(repository) }
    val createProductUseCase by lazy { CreateProductUseCase(repository) }
    val updateProductUseCase by lazy { UpdateProductUseCase(repository) }
    val deleteProductUseCase by lazy { DeleteProductUseCase(repository) }


    // Sync manager
    val syncManager by lazy { SyncManager(queue, productsApi) }

    // ViewModel
    val productViewModel by lazy {
        ProductViewModel(
            getProductsUseCase = getProductsUseCase,
            getProductDetailUseCase = getProductDetailUseCase,
            createProductUseCase = createProductUseCase,
            updateProductUseCase = updateProductUseCase,
            deleteProductUseCase = deleteProductUseCase,
            networkMonitor = networkMonitor,
            syncManager = syncManager
        )
    }
}
