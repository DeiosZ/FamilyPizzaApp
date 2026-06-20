package com.example.familypizza.presentation.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.familypizza.presentation.theme.FamilyMuted
import com.example.familypizza.presentation.theme.FamilyRed

@Composable
fun AdminDashboardScreen(
    padding: PaddingValues,
    onProducts: () -> Unit,
    onOrders: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Panel administrador", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text("Gestiona productos y pedidos de FamilyPizza.", color = FamilyMuted)

        Button(
            onClick = onProducts,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FamilyRed)
        ) {
            Icon(Icons.Default.Inventory, contentDescription = null)
            Text("  Mantenimiento de productos", fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onOrders,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FamilyRed)
        ) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null)
            Text("  Gestion de pedidos", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Text("  Cerrar sesion", fontWeight = FontWeight.Bold)
        }
    }
}
