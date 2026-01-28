package com.rahman.railwayapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rahman.railwayapp.core.util.validator.InputValidator

data class ProductFormError(
    val title: String? = null,
    val description: String? = null
)

@Composable
fun ProductForm(
    onSubmit: (title: String, description: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(ProductFormError()) }

    fun validate(): Boolean {
        val titleResult = InputValidator.validateInput(title)
        val descResult = InputValidator.validateDescription(description)

        error = ProductFormError(
            title = titleResult.error,
            description = descResult.error
        )

        return titleResult.isValid && descResult.isValid
    }

    Column {

        // 🔹 TITLE INPUT
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                if (error.title != null) validate()
            },
            label = { Text("Title") },
            isError = error.title != null,
            supportingText = {
                error.title?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 DESCRIPTION INPUT
        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                if (error.description != null) validate()
            },
            label = { Text("Description") },
            isError = error.description != null,
            supportingText = {
                error.description?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                } ?: Text("${description.length}/1000")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 SUBMIT BUTTON
        Button(
            onClick = {
                if (validate()) {
                    onSubmit(title.trim(), description.trim().ifBlank { null })
                    title = ""
                    description = ""
                    error = ProductFormError()
                }
            },
            enabled = title.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Product")
        }
    }
}
