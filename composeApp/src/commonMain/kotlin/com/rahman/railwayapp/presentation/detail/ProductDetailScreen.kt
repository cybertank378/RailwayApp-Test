package com.rahman.railwayapp.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rahman.railwayapp.presentation.product.ProductViewModel

@Composable
fun ProductDetailScreen(
    productId: Int,
    viewModel: ProductViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.getProductById(productId)
    }

    val product = state.selectedProduct

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(onClick = onBack) { Text("Back") }
        }

        item {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.getProductById(productId) }) {
                            Text("Retry")
                        }
                    }
                }
                product != null -> {
                    Text("Title: ${product.title}", style = MaterialTheme.typography.titleMedium)
                    Text("Description: ${product.description.orEmpty()}", style = MaterialTheme.typography.bodyMedium)
                    Text("User ID: ${product.userId}", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        Button(onClick = { viewModel.update(product.id, product.title + " Updated", product.description + " Updated") }) {
                            Text("Update")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { viewModel.delete(product.id) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}
