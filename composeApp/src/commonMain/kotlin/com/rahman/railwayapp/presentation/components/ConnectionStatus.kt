package com.rahman.railwayapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rahman.railwayapp.presentation.product.ProductUiState

@Composable
fun ConnectionStatus(state: ProductUiState) {
    val (color, text) = when {
        state.isOffline -> Color.Red to "Offline - perubahan akan disinkron saat online"
        state.isSyncing -> Color.Blue to "Sedang menyinkron..."
        state.errorMessage != null -> Color(0xFFFFA500) to "Error sinkronisasi - Coba lagi dalam beberapa saat Lagi"
        else -> Color.Green to "Terhubung dan tersinkron"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.2f))
            .padding(8.dp)
    ) {
        Box(modifier = Modifier.size(12.dp).background(color))
        Spacer(Modifier.width(8.dp))
        Text(text)
        Spacer(Modifier.weight(1f))
        Text("Pending: ${state.pendingQueueCount}", style = MaterialTheme.typography.bodySmall)
    }
}
