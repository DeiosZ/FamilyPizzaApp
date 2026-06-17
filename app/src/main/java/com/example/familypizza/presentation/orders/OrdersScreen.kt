package com.example.familypizza.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.familypizza.domain.model.Order
import com.example.familypizza.domain.model.OrderStatus
import com.example.familypizza.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(
    padding : PaddingValues,
    orders  : List<Order>,
    onCancel: (Int) -> Unit
) {
    if (orders.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📋", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(12.dp))
                Text("Sin pedidos aún", style = MaterialTheme.typography.titleMedium, color = FamilyMuted)
            }
        }
        return
    }

    LazyColumn(
        modifier            = Modifier.fillMaxSize().padding(padding),
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Mis pedidos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(4.dp))
        }
        items(orders, key = { it.id }) { order ->
            OrderCard(order = order, onCancel = { onCancel(order.id) })
        }
    }
}

@Composable
private fun OrderCard(order: Order, onCancel: () -> Unit) {
    val dateStr = remember(order.createdAt) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(order.createdAt))
    }

    Card(
        shape     = RoundedCornerShape(8.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {

            // ── Cabecera ────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Pedido #${order.id}", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                StatusChip(order.status)
            }
            Text(dateStr, color = FamilyMuted, style = MaterialTheme.typography.labelSmall)
            Divider(Modifier.padding(vertical = 8.dp))

            // ── Items ───────────────────────────────────────────────────────
            order.items.forEach { item ->
                Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${item.quantity}x ${item.name}", modifier = Modifier.weight(1f), maxLines = 1)
                    Text("S/ ${"%.2f".format(item.subtotal)}", color = FamilyMuted)
                }
            }
            Divider(Modifier.padding(vertical = 8.dp))

            // ── Total ───────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Black)
                Text("S/ ${"%.2f".format(order.total)}", color = FamilyRed, fontWeight = FontWeight.Black)
            }

            // ── Botón cancelar solo en PENDIENTE ────────────────────────────
            if (order.status == OrderStatus.PENDIENTE) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick  = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB71C1C)),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB71C1C))
                ) {
                    Text("Cancelar pedido", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: OrderStatus) {
    val (bg, fg) = when (status) {
        OrderStatus.PENDIENTE  -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        OrderStatus.EN_CAMINO  -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        OrderStatus.ENTREGADO  -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        OrderStatus.CANCELADO  -> Color(0xFFFFEBEE) to Color(0xFFB71C1C)
    }
    Surface(color = bg, shape = RoundedCornerShape(20.dp)) {
        Text(
            text     = status.label,
            color    = fg,
            fontWeight = FontWeight.Bold,
            style    = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}