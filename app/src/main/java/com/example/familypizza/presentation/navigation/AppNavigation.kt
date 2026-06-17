package com.example.familypizza.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.familypizza.R
import com.example.familypizza.domain.model.CartItem
import com.example.familypizza.domain.repository.CartRepository
import com.example.familypizza.domain.repository.OrderRepository
import com.example.familypizza.domain.repository.UserRepository
import com.example.familypizza.presentation.AppViewModelFactory
import com.example.familypizza.presentation.cart.CartScreen
import com.example.familypizza.presentation.cart.CartViewModel
import com.example.familypizza.presentation.detail.DetailScreen
import com.example.familypizza.presentation.home.HomeScreen
import com.example.familypizza.presentation.login.LoginScreen
import com.example.familypizza.presentation.orders.OrdersScreen
import com.example.familypizza.presentation.orders.OrdersViewModel
import com.example.familypizza.presentation.products.ProductsScreen
import com.example.familypizza.presentation.profile.ProfileScreen
import com.example.familypizza.presentation.register.RegisterScreen
import com.example.familypizza.presentation.theme.*

object Routes {
    const val LOGIN    = "login"
    const val REGISTER = "register"
    const val HOME     = "home"
    const val PRODUCTS = "products"
    const val DETAIL   = "detail/{productId}"
    const val CART     = "cart"
    const val ORDERS   = "orders"
    const val PROFILE  = "profile"
    fun detail(productId: Int) = "detail/$productId"
}

data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val priceValue: Double,     // precio numérico para el carrito
    val description: String,
    val tag: String,
    @DrawableRes val imageRes: Int
)

val products = listOf(
    Product(1, "Pizza Familiar",     "S/ 19.90",      19.90, "Americana, pepperoni o mozzarella. Pizza familiar de 33 cm de diametro.",                         "Oferta",   R.drawable.pizza_familiar),
    Product(2, "2x1 Familiar",       "S/ 39.90",      39.90, "Dos pizzas familiares: americana, pepperoni, mozzarella y cordon blue.",                           "Promo",    R.drawable.promo_2x1_pizza_familiar),
    Product(3, "Combo Extra Cheese", "S/ 50.00",      50.00, "Dos pizzas medianas, seis alitas BBQ, buffalo o acevichada y Pepsi de 1 litro.",                  "Combo",    R.drawable.combo_pizzas_alitas),
    Product(4, "Pizza Burger",       "Desde S/ 10.90",10.90, "Queso mozzarella, carne, tocino crujiente y salsa BBQ.",                                           "Nuevo",    R.drawable.pizza_burger),
    Product(5, "Familiar + Lasagna", "S/ 34.90",      34.90, "Pizza familiar con lasagna personal. No valido para suprema.",                                    "Especial", R.drawable.pizza_familiar_mas_lasana)
)

