package com.example.familypizza.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.familypizza.domain.model.User
import com.example.familypizza.domain.repository.UserRepository
import com.example.familypizza.presentation.components.*
import com.example.familypizza.presentation.theme.FamilyBackground
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    repository: UserRepository,
    onCreateAccount: (User) -> Unit,
    onBackToLogin: () -> Unit
) {
    val scope    = rememberCoroutineScope()
    var name     by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var phone    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message  by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FamilyBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LogoHeader(title = "Crea tu cuenta", subtitle = "Registrate y empieza tu pedido")
        FamilyTextField(value = name,     onValueChange = { name     = it }, label = "Nombre")
        FamilyTextField(value = email,    onValueChange = { email    = it }, label = "Correo",    keyboardType = KeyboardType.Email)
        FamilyTextField(value = phone,    onValueChange = { phone    = it }, label = "Telefono",  keyboardType = KeyboardType.Phone)
        FamilyTextField(value = password, onValueChange = { password = it }, label = "Contrasena", isPassword = true)
        if (message.isNotBlank()) ErrorText(message)
        PrimaryAction(text = "Crear cuenta") {
            if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                message = "Completa todos los campos."
            } else {
                scope.launch {
                    val cleanEmail = email.trim()
                    if (repository.emailExists(cleanEmail)) {
                        message = "Ese correo ya esta registrado."
                    } else {
                        val newUser = repository.register(
                            User(
                                name     = name.trim(),
                                email    = cleanEmail,
                                phone    = phone.trim(),
                                password = password
                            )
                        )
                        onCreateAccount(newUser)
                    }
                }
            }
        }
        AuthLink(text = "Ya tienes cuenta? Inicia sesion", onClick = onBackToLogin)
    }
}

