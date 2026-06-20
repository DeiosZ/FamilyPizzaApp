package com.example.familypizza.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.familypizza.R
import com.example.familypizza.domain.model.Product
import com.example.familypizza.presentation.components.SectionTitle
import com.example.familypizza.presentation.theme.FamilyDark
import com.example.familypizza.presentation.theme.FamilyMuted
import com.example.familypizza.presentation.theme.FamilyRed
import com.example.familypizza.presentation.theme.FamilyYellow

@Composable
fun HomeScreen(
    padding: PaddingValues,
    products: List<Product>,
    isLoading: Boolean,
    error: String,
    onOpenProducts: () -> Unit,
    onOpenDetail: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { HeroCard(onOpenProducts) }
        if (isLoading) {
            item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
        }
        if (error.isNotBlank()) {
            item { Text("Productos locales activos. API: $error", color = FamilyMuted) }
        }
        item {
            SectionTitle("Promociones destacadas")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(products.take(3), key = { it.id }) { product ->
                    PromoCard(product) { onOpenDetail(product.id) }
                }
            }
        }
        item { SectionTitle("Favoritos de la casa") }
        items(products.drop(3), key = { it.id }) { product ->
            ProductRow(product) { onOpenDetail(product.id) }
        }
    }
}

@Composable
fun HeroCard(onOpenProducts: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = FamilyRed)
    ) {
        Box {
            Image(
                painterResource(R.drawable.pizza_familiar),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier
                    .matchParentSize()
                    .backgroundBrush(Brush.verticalGradient(listOf(Color.Transparent, Color(0xDD000000))))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(18.dp)
            ) {
                Text(
                    "Pizza familiar desde S/ 19.90",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                Text("Promos calientes para compartir hoy.", color = Color.White.copy(alpha = 0.86f))
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onOpenProducts,
                    colors = ButtonDefaults.buttonColors(containerColor = FamilyYellow, contentColor = FamilyDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ver productos", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PromoCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(230.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Image(
            painterResource(product.imageRes),
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentScale = ContentScale.Crop
        )
        Column(Modifier.padding(12.dp)) {
            Text(product.name, fontWeight = FontWeight.Black)
            Text(product.price, color = FamilyRed, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProductRow(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .size(94.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.tag, color = FamilyYellow, fontWeight = FontWeight.Black)
                Text(product.name, fontWeight = FontWeight.Black)
                Text(product.description, color = FamilyMuted, maxLines = 2)
                Text(product.price, color = FamilyRed, fontWeight = FontWeight.Black)
            }
            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = FamilyRed)
        }
    }
}

private fun Modifier.backgroundBrush(brush: Brush) = drawBehind { drawRect(brush) }
