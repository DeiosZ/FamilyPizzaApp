package com.example.familypizza.presentation.home

import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.familypizza.R
import com.example.familypizza.presentation.components.SectionTitle
import com.example.familypizza.presentation.navigation.Product
import com.example.familypizza.presentation.navigation.products
import com.example.familypizza.presentation.theme.*

@Composable
fun HomeScreen(
    padding: PaddingValues,
    onOpenProducts: () -> Unit,
    onOpenDetail: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { HeroCard(onOpenProducts) }
        item {
            SectionTitle("Promociones destacadas")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(products.take(3)) { PromoCard(it) { onOpenDetail(it.id) } }
            }
        }
        item { SectionTitle("Favoritos de la casa") }
        items(products.drop(3)) { ProductRow(it) { onOpenDetail(it.id) } }
    }
}

@Composable
fun HeroCard(onOpenProducts: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = FamilyRed)) {
        Box {
            Image(painterResource(R.drawable.pizza_familiar), null, Modifier.fillMaxWidth().height(240.dp), contentScale = ContentScale.Crop)
            Box(Modifier.matchParentSize().then(Modifier.background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xDD000000))))))
            Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
                Text("Pizza familiar desde S/ 19.90", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text("Promos calientes para compartir hoy.", color = Color.White.copy(alpha = 0.86f))
                Spacer(Modifier.height(12.dp))
                Button(onClick = onOpenProducts, colors = ButtonDefaults.buttonColors(containerColor = FamilyYellow, contentColor = FamilyDark), shape = RoundedCornerShape(8.dp)) {
                    Text("Ver productos", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PromoCard(product: Product, onClick: () -> Unit) {
    Card(Modifier.width(230.dp).clickable(onClick = onClick), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Image(painterResource(product.imageRes), product.name, Modifier.fillMaxWidth().height(140.dp), contentScale = ContentScale.Crop)
        Column(Modifier.padding(12.dp)) {
            Text(product.name, fontWeight = FontWeight.Black)
            Text(product.price, color = FamilyRed, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProductRow(product: Product, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(product.imageRes), product.name, Modifier.size(94.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.tag, color = FamilyYellow, fontWeight = FontWeight.Black)
                Text(product.name, fontWeight = FontWeight.Black)
                Text(product.description, color = FamilyMuted, maxLines = 2)
                Text(product.price, color = FamilyRed, fontWeight = FontWeight.Black)
            }
            Icon(Icons.Default.ShoppingCart, null, tint = FamilyRed)
        }
    }
}

// Fix for Box background
private fun Modifier.background(brush: Brush) = this.then(
    Modifier.drawBehind { drawRect(brush) }
)
