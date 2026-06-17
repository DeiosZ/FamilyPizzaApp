package com.example.familypizza.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.familypizza.domain.model.User
import com.example.familypizza.domain.repository.UserRepository
import com.example.familypizza.presentation.components.*
import com.example.familypizza.presentation.theme.FamilyBackground
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    repository: UserRepository,
    onLoginSuccess: (User) -> Unit,
    onRegister: () -> Unit
) {
    val scope     = rememberCoroutineScope()
    var userInput by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var message   by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FamilyBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LogoHeader(
            title    = "Bienvenido a FamilyPizza",
            subtitle = "Inicia sesion para pedir tus favoritos"
        )
        FamilyTextField(value = userInput, onValueChange = { userInput = it }, label = "Usuario o correo")
        FamilyTextField(value = password,  onValueChange = { password  = it }, label = "Contrasena", isPassword = true)
        if (message.isNotBlank()) ErrorText(message)
        PrimaryAction(text = "Ingresar") {
            if (userInput.isBlank() || password.isBlank()) {
                message = "Completa usuario y contrasena."
            } else {
                scope.launch {
                    val found = repository.login(userInput.trim())
                    message = when {
                        found == null              -> "Usuario no registrado."
                        found.password != password -> "Contrasena incorrecta."
                        else                       -> { onLoginSuccess(found); "" }
                    }
                }
            }
        }
        AuthLink(text = "No tienes cuenta? Registrate", onClick = onRegister)
    }
}