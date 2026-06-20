package com.example.familypizza.presentation.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.familypizza.domain.model.User
import com.example.familypizza.domain.repository.UserRepository
import com.example.familypizza.presentation.components.*
import com.example.familypizza.presentation.theme.FamilyBackground
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    repository: UserRepository,
    onLoginSuccess: (User) -> Unit,
    onAdminLogin: () -> Unit,
    onRegister: () -> Unit
) {
    val context = LocalContext.current
    val scope     = rememberCoroutineScope()
    var userInput by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var message   by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    fun webClientId(): String? {
        val id = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        return if (id == 0) null else context.getString(id)
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val account = runCatching {
            GoogleSignIn.getSignedInAccountFromIntent(result.data).result
        }.getOrNull()
        val token = account?.idToken
        if (token.isNullOrBlank()) {
            message = "Google Sign-In no esta configurado. Agrega SHA-1/SHA-256 en Firebase."
            isLoading = false
        } else {
            scope.launch {
                val user = runCatching { repository.loginWithGoogle(token) }.getOrNull()
                isLoading = false
                if (user == null) message = "No se pudo iniciar sesion con Google."
                else onLoginSuccess(user)
            }
        }
    }

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
                    val login = userInput.trim()
                    if (login.equals("admin@familypizza.com", ignoreCase = true) && password == "admin123") {
                        isLoading = true
                        val firebaseReady = repository.ensureAdminSession(login, password)
                        isLoading = false
                        if (!firebaseReady) {
                            message = "Admin local activo. Revisa Email/Password en Firebase para guardar en Firestore."
                        }
                        onAdminLogin()
                    } else {
                        isLoading = true
                        val found = repository.loginWithEmail(login, password)
                        isLoading = false
                        message = when {
                            found == null -> "Usuario no registrado."
                            else -> {
                                onLoginSuccess(found)
                                ""
                            }
                        }
                    }
                }
            }
        }
        OutlinedButton(
            onClick = {
                val clientId = webClientId()
                if (clientId == null) {
                    message = "Para Google debes agregar SHA-1/SHA-256 en Firebase y volver a descargar google-services.json."
                    return@OutlinedButton
                }
                isLoading = true
                val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(clientId)
                    .requestEmail()
                    .build()
                googleLauncher.launch(GoogleSignIn.getClient(context, options).signInIntent)
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = null)
            Text("  Ingresar con Google", fontWeight = FontWeight.Bold)
        }
        AuthLink(text = "No tienes cuenta? Registrate", onClick = onRegister)
    }
}
