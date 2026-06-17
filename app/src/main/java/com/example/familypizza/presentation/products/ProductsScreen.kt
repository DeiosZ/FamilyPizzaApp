package com.example.familypizza.presentation.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.familypizza.presentation.home.ProductRow
import com.example.familypizza.presentation.navigation.products
import com.example.familypizza.presentation.theme.FamilyMuted

@Composable
fun ProductsScreen(padding: PaddingValues, onOpenDetail: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Lista de productos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text("Elige una promocion y mira su detalle.", color = FamilyMuted)
            Spacer(modifier = Modifier.height(6.dp))
        }
        items(products) { product ->
            ProductRow(product = product) { onOpenDetail(product.id) }
        }
    }
}