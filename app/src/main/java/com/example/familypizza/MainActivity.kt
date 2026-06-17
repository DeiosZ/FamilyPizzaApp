package com.example.familypizza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.familypizza.data.local.FamilyPizzaDatabase
import com.example.familypizza.data.repository.CartRepositoryImpl
import com.example.familypizza.data.repository.OrderRepositoryImpl
import com.example.familypizza.data.repository.UserRepositoryImpl
import com.example.familypizza.presentation.navigation.FamilyPizzaApp
import com.example.familypizza.presentation.theme.FamilyPizzaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db         = FamilyPizzaDatabase.getInstance(this)
        val userRepo   = UserRepositoryImpl(db.userDao())
        val cartRepo   = CartRepositoryImpl(db.cartDao())
        val orderRepo  = OrderRepositoryImpl(db.orderDao())
        setContent {
            FamilyPizzaTheme {
                FamilyPizzaApp(userRepo, cartRepo, orderRepo)
            }
        }
    }
}