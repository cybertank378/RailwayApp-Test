package com.rahman.railwayapp.presentation.product

import com.rahman.railwayapp.core.config.AppConfig
import com.rahman.railwayapp.core.network.NetworkMonitor
import com.rahman.railwayapp.core.sync.SyncManager
import com.rahman.railwayapp.core.util.Result
import com.rahman.railwayapp.domain.usecase.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ProductViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val networkMonitor: NetworkMonitor,
    private val syncManager: SyncManager
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    init {
        observeProducts()
        observeNetwork()
    }

    // ------------------------------------------------------------------------
    // Observers
    // ------------------------------------------------------------------------

    private fun observeProducts() {
        scope.launch {
            getProductsUseCase().collect { products ->
                _uiState.update { it.copy(products = products) }
            }
        }
    }

    private fun observeNetwork() {
        scope.launch {
            networkMonitor.observe().collect { online ->
                _uiState.update { it.copy(isOffline = !online) }
                if (online) {
                    AppConfig.logger.info("Network back online → trigger sync")
                    syncManager.sync()
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Actions
    // ------------------------------------------------------------------------

    fun refresh(page: Int, limit: Int) = scope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        when (val result = getProductsUseCase.refresh(page, limit)) {
            is Result.Success ->
                _uiState.update { it.copy(isLoading = false) }

            is Result.Error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
        }
    }

    fun create(title: String, description: String?) = scope.launch {
        // Mulai loading dan hapus error sebelumnya
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // Validasi input lokal
        if (title.isBlank() || title.length > 255) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Title is required and must be 1-255 characters"
                )
            }
            return@launch
        }

        if ((description?.length ?: 0) > 1000) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Description cannot exceed 1000 characters"
                )
            }
            return@launch
        }

        // Panggil usecase
        when (val result = createProductUseCase(title.trim(), description?.trim()?.ifBlank { null })) {
            is Result.Success -> {
                _uiState.update { it.copy(isLoading = false) }
                // Optional: refresh product list setelah create sukses
                refresh(1, 10)
            }

            is Result.Error -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                AppConfig.logger.error("Create failed: ${result.message}")
            }
        }
    }


    fun update(id: Int, title: String, description: String?) = scope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        when (val result = updateProductUseCase(id, title, description)) {
            is Result.Success ->
                _uiState.update { it.copy(isLoading = false) }

            is Result.Error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
        }
    }

    fun delete(id: Int) = scope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        when (val result = deleteProductUseCase(id)) {
            is Result.Success ->
                _uiState.update { it.copy(isLoading = false) }

            is Result.Error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
        }
    }

    fun getProductById(id: Int) = scope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        when (val result = getProductDetailUseCase(id)) {
            is Result.Success -> _uiState.update {
                it.copy(
                    selectedProduct = result.data,
                    isLoading = false
                )
            }
            is Result.Error -> _uiState.update {
                it.copy(
                    selectedProduct = null,
                    isLoading = false,
                    errorMessage = result.message
                )
            }
        }
    }

    fun clear() {
        scope.cancel()
    }
}
