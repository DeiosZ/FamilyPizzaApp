package com.example.familypizza.presentation.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.familypizza.domain.model.CartItem
import com.example.familypizza.presentation.theme.*

@Composable
fun CartScreen(
    padding   : PaddingValues,
    uiState   : CartUiState,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onRemove  : (Int) -> Unit,
    onCheckout: () -> Unit
) {
    if (uiState.items.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🛒", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(12.dp))
                Text("Tu carrito está vacío", style = MaterialTheme.typography.titleMedium, color = FamilyMuted)
            }
        }
        return
    }

    Column(Modifier.fillMaxSize().padding(padding)) {
        LazyColumn(
            modifier            = Modifier.weight(1f),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.items, key = { it.productId }) { item ->
                CartItemRow(
                    item       = item,
                    onIncrease = { onIncrease(item.productId) },
                    onDecrease = { onDecrease(item.productId) },
                    onRemove   = { onRemove(item.productId) }
                )
            }
        }

        // ── Resumen y botón ─────────────────────────────────────────────────
        Surface(shadowElevation = 8.dp, color = Color.White) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Productos (${uiState.count})", color = FamilyMuted)
                    Text("S/ ${"%.2f".format(uiState.total)}", fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Delivery", color = FamilyMuted)
                    Text("Gratis", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
                Divider(Modifier.padding(vertical = 8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    Text("S/ ${"%.2f".format(uiState.total)}", style = MaterialTheme.typography.titleMedium, color = FamilyRed, fontWeight = FontWeight.Black)
                }
                if (uiState.error.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(uiState.error, color = Color(0xFFB71C1C), fontWeight = FontWeight.Bold)
                }
                if (uiState.isLoading) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick  = onCheckout,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = FamilyRed),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text("Hacer pedido", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item      : CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove  : () -> Unit
) {
    Card(
        shape   = RoundedCornerShape(8.dp),
        colors  = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter           = painterResource(item.imageRes),
                contentDescription= item.name,
                modifier          = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp)),
                contentScale      = ContentScale.Crop
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Black, maxLines = 1)
                Text("S/ ${"%.2f".format(item.price)}", color = FamilyMuted)
                Text("Subtotal: S/ ${"%.2f".format(item.subtotal)}", color = FamilyRed, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFFB71C1C), modifier = Modifier.size(18.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Remove, null, tint = FamilyRed, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text     = "${item.quantity}",
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .background(FamilyBackground, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, null, tint = FamilyRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
