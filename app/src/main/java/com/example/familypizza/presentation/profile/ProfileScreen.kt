package com.example.familypizza.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.familypizza.domain.repository.UserRepository
import com.example.familypizza.presentation.components.*
import com.example.familypizza.presentation.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    repository: UserRepository,
    padding: PaddingValues,
    userId: Int?,
    name: String,
    email: String,
    phone: String,
    address: String,
    onProfileUpdated: (String, String) -> Unit,
    onLogout: () -> Unit
) {
    val scope           = rememberCoroutineScope()
    var isEditing       by remember(phone, address) { mutableStateOf(false) }
    var editablePhone   by remember(phone)          { mutableStateOf(phone) }
    var editableAddress by remember(address)        { mutableStateOf(address) }
    var message         by remember                 { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(FamilyRed, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(46.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(name,  style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text(email, color = FamilyMuted)
        Spacer(Modifier.height(28.dp))

        if (isEditing) {
            FamilyTextField(editablePhone,   { editablePhone   = it }, "Telefono",  keyboardType = KeyboardType.Phone)
            FamilyTextField(editableAddress, { editableAddress = it }, "Direccion")
            if (message.isNotBlank()) {
                Text(
                    text     = message,
                    color    = if (message.startsWith("Datos")) FamilyRed else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            PrimaryAction("Guardar cambios") {
                when {
                    userId == null           -> message = "Inicia sesion nuevamente para editar tu perfil."
                    editablePhone.isBlank()  -> message = "El telefono no puede estar vacio."
                    else -> scope.launch {
                        repository.updateProfile(userId, editablePhone.trim(), editableAddress.trim())
                        onProfileUpdated(editablePhone.trim(), editableAddress.trim())
                        isEditing = false
                        message   = "Datos actualizados."
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick  = { editablePhone = phone; editableAddress = address; message = ""; isEditing = false },
                shape    = RoundedCornerShape(8.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = FamilyMuted),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cancelar") }

        } else {
            ProfileInfo("Telefono", phone)
            ProfileInfo("Direccion", address.ifBlank { "Agrega tu direccion para pedidos delivery" })
        }

        Spacer(Modifier.weight(1f))

        if (!isEditing) {
            Button(
                onClick  = { editablePhone = phone; editableAddress = address; message = ""; isEditing = true },
                shape    = RoundedCornerShape(8.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = FamilyRed),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Editar datos") }
            Spacer(Modifier.height(10.dp))
        }

        Button(
            onClick  = onLogout,
            shape    = RoundedCornerShape(8.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = FamilyDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Logout, null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesion")
        }
    }
}