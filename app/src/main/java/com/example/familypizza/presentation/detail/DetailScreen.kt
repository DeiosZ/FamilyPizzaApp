package com.example.familypizza.presentation.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.familypizza.presentation.components.PrimaryAction
import com.example.familypizza.presentation.navigation.Product
import com.example.familypizza.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(product: Product, onBack: () -> Unit, onAddToCart: (Product) -> Unit) {
    var added by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = FamilyRed, titleContentColor = Color.White, navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = FamilyBackground
    ) { padding ->
        LazyColumn(
            modifier            = Modifier.fillMaxSize().padding(padding),
            contentPadding      = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Image(
                    painterResource(product.imageRes), product.name,
                    Modifier.fillMaxWidth().height(330.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            item {
                Text(product.tag.uppercase(), color = FamilyYellow, fontWeight = FontWeight.Black)
                Text(product.name,  style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                Text(product.price, color = FamilyRed, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(10.dp))
                Text(product.description, color = FamilyMuted)
                Spacer(Modifier.height(20.dp))

                if (added) {
                    Button(
                        onClick  = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(8.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Text("✓ Agregado — Ver carrito", fontWeight = FontWeight.Black)
                    }
                } else {
                    PrimaryAction(text = "Agregar al carrito") {
                        onAddToCart(product)
                        added = true
                    }
                }
            }
        }
    }
}