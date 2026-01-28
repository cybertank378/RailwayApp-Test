package com.rahman.railwayapp.presentation.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rahman.railwayapp.presentation.components.ConnectionStatus
import com.rahman.railwayapp.presentation.components.ProductForm
import com.rahman.railwayapp.presentation.components.ProductItem

@Composable
fun ProductScreen(
    viewModel: ProductViewModel,
    onProductClick: (productId: Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ConnectionStatus(state)

        Spacer(modifier = Modifier.height(16.dp))

        ProductForm(
            onSubmit = { title, description ->
                viewModel.create(title, description)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refresh(1, 10) }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.products) { product ->
                            ProductItem(
                                product = product,
                                onItemClicked = { onProductClick(product.id) },
                                onUpdate = { viewModel.update(it.id, it.title + " Updated", it.description + " Updated") },
                                onDelete = { viewModel.delete(it.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
