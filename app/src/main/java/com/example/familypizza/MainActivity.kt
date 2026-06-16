package com.example.familypizza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.familypizza.data.FamilyPizzaDatabase
import com.example.familypizza.data.UserDao
import com.example.familypizza.data.UserEntity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FamilyPizzaTheme {
                FamilyPizzaApp(FamilyPizzaDatabase.getInstance(this).userDao())
            }
        }
    }
}

private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PRODUCTS = "products"
    const val DETAIL = "detail/{productId}"
    const val PROFILE = "profile"

    fun detail(productId: Int) = "detail/$productId"
}

private data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val description: String,
    val tag: String,
    @DrawableRes val imageRes: Int
)

private val products = listOf(
    Product(
        id = 1,
        name = "Pizza Familiar",
        price = "S/ 19.90",
        description = "Americana, pepperoni o mozzarella. Pizza familiar de 33 cm de diametro.",
        tag = "Oferta",
        imageRes = R.drawable.pizza_familiar
    ),
    Product(
        id = 2,
        name = "2x1 Familiar",
        price = "S/ 39.90",
        description = "Dos pizzas familiares: americana, pepperoni, mozzarella y cordon blue.",
        tag = "Promo",
        imageRes = R.drawable.promo_2x1_pizza_familiar
    ),
    Product(
        id = 3,
        name = "Combo Extra Cheese",
        price = "S/ 50.00",
        description = "Dos pizzas medianas, seis alitas BBQ, buffalo o acevichada y Pepsi de 1 litro.",
        tag = "Combo",
        imageRes = R.drawable.combo_pizzas_alitas
    ),
    Product(
        id = 4,
        name = "Pizza Burger",
        price = "Desde S/ 10.90",
        description = "Queso mozzarella, carne, tocino crujiente y salsa BBQ.",
        tag = "Nuevo",
        imageRes = R.drawable.pizza_burger
    ),
    Product(
        id = 5,
        name = "Familiar + Lasagna",
        price = "S/ 34.90",
        description = "Pizza familiar con lasagna personal. No valido para suprema, dos estaciones ni cuatro estaciones.",
        tag = "Especial",
        imageRes = R.drawable.pizza_familiar_mas_lasana
    )
)

@Composable
private fun FamilyPizzaApp(userDao: UserDao) {
    val navController = rememberNavController()
    var profileName by remember { mutableStateOf("Cliente FamilyPizza") }
    var profileEmail by remember { mutableStateOf("cliente@familypizza.pe") }
    var profilePhone by remember { mutableStateOf("986 732 411") }
    var profileAddress by remember { mutableStateOf("") }
    var currentUserId by remember { mutableStateOf<Int?>(null) }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                userDao = userDao,
                onLoginSuccess = { user ->
                    currentUserId = user.id
                    profileName = user.name
                    profileEmail = user.email
                    profilePhone = user.phone
                    profileAddress = user.address
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                userDao = userDao,
                onCreateAccount = { user ->
                    currentUserId = user.id
                    profileName = user.name
                    profileEmail = user.email
                    profilePhone = user.phone
                    profileAddress = user.address
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable(Routes.HOME) {
            MainScaffold(navController = navController, selectedRoute = Routes.HOME) { padding ->
                HomeScreen(
                    padding = padding,
                    onOpenProducts = { navController.navigate(Routes.PRODUCTS) },
                    onOpenDetail = { navController.navigate(Routes.detail(it)) }
                )
            }
        }
        composable(Routes.PRODUCTS) {
            MainScaffold(navController = navController, selectedRoute = Routes.PRODUCTS) { padding ->
                ProductsScreen(
                    padding = padding,
                    onOpenDetail = { navController.navigate(Routes.detail(it)) }
                )
            }
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { entry ->
            val productId = entry.arguments?.getInt("productId") ?: 1
            val product = products.firstOrNull { it.id == productId } ?: products.first()
            DetailScreen(product = product, onBack = { navController.popBackStack() })
        }
        composable(Routes.PROFILE) {
            MainScaffold(navController = navController, selectedRoute = Routes.PROFILE) { padding ->
                ProfileScreen(
                    userDao = userDao,
                    padding = padding,
                    userId = currentUserId,
                    name = profileName,
                    email = profileEmail,
                    phone = profilePhone,
                    address = profileAddress,
                    onProfileUpdated = { phone, address ->
                        profilePhone = phone
                        profileAddress = address
                    },
                    onLogout = {
                        currentUserId = null
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LoginScreen(
    userDao: UserDao,
    onLoginSuccess: (UserEntity) -> Unit,
    onRegister: () -> Unit
) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    AuthLayout {
        LogoHeader(title = "Bienvenido a FamilyPizza", subtitle = "Inicia sesion para pedir tus favoritos")
        FamilyTextField(value = user, onValueChange = { user = it }, label = "Usuario o correo")
        FamilyTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contrasena",
            isPassword = true
        )
        if (message.isNotBlank()) ErrorText(message)
        PrimaryAction(text = "Ingresar") {
            if (user.isBlank() || password.isBlank()) {
                message = "Completa usuario y contrasena."
            } else {
                scope.launch {
                    val savedUser = userDao.findByLogin(user.trim())
                    message = when {
                        savedUser == null -> "Usuario no registrado."
                        savedUser.password != password -> "Contrasena incorrecta."
                        else -> {
                            onLoginSuccess(savedUser)
                            ""
                        }
                    }
                }
            }
        }
        AuthLink(text = "No tienes cuenta? Registrate", onClick = onRegister)
    }
}

@Composable
private fun RegisterScreen(
    userDao: UserDao,
    onCreateAccount: (UserEntity) -> Unit,
    onBackToLogin: () -> Unit
) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    AuthLayout {
        LogoHeader(title = "Crea tu cuenta", subtitle = "Registrate y empieza tu pedido")
        FamilyTextField(value = name, onValueChange = { name = it }, label = "Nombre")
        FamilyTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo",
            keyboardType = KeyboardType.Email
        )
        FamilyTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Telefono",
            keyboardType = KeyboardType.Phone
        )
        FamilyTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contrasena",
            isPassword = true
        )
        if (message.isNotBlank()) ErrorText(message)
        PrimaryAction(text = "Crear cuenta") {
            if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                message = "Completa todos los campos."
            } else {
                scope.launch {
                    val cleanEmail = email.trim()
                    if (userDao.countByEmail(cleanEmail) > 0) {
                        message = "Ese correo ya esta registrado."
                    } else {
                        val newUser = UserEntity(
                            name = name.trim(),
                            email = cleanEmail,
                            phone = phone.trim(),
                            password = password
                        )
                        val newUserId = userDao.insert(newUser).toInt()
                        onCreateAccount(newUser.copy(id = newUserId))
                    }
                }
            }
        }
        AuthLink(text = "Ya tienes cuenta? Inicia sesion", onClick = onBackToLogin)
    }
}

@Composable
private fun AuthLayout(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FamilyBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content
    )
}

