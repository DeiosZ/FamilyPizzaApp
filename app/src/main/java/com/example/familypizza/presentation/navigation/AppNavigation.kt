package com.example.familypizza.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.familypizza.domain.model.CartItem
import com.example.familypizza.domain.repository.CartRepository
import com.example.familypizza.domain.repository.OrderRepository
import com.example.familypizza.domain.repository.ProductRepository
import com.example.familypizza.domain.repository.UserRepository
import com.example.familypizza.presentation.AppViewModelFactory
import com.example.familypizza.presentation.admin.AdminDashboardScreen
import com.example.familypizza.presentation.admin.AdminOrdersScreen
import com.example.familypizza.presentation.admin.AdminOrdersViewModel
import com.example.familypizza.presentation.admin.AdminProductsScreen
import com.example.familypizza.presentation.admin.AdminProductsViewModel
import com.example.familypizza.presentation.cart.CartScreen
import com.example.familypizza.presentation.cart.CartViewModel
import com.example.familypizza.presentation.detail.DetailScreen
import com.example.familypizza.presentation.home.HomeScreen
import com.example.familypizza.presentation.login.LoginScreen
import com.example.familypizza.presentation.orders.OrdersScreen
import com.example.familypizza.presentation.orders.OrdersViewModel
import com.example.familypizza.presentation.products.ProductsScreen
import com.example.familypizza.presentation.products.ProductsViewModel
import com.example.familypizza.presentation.profile.ProfileScreen
import com.example.familypizza.presentation.register.RegisterScreen
import com.example.familypizza.presentation.theme.FamilyBackground
import com.example.familypizza.presentation.theme.FamilyDark
import com.example.familypizza.presentation.theme.FamilyMuted
import com.example.familypizza.presentation.theme.FamilyRed
import com.example.familypizza.presentation.theme.FamilyYellow

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PRODUCTS = "products"
    const val DETAIL = "detail/{productId}"
    const val CART = "cart"
    const val ORDERS = "orders"
    const val PROFILE = "profile"
    const val ADMIN_HOME = "admin_home"
    const val ADMIN_PRODUCTS = "admin_products"
    const val ADMIN_ORDERS = "admin_orders"

    fun detail(productId: Int) = "detail/$productId"
}

