package com.example.familypizza.presentation.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.familypizza.domain.model.Product
import com.example.familypizza.presentation.home.ProductRow
import com.example.familypizza.presentation.theme.FamilyMuted

@Composable
fun ProductsScreen(
    padding: PaddingValues,
    products: List<Product>,
    isLoading: Boolean,
    error: String,
    onRetry: () -> Unit,
    onOpenDetail: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Lista de productos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text("Elige una promocion y mira su detalle.", color = FamilyMuted)
            Spacer(modifier = Modifier.height(6.dp))
        }
        if (isLoading) {
            item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
        }
        if (error.isNotBlank()) {
            item {
                Column {
                    Text("No se pudo actualizar desde la API. Mostrando datos locales.", color = FamilyMuted)
                    TextButton(onClick = onRetry) { Text("Reintentar") }
                }
            }
        }
        items(products, key = { it.id }) { product ->
            ProductRow(product = product) { onOpenDetail(product.id) }
        }
    }
}
