package com.example.familypizza.data.firebase

import com.example.familypizza.R
import com.example.familypizza.domain.model.CartItem
import com.example.familypizza.domain.model.Order
import com.example.familypizza.domain.model.OrderStatus
import com.example.familypizza.domain.model.Product
import com.example.familypizza.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirebaseDataSource(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun registerEmail(user: User): User {
        val result = auth.createUserWithEmailAndPassword(user.email, user.password).await()
        val savedUser = user.copy(id = firebaseIdToLocalId(result.user?.uid.orEmpty()))
        runCatching { saveUser(savedUser, result.user?.uid) }
        return savedUser
    }

    suspend fun loginEmail(email: String, password: String): User? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: return null
        return runCatching { getUser(uid) }.getOrNull() ?: User(
            id = firebaseIdToLocalId(uid),
            name = result.user?.displayName ?: email.substringBefore("@"),
            email = email,
            phone = "",
            password = password,
            address = ""
        ).also { user -> runCatching { saveUser(user, uid) } }
    }

    suspend fun loginGoogle(idToken: String): User? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val firebaseUser = result.user ?: return null
        return runCatching { getUser(firebaseUser.uid) }.getOrNull() ?: User(
            id = firebaseIdToLocalId(firebaseUser.uid),
            name = firebaseUser.displayName ?: "Cliente Google",
            email = firebaseUser.email.orEmpty(),
            phone = firebaseUser.phoneNumber.orEmpty(),
            password = "",
            address = ""
        ).also { user -> runCatching { saveUser(user, firebaseUser.uid) } }
    }

    suspend fun saveUser(user: User, uid: String? = auth.currentUser?.uid) {
        val documentId = uid ?: user.email.ifBlank { user.id.toString() }
        firestore.collection("users").document(documentId).set(user.toMap()).await()
    }

    suspend fun getProducts(): List<Product> {
        val snapshot = firestore.collection("products").orderBy("id").get().await()
        return snapshot.documents.mapNotNull { document ->
            val id = document.getLong("id")?.toInt() ?: return@mapNotNull null
            val price = document.getDouble("price") ?: 0.0
            Product(
                id = id,
                name = document.getString("name").orEmpty(),
                price = "S/ ${"%.2f".format(price)}",
                priceValue = price,
                description = document.getString("description").orEmpty(),
                tag = document.getString("tag").orEmpty(),
                imageRes = imageForKey(document.getString("imageKey").orEmpty())
            )
        }
    }

    suspend fun saveProduct(product: Product) {
        firestore.collection("products")
            .document(product.id.toString())
            .set(product.toFirestoreMap())
            .await()
    }

    suspend fun deleteProduct(id: Int) {
        firestore.collection("products").document(id.toString()).delete().await()
    }

    suspend fun saveOrder(order: Order) {
        firestore.collection("orders")
            .document(order.id.toString())
            .set(order.toFirestoreMap())
            .await()
    }

    suspend fun updateOrderStatus(orderId: Int, status: OrderStatus) {
        firestore.collection("orders").document(orderId.toString())
            .update("status", status.name)
            .await()
    }

    suspend fun getAllOrders(): List<Order> {
        val snapshot = firestore.collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.documents.mapNotNull { document ->
            val id = document.getLong("id")?.toInt() ?: return@mapNotNull null
            val userId = document.getLong("userId")?.toInt() ?: 0
            val total = document.getDouble("total") ?: 0.0
            val status = runCatching {
                OrderStatus.valueOf(document.getString("status").orEmpty())
            }.getOrDefault(OrderStatus.PENDIENTE)
            val createdAt = document.getLong("createdAt") ?: System.currentTimeMillis()
            val items = (document.get("items") as? List<*>)?.mapNotNull { raw ->
                val map = raw as? Map<*, *> ?: return@mapNotNull null
                CartItem(
                    productId = (map["productId"] as? Number)?.toInt() ?: 0,
                    name = map["name"] as? String ?: "",
                    price = (map["price"] as? Number)?.toDouble() ?: 0.0,
                    imageRes = imageForKey(map["imageKey"] as? String ?: ""),
                    quantity = (map["quantity"] as? Number)?.toInt() ?: 0
                )
            }.orEmpty()
            Order(id, userId, items, total, status, createdAt)
        }
    }

    private suspend fun getUser(uid: String): User? {
        val document = firestore.collection("users").document(uid).get().await()
        if (!document.exists()) return null
        return User(
            id = document.getLong("id")?.toInt() ?: firebaseIdToLocalId(uid),
            name = document.getString("name").orEmpty(),
            email = document.getString("email").orEmpty(),
            phone = document.getString("phone").orEmpty(),
            password = document.getString("password").orEmpty(),
            address = document.getString("address").orEmpty()
        )
    }

    private fun User.toMap() = mapOf(
        "id" to id,
        "name" to name,
        "email" to email,
        "phone" to phone,
        "password" to password,
        "address" to address
    )

    private fun Product.toFirestoreMap() = mapOf(
        "id" to id,
        "name" to name,
        "price" to priceValue,
        "description" to description,
        "tag" to tag,
        "imageKey" to imageKeyForRes(imageRes)
    )

    private fun Order.toFirestoreMap() = mapOf(
        "id" to id,
        "userId" to userId,
        "total" to total,
        "status" to status.name,
        "createdAt" to createdAt,
        "items" to items.map { item ->
            mapOf(
                "productId" to item.productId,
                "name" to item.name,
                "price" to item.price,
                "quantity" to item.quantity,
                "imageKey" to imageKeyForRes(item.imageRes)
            )
        }
    )

    private fun firebaseIdToLocalId(uid: String): Int =
        uid.fold(0) { acc, char -> acc + char.code }.coerceAtLeast(1)

    private fun imageForKey(key: String): Int = when (key) {
        "pizza_familiar" -> R.drawable.pizza_familiar
        "promo_2x1" -> R.drawable.promo_2x1_pizza_familiar
        "combo_alitas" -> R.drawable.combo_pizzas_alitas
        "pizza_burger" -> R.drawable.pizza_burger
        "lasagna" -> R.drawable.pizza_familiar_mas_lasana
        else -> R.drawable.pizza_familiar
    }

    private fun imageKeyForRes(resId: Int): String = when (resId) {
        R.drawable.promo_2x1_pizza_familiar -> "promo_2x1"
        R.drawable.combo_pizzas_alitas -> "combo_alitas"
        R.drawable.pizza_burger -> "pizza_burger"
        R.drawable.pizza_familiar_mas_lasana -> "lasagna"
        else -> "pizza_familiar"
    }
}
