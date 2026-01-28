package com.rahman.railwayapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rahman.railwayapp.domain.model.Product

@Composable
fun ProductItem(
    product: Product,
    onItemClicked: () -> Unit,
    onUpdate: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier
            .padding(8.dp)) {
            Text(product.title, style = MaterialTheme.typography.titleMedium)
            Text(product.description.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Button(onClick = { onUpdate(product) }) { Text("Update") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onDelete(product) }) { Text("Delete") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onItemClicked() }) { Text("Detail") }
            }
        }
    }
}
