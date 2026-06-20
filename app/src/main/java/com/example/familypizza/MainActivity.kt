package com.example.familypizza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.familypizza.data.firebase.FirebaseDataSource
import com.example.familypizza.data.local.FamilyPizzaDatabase
import com.example.familypizza.data.remote.NetworkModule
import com.example.familypizza.data.repository.CartRepositoryImpl
import com.example.familypizza.data.repository.OrderRepositoryImpl
import com.example.familypizza.data.repository.ProductRepositoryImpl
import com.example.familypizza.data.repository.UserRepositoryImpl
import com.example.familypizza.presentation.navigation.FamilyPizzaApp
import com.example.familypizza.presentation.theme.FamilyPizzaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val api        = NetworkModule.createApi()
        val firebase   = FirebaseDataSource()
        val db         = FamilyPizzaDatabase.getInstance(this)
        val userRepo   = UserRepositoryImpl(db.userDao(), api, firebase)
        val cartRepo   = CartRepositoryImpl(db.cartDao())
        val orderRepo  = OrderRepositoryImpl(db.orderDao(), db.orderItemDao(), api, firebase)
        val productRepo = ProductRepositoryImpl(db.productDao(), api, firebase)
        setContent {
            FamilyPizzaTheme {
                FamilyPizzaApp(userRepo, cartRepo, orderRepo, productRepo)
            }
        }
    }
}
