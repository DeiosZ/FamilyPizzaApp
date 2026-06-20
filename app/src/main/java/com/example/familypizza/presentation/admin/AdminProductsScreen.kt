package com.example.familypizza.presentation.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.familypizza.R
import com.example.familypizza.domain.model.Product
import com.example.familypizza.presentation.theme.FamilyMuted
import com.example.familypizza.presentation.theme.FamilyRed

private data class ImageOption(val label: String, val resId: Int)

private val imageOptions = listOf(
    ImageOption("Pizza familiar", R.drawable.pizza_familiar),
    ImageOption("2x1 familiar", R.drawable.promo_2x1_pizza_familiar),
    ImageOption("Combo pizzas y alitas", R.drawable.combo_pizzas_alitas),
    ImageOption("Pizza burger", R.drawable.pizza_burger),
    ImageOption("Familiar + lasagna", R.drawable.pizza_familiar_mas_lasana)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    padding: PaddingValues,
    uiState: AdminProductsUiState,
    onSave: (Product) -> Unit,
    onDelete: (Int) -> Unit
) {
    var editingId by remember { mutableStateOf<Int?>(null) }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("Oferta") }
    var imageRes by remember { mutableStateOf(imageOptions.first().resId) }
    var imageExpanded by remember { mutableStateOf(false) }

    fun clearForm() {
        editingId = null
        name = ""
        price = ""
        description = ""
        tag = "Oferta"
        imageRes = imageOptions.first().resId
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Mantenimiento de productos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text("Agrega, edita o elimina productos del catalogo.", color = FamilyMuted)
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(8.dp)) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (editingId == null) "Nuevo producto" else "Editar producto", fontWeight = FontWeight.Black)
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Precio") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripcion") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tag, onValueChange = { tag = it }, label = { Text("Etiqueta") }, modifier = Modifier.fillMaxWidth())

                    ExposedDropdownMenuBox(expanded = imageExpanded, onExpandedChange = { imageExpanded = !imageExpanded }) {
                        OutlinedTextField(
                            value = imageOptions.firstOrNull { it.resId == imageRes }?.label.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Imagen") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = imageExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = imageExpanded, onDismissRequest = { imageExpanded = false }) {
                            imageOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        imageRes = option.resId
                                        imageExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val cleanPrice = price.replace(",", ".").toDoubleOrNull() ?: 0.0
                                if (name.isBlank() || cleanPrice <= 0.0) return@Button
                                val id = editingId ?: ((uiState.products.maxOfOrNull { it.id } ?: 0) + 1)
                                onSave(
                                    Product(
                                        id = id,
                                        name = name.trim(),
                                        price = "S/ ${"%.2f".format(cleanPrice)}",
                                        priceValue = cleanPrice,
                                        description = description.trim(),
                                        tag = tag.trim().ifBlank { "Oferta" },
                                        imageRes = imageRes
                                    )
                                )
                                clearForm()
                            }
                        ) {
                            Text("Guardar")
                        }
                        Button(onClick = { clearForm() }) {
                            Text("Limpiar")
                        }
                    }
                    if (uiState.message.isNotBlank()) {
                        Text(uiState.message, color = FamilyRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(uiState.products, key = { it.id }) { product ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(8.dp)) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(product.imageRes),
                        contentDescription = product.name,
                        modifier = Modifier.size(70.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                        Text(product.name, fontWeight = FontWeight.Black)
                        Text(product.price, color = FamilyRed, fontWeight = FontWeight.Bold)
                        Text(product.tag, color = FamilyMuted)
                    }
                    IconButton(
                        onClick = {
                            editingId = product.id
                            name = product.name
                            price = product.priceValue.toString()
                            description = product.description
                            tag = product.tag
                            imageRes = product.imageRes
                        }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { onDelete(product.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = FamilyRed)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}
