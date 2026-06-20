package com.example.familypizza.data.repository

import com.example.familypizza.R
import com.example.familypizza.data.firebase.FirebaseDataSource
import com.example.familypizza.data.local.ProductDao
import com.example.familypizza.data.local.ProductEntity
import com.example.familypizza.data.remote.ApiResult
import com.example.familypizza.data.remote.FamilyPizzaApi
import com.example.familypizza.data.remote.dto.ProductDto
import com.example.familypizza.data.remote.safeApiCall
import com.example.familypizza.domain.model.Product
import com.example.familypizza.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val dao: ProductDao,
    private val api: FamilyPizzaApi,
    private val firebase: FirebaseDataSource
) : ProductRepository {

    override fun observeProducts(): Flow<List<Product>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun findById(id: Int): Product? =
        dao.findById(id)?.toDomain()

    override suspend fun refreshProducts(): Result<Unit> {
        if (dao.count() == 0) {
            dao.upsertAll(defaultProducts)
        }

        runCatching { firebase.getProducts() }.onSuccess { products ->
            if (products.isNotEmpty()) {
                dao.upsertAll(products.map { it.toEntity() })
                Result.success(Unit)
            } else {
                dao.getAllOnce().ifEmpty { defaultProducts }.forEach { product ->
                    runCatching { firebase.saveProduct(product.toDomain()) }
                }
            }
        }

        return when (val result = safeApiCall { api.getProducts() }) {
            is ApiResult.Success -> {
                if (result.data.isNotEmpty()) {
                    dao.upsertAll(result.data.map { it.toEntity() })
                }
                Result.success(Unit)
            }
            is ApiResult.Error -> Result.success(Unit)
        }
    }

    override suspend fun upsertProduct(product: Product) {
        // Guarda primero en local (Room) para no perder el dato sin conexion...
        dao.upsertAll(listOf(product.toEntity()))
        // ...pero la escritura en Firebase NO se silencia: si falla, propaga el
        // error para que la UI muestre el motivo real (reglas, sesion, red, etc.).
        firebase.saveProduct(product)
    }

    override suspend fun deleteProduct(id: Int) {
        dao.deleteById(id)
        firebase.deleteProduct(id)
    }

    private fun ProductEntity.toDomain() = Product(
        id = id,
        name = name,
        price = priceLabel,
        priceValue = priceValue,
        description = description,
        tag = tag,
        imageRes = imageRes
    )

    private fun Product.toEntity() = ProductEntity(
        id = id,
        name = name,
        priceLabel = price,
        priceValue = priceValue,
        description = description,
        tag = tag,
        imageRes = imageRes
    )

    private fun ProductDto.toEntity() = ProductEntity(
        id = id,
        name = name,
        priceLabel = "S/ ${"%.2f".format(price)}",
        priceValue = price,
        description = description,
        tag = tag,
        imageRes = imageForKey(imageKey)
    )

    private fun imageForKey(key: String): Int = when (key) {
        "pizza_familiar" -> R.drawable.pizza_familiar
        "promo_2x1" -> R.drawable.promo_2x1_pizza_familiar
        "combo_alitas" -> R.drawable.combo_pizzas_alitas
        "pizza_burger" -> R.drawable.pizza_burger
        "lasagna" -> R.drawable.pizza_familiar_mas_lasana
        else -> R.drawable.pizza_familiar
    }

    private val defaultProducts = listOf(
        ProductEntity(1, "Pizza Familiar", "S/ 19.90", 19.90, "Americana, pepperoni o mozzarella. Pizza familiar de 33 cm de diametro.", "Oferta", R.drawable.pizza_familiar),
        ProductEntity(2, "2x1 Familiar", "S/ 39.90", 39.90, "Dos pizzas familiares: americana, pepperoni, mozzarella y cordon blue.", "Promo", R.drawable.promo_2x1_pizza_familiar),
        ProductEntity(3, "Combo Extra Cheese", "S/ 50.00", 50.00, "Dos pizzas medianas, seis alitas BBQ, buffalo o acevichada y Pepsi de 1 litro.", "Combo", R.drawable.combo_pizzas_alitas),
        ProductEntity(4, "Pizza Burger", "Desde S/ 10.90", 10.90, "Queso mozzarella, carne, tocino crujiente y salsa BBQ.", "Nuevo", R.drawable.pizza_burger),
        ProductEntity(5, "Familiar + Lasagna", "S/ 34.90", 34.90, "Pizza familiar con lasagna personal. No valido para suprema.", "Especial", R.drawable.pizza_familiar_mas_lasana)
    )
}