@Composable
fun FamilyPizzaApp(
    userRepo: UserRepository,
    cartRepo: CartRepository,
    orderRepo: OrderRepository,
    productRepo: ProductRepository
) {
    val navController = rememberNavController()
    var profileName by remember { mutableStateOf("Cliente FamilyPizza") }
    var profileEmail by remember { mutableStateOf("cliente@familypizza.pe") }
    var profilePhone by remember { mutableStateOf("986 732 411") }
    var profileAddress by remember { mutableStateOf("") }
    var currentUserId by remember { mutableStateOf<Int?>(null) }

    val factory = remember(cartRepo, orderRepo, productRepo) {
        AppViewModelFactory(cartRepo, orderRepo, productRepo)
    }
    val cartViewModel: CartViewModel = viewModel(factory = factory)
    val cartUiState by cartViewModel.ui.collectAsState()
    val productsViewModel: ProductsViewModel = viewModel(factory = factory)
    val productsUiState by productsViewModel.ui.collectAsState()

    LaunchedEffect(cartUiState.orderPlaced) {
        if (cartUiState.orderPlaced) {
            navController.navigate(Routes.ORDERS) { launchSingleTop = true }
            cartViewModel.resetOrderPlaced()
        }
    }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                repository = userRepo,
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
                onAdminLogin = {
                    currentUserId = null
                    navController.navigate(Routes.ADMIN_HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                repository = userRepo,
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
            MainScaffold(navController, Routes.HOME, cartUiState.count) { padding ->
                HomeScreen(
                    padding = padding,
                    products = productsUiState.products,
                    isLoading = productsUiState.isLoading,
                    error = productsUiState.error,
                    onOpenProducts = { navController.navigate(Routes.PRODUCTS) },
                    onOpenDetail = { navController.navigate(Routes.detail(it)) }
                )
            }
        }

        composable(Routes.PRODUCTS) {
            MainScaffold(navController, Routes.PRODUCTS, cartUiState.count) { padding ->
                ProductsScreen(
                    padding = padding,
                    products = productsUiState.products,
                    isLoading = productsUiState.isLoading,
                    error = productsUiState.error,
                    onRetry = productsViewModel::refresh,
                    onOpenDetail = { navController.navigate(Routes.detail(it)) }
                )
            }
        }

        composable(Routes.DETAIL, arguments = listOf(navArgument("productId") { type = NavType.IntType })) { entry ->
            val productId = entry.arguments?.getInt("productId") ?: 1
            val product = productsUiState.products.firstOrNull { it.id == productId }
            DetailScreen(
                product = product,
                onBack = { navController.popBackStack() },
                onAddToCart = { p ->
                    cartViewModel.addItem(CartItem(p.id, p.name, p.priceValue, p.imageRes, 1))
                }
            )
        }

        composable(Routes.CART) {
            MainScaffold(navController, Routes.CART, cartUiState.count) { padding ->
                CartScreen(
                    padding = padding,
                    uiState = cartUiState,
                    onIncrease = cartViewModel::increase,
                    onDecrease = cartViewModel::decrease,
                    onRemove = cartViewModel::remove,
                    onCheckout = { cartViewModel.placeOrder(currentUserId ?: 0) }
                )
            }
        }

        composable(Routes.ORDERS) {
            val ordersFactory = remember(orderRepo, productRepo, currentUserId) {
                AppViewModelFactory(cartRepo, orderRepo, productRepo, currentUserId ?: 0)
            }
            val ordersViewModel: OrdersViewModel = viewModel(key = "orders_${currentUserId}", factory = ordersFactory)
            val orders by ordersViewModel.orders.collectAsState()

            MainScaffold(navController, Routes.ORDERS, cartUiState.count) { padding ->
                OrdersScreen(
                    padding = padding,
                    orders = orders,
                    onCancel = ordersViewModel::cancel
                )
            }
        }

        composable(Routes.PROFILE) {
            MainScaffold(navController, Routes.PROFILE, cartUiState.count) { padding ->
                ProfileScreen(
                    repository = userRepo,
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
                        navController.navigate(Routes.LOGIN) { popUpTo(0) }
                    }
                )
            }
        }

        composable(Routes.ADMIN_HOME) {
            AdminScaffold(title = "FamilyPizza Admin") { padding ->
                AdminDashboardScreen(
                    padding = padding,
                    onProducts = { navController.navigate(Routes.ADMIN_PRODUCTS) },
                    onOrders = { navController.navigate(Routes.ADMIN_ORDERS) },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0)
                        }
                    }
                )
            }
        }

        composable(Routes.ADMIN_PRODUCTS) {
            val adminProductsViewModel: AdminProductsViewModel = viewModel(factory = factory)
            val uiState by adminProductsViewModel.ui.collectAsState()
            AdminScaffold(title = "Productos Admin") { padding ->
                AdminProductsScreen(
                    padding = padding,
                    uiState = uiState,
                    onSave = adminProductsViewModel::saveProduct,
                    onDelete = adminProductsViewModel::deleteProduct
                )
            }
        }

        composable(Routes.ADMIN_ORDERS) {
            val adminOrdersViewModel: AdminOrdersViewModel = viewModel(factory = factory)
            val orders by adminOrdersViewModel.orders.collectAsState()
            AdminScaffold(title = "Pedidos Admin") { padding ->
                AdminOrdersScreen(
                    padding = padding,
                    orders = orders,
                    onStatusChange = adminOrdersViewModel::updateStatus
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScaffold(
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = FamilyRed,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = FamilyBackground,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavController,
    selectedRoute: String,
    cartCount: Int,
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
        bottomBar = { FamilyBottomBar(navController, selectedRoute, cartCount) },
        containerColor = FamilyBackground,
        content = content
    )
}

@Composable
fun FamilyBottomBar(navController: NavController, selectedRoute: String, cartCount: Int) {
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
            BottomItemBadge(navController, route, Routes.CART, "Carrito", Icons.Default.ShoppingCart, cartCount)
            BottomItem(navController, route, Routes.ORDERS, "Pedidos", Icons.Default.ReceiptLong)
            BottomItem(navController, route, Routes.PROFILE, "Perfil", Icons.Default.Person)
        }
    }
}

@Composable
fun BottomItem(
    navController: NavController,
    currentRoute: String,
    route: String,
    label: String,
    icon: ImageVector
) {
    val color = if (currentRoute == route) FamilyRed else FamilyMuted
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                navController.navigate(route) {
                    launchSingleTop = true
                    popUpTo(Routes.HOME)
                }
            }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, label, tint = color)
        Text(label, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BottomItemBadge(
    navController: NavController,
    currentRoute: String,
    route: String,
    label: String,
    icon: ImageVector,
    count: Int
) {
    val color = if (currentRoute == route) FamilyRed else FamilyMuted
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                navController.navigate(route) {
                    launchSingleTop = true
                    popUpTo(Routes.HOME)
                }
            }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgedBox(
            badge = {
                if (count > 0) {
                    Badge(containerColor = FamilyYellow, contentColor = FamilyDark) {
                        Text("$count", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                    }
                }
            }
        ) {
            Icon(icon, label, tint = color)
        }
        Text(label, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}
