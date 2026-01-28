package com.rahman.railwayapp.presentation.product

import com.rahman.railwayapp.domain.model.Product

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val selectedProduct: Product? = null,
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val isSyncing: Boolean = false,
    val pendingQueueCount: Int = 0,
    val errorMessage: String? = null
)
