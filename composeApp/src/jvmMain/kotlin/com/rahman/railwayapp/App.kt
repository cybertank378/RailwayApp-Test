package com.rahman.railwayapp

import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import com.rahman.railwayapp.core.config.AppConfig
import com.rahman.railwayapp.presentation.detail.ProductDetailScreen
import com.rahman.railwayapp.presentation.product.ProductScreen

enum class Screen {
    ProductList,
    ProductDetail
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.ProductList) }
    var selectedProductId by remember { mutableStateOf<Int?>(null) }

    MaterialTheme {
        when (currentScreen) {
            Screen.ProductList -> ProductScreen(
                viewModel = AppConfig.productViewModel,
                onProductClick = { productId ->
                    selectedProductId = productId
                    currentScreen = Screen.ProductDetail
                }
            )

            Screen.ProductDetail -> selectedProductId?.let { id ->
                ProductDetailScreen(
                    productId = id,
                    viewModel = AppConfig.productViewModel,
                    onBack = { currentScreen = Screen.ProductList }
                )
            }
        }
    }
}