@Composable
private fun LogoHeader(title: String, subtitle: String) {
    Image(
        painter = painterResource(R.drawable.logosinfondo),
        contentDescription = "FamilyPizza",
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Fit
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
    Text(subtitle, color = FamilyMuted, style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(28.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold(
    navController: NavController,
    selectedRoute: String,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FamilyPizza", fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = FamilyRed,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            FamilyBottomBar(navController = navController, selectedRoute = selectedRoute)
        },
        containerColor = FamilyBackground,
        content = content
    )
}

@Composable
private fun HomeScreen(
    padding: PaddingValues,
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
        item {
            HeroCard(onOpenProducts = onOpenProducts)
        }
        item {
            SectionTitle("Promociones destacadas")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(products.take(3)) { product ->
                    PromoCard(product = product, onClick = { onOpenDetail(product.id) })
                }
            }
        }
        item {
            SectionTitle("Favoritos de la casa")
        }
        items(products.drop(3)) { product ->
            ProductRow(product = product, onClick = { onOpenDetail(product.id) })
        }
    }
}

@Composable
private fun ProductsScreen(
    padding: PaddingValues,
    onOpenDetail: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Lista de productos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text("Elige una promocion y mira su detalle.", color = FamilyMuted)
            Spacer(modifier = Modifier.height(6.dp))
        }
        items(products) { product ->
            ProductRow(product = product, onClick = { onOpenDetail(product.id) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(product: Product, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = FamilyRed,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = FamilyBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Image(
                    painter = painterResource(product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(330.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            item {
                Text(product.tag.uppercase(), color = FamilyYellow, fontWeight = FontWeight.Black)
                Text(product.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                Text(product.price, color = FamilyRed, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(10.dp))
                Text(product.description, color = FamilyMuted)
                Spacer(modifier = Modifier.height(20.dp))
                PrimaryAction(text = "Agregar al carrito") {}
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    userDao: UserDao,
    padding: PaddingValues,
    userId: Int?,
    name: String,
    email: String,
    phone: String,
    address: String,
    onProfileUpdated: (String, String) -> Unit,
    onLogout: () -> Unit
) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var isEditing by remember(phone, address) { mutableStateOf(false) }
    var editablePhone by remember(phone) { mutableStateOf(phone) }
    var editableAddress by remember(address) { mutableStateOf(address) }
    var message by remember { mutableStateOf("") }

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
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(46.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text(email, color = FamilyMuted)
        Spacer(modifier = Modifier.height(28.dp))

        if (isEditing) {
            FamilyTextField(
                value = editablePhone,
                onValueChange = { editablePhone = it },
                label = "Telefono",
                keyboardType = KeyboardType.Phone
            )
            FamilyTextField(
                value = editableAddress,
                onValueChange = { editableAddress = it },
                label = "Direccion"
            )
            if (message.isNotBlank()) {
                Text(
                    text = message,
                    color = if (message.startsWith("Datos")) FamilyRed else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            PrimaryAction(text = "Guardar cambios") {
                val id = userId
                if (id == null) {
                    message = "Inicia sesion nuevamente para editar tu perfil."
                } else if (editablePhone.isBlank()) {
                    message = "El telefono no puede estar vacio."
                } else {
                    scope.launch {
                        val cleanPhone = editablePhone.trim()
                        val cleanAddress = editableAddress.trim()
                        userDao.updateProfile(id, cleanPhone, cleanAddress)
                        onProfileUpdated(cleanPhone, cleanAddress)
                        isEditing = false
                        message = "Datos actualizados."
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    editablePhone = phone
                    editableAddress = address
                    message = ""
                    isEditing = false
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FamilyMuted),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        } else {
            ProfileInfo(label = "Telefono", value = phone)
            ProfileInfo(
                label = "Direccion",
                value = address.ifBlank { "Agrega tu direccion para pedidos delivery" }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        if (!isEditing) {
            Button(
                onClick = {
                    editablePhone = phone
                    editableAddress = address
                    message = ""
                    isEditing = true
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FamilyRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editar datos")
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Button(
            onClick = onLogout,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FamilyDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar sesion")
        }
    }
}

@Composable
private fun HeroCard(onOpenProducts: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = FamilyRed)
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.pizza_familiar),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xDD000000))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(18.dp)
            ) {
                Text("Pizza familiar desde S/ 19.90", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text("Promos calientes para compartir hoy.", color = Color.White.copy(alpha = 0.86f))
                Spacer(modifier = Modifier.height(12.dp))
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
private fun PromoCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(230.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Image(
            painter = painterResource(product.imageRes),
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(12.dp)) {
            Text(product.name, fontWeight = FontWeight.Black)
            Text(product.price, color = FamilyRed, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProductRow(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .size(94.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.tag, color = FamilyYellow, fontWeight = FontWeight.Black)
                Text(product.name, fontWeight = FontWeight.Black)
                Text(product.description, color = FamilyMuted, maxLines = 2)
                Text(product.price, color = FamilyRed, fontWeight = FontWeight.Black)
            }
            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = FamilyRed)
        }
    }
}

@Composable
private fun FamilyBottomBar(navController: NavController, selectedRoute: String) {
    val currentRoute by navController.currentBackStackEntryAsState()
    val route = currentRoute?.destination?.route ?: selectedRoute

    Surface(color = Color.White, tonalElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
        BottomItem(navController, route, Routes.HOME, "Inicio", Icons.Default.Home)
        BottomItem(navController, route, Routes.PRODUCTS, "Productos", Icons.Default.RestaurantMenu)
        BottomItem(navController, route, Routes.PROFILE, "Perfil", Icons.Default.Person)
        }
    }
}

@Composable
private fun BottomItem(
    navController: NavController,
    currentRoute: String,
    route: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val selected = currentRoute == route
    val color = if (selected) FamilyRed else FamilyMuted

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                navController.navigate(route) {
                    launchSingleTop = true
                    popUpTo(Routes.HOME)
                }
            }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = color)
        Text(label, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FamilyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
private fun PrimaryAction(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FamilyRed),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Text(text, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun AuthLink(text: String, onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = text,
        color = FamilyRed,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun ProfileInfo(label: String, value: String) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, color = FamilyMuted, style = MaterialTheme.typography.labelMedium)
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FamilyPizzaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = FamilyRed,
            secondary = FamilyYellow,
            background = FamilyBackground,
            surface = Color.White,
            error = Color(0xFFD32F2F),
            onPrimary = Color.White,
            onSecondary = FamilyDark,
            onBackground = FamilyDark,
            onSurface = FamilyDark
        ),
        content = content
    )
}

private val FamilyRed = Color(0xFFD71920)
private val FamilyYellow = Color(0xFFFFC928)
private val FamilyDark = Color(0xFF1F1F1F)
private val FamilyMuted = Color(0xFF6E6E6E)
private val FamilyBackground = Color(0xFFFFF7EF)