@Composable
fun FamilyPizzaApp(
    userRepo : UserRepository,
    cartRepo : CartRepository,
    orderRepo: OrderRepository
) {
    val navController = rememberNavController()
    var profileName    by remember { mutableStateOf("Cliente FamilyPizza") }
    var profileEmail   by remember { mutableStateOf("cliente@familypizza.pe") }
    var profilePhone   by remember { mutableStateOf("986 732 411") }
    var profileAddress by remember { mutableStateOf("") }
    var currentUserId  by remember { mutableStateOf<Int?>(null) }

    // CartViewModel único, compartido en toda la app
    val factory      = remember(cartRepo, orderRepo) { AppViewModelFactory(cartRepo, orderRepo) }
    val cartViewModel: CartViewModel = viewModel(factory = factory)
    val cartUiState  by cartViewModel.ui.collectAsState()

    // Navegar a Órdenes cuando se confirme el pedido
    LaunchedEffect(cartUiState.orderPlaced) {
        if (cartUiState.orderPlaced) {
            navController.navigate(Routes.ORDERS) { launchSingleTop = true }
            cartViewModel.resetOrderPlaced()
        }
    }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                repository     = userRepo,
                onLoginSuccess = { user ->
                    currentUserId  = user.id
                    profileName    = user.name
                    profileEmail   = user.email
                    profilePhone   = user.phone
                    profileAddress = user.address
                    navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                repository      = userRepo,
                onCreateAccount = { user ->
                    currentUserId  = user.id
                    profileName    = user.name
                    profileEmail   = user.email
                    profilePhone   = user.phone
                    profileAddress = user.address
                    navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            MainScaffold(navController, Routes.HOME, cartUiState.count) { padding ->
                HomeScreen(padding,
                    onOpenProducts = { navController.navigate(Routes.PRODUCTS) },
                    onOpenDetail   = { navController.navigate(Routes.detail(it)) }
                )
            }
        }

        composable(Routes.PRODUCTS) {
            MainScaffold(navController, Routes.PRODUCTS, cartUiState.count) { padding ->
                ProductsScreen(padding, onOpenDetail = { navController.navigate(Routes.detail(it)) })
            }
        }

        composable(Routes.DETAIL, arguments = listOf(navArgument("productId") { type = NavType.IntType })) { entry ->
            val product = products.firstOrNull { it.id == (entry.arguments?.getInt("productId") ?: 1) } ?: products.first()
            DetailScreen(
                product  = product,
                onBack   = { navController.popBackStack() },
                onAddToCart = { p ->
                    cartViewModel.addItem(CartItem(p.id, p.name, p.priceValue, p.imageRes, 1))
                }
            )
        }

        composable(Routes.CART) {
            MainScaffold(navController, Routes.CART, cartUiState.count) { padding ->
                CartScreen(
                    padding    = padding,
                    uiState    = cartUiState,
                    onIncrease = cartViewModel::increase,
                    onDecrease = cartViewModel::decrease,
                    onRemove   = cartViewModel::remove,
                    onCheckout = { cartViewModel.placeOrder(currentUserId ?: 0) }
                )
            }
        }

        composable(Routes.ORDERS) {
            val ordersFactory  = remember(orderRepo, currentUserId) { AppViewModelFactory(cartRepo, orderRepo, currentUserId ?: 0) }
            val ordersViewModel: OrdersViewModel = viewModel(key = "orders_${currentUserId}", factory = ordersFactory)
            val orders by ordersViewModel.orders.collectAsState()

            MainScaffold(navController, Routes.ORDERS, cartUiState.count) { padding ->
                OrdersScreen(
                    padding  = padding,
                    orders   = orders,
                    onCancel = ordersViewModel::cancel
                )
            }
        }

        composable(Routes.PROFILE) {
            MainScaffold(navController, Routes.PROFILE, cartUiState.count) { padding ->
                ProfileScreen(
                    repository       = userRepo,
                    padding          = padding,
                    userId           = currentUserId,
                    name             = profileName,
                    email            = profileEmail,
                    phone            = profilePhone,
                    address          = profileAddress,
                    onProfileUpdated = { phone, address -> profilePhone = phone; profileAddress = address },
                    onLogout         = { currentUserId = null; navController.navigate(Routes.LOGIN) { popUpTo(0) } }
                )
            }
        }
    }
}

// ── Scaffold y BottomBar ────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController : NavController,
    selectedRoute : String,
    cartCount     : Int,
    content       : @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title  = { Text("FamilyPizza", fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = FamilyRed, titleContentColor = Color.White)
            )
        },
        bottomBar   = { FamilyBottomBar(navController, selectedRoute, cartCount) },
        containerColor = FamilyBackground,
        content        = content
    )
}

@Composable
fun FamilyBottomBar(navController: NavController, selectedRoute: String, cartCount: Int) {
    val currentRoute by navController.currentBackStackEntryAsState()
    val route = currentRoute?.destination?.route ?: selectedRoute

    Surface(color = Color.White, tonalElevation = 4.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomItem(navController, route, Routes.HOME,     "Inicio",    Icons.Default.Home)
            BottomItem(navController, route, Routes.PRODUCTS, "Productos", Icons.Default.RestaurantMenu)
            BottomItemBadge(navController, route, Routes.CART, "Carrito", Icons.Default.ShoppingCart, cartCount)
            BottomItem(navController, route, Routes.ORDERS,  "Pedidos",   Icons.Default.ReceiptLong)
            BottomItem(navController, route, Routes.PROFILE, "Perfil",    Icons.Default.Person)
        }
    }
}

@Composable
fun BottomItem(navController: NavController, currentRoute: String, route: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    val color = if (currentRoute == route) FamilyRed else FamilyMuted
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { navController.navigate(route) { launchSingleTop = true; popUpTo(Routes.HOME) } }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, label, tint = color)
        Text(label, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BottomItemBadge(navController: NavController, currentRoute: String, route: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, count: Int) {
    val color = if (currentRoute == route) FamilyRed else FamilyMuted
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { navController.navigate(route) { launchSingleTop = true; popUpTo(Routes.HOME) } }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgedBox(badge = {
            if (count > 0) Badge(containerColor = FamilyYellow, contentColor = FamilyDark) { Text("$count", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black) }
        }) {
            Icon(icon, label, tint = color)
        }
        Text(label, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}