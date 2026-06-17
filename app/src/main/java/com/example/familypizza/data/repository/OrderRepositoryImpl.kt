package com.example.familypizza.data.repository

import com.example.familypizza.data.local.OrderDao
import com.example.familypizza.data.local.OrderEntity
import com.example.familypizza.domain.model.CartItem
import com.example.familypizza.domain.model.Order
import com.example.familypizza.domain.model.OrderStatus
import com.example.familypizza.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class OrderRepositoryImpl(private val dao: OrderDao) : OrderRepository {

    override fun observeOrders(userId: Int): Flow<List<Order>> =
        dao.observeByUser(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun placeOrder(order: Order): Order {
        val entity = order.toEntity()
        val newId  = dao.insert(entity).toInt()
        return order.copy(id = newId)
    }

    override suspend fun updateStatus(orderId: Int, status: OrderStatus) {
        dao.updateStatus(orderId, status.name)
    }

    // ── Serialización JSON manual (sin dependencias extra) ──────────────────

    private fun OrderEntity.toDomain(): Order {
        val statusEnum = runCatching { OrderStatus.valueOf(status) }.getOrDefault(OrderStatus.PENDIENTE)
        return Order(
            id        = id,
            userId    = userId,
            items     = deserializeItems(items),
            total     = total,
            status    = statusEnum,
            createdAt = createdAt
        )
    }

    private fun Order.toEntity() = OrderEntity(
        id        = id,
        userId    = userId,
        items     = serializeItems(items),
        total     = total,
        status    = status.name,
        createdAt = createdAt
    )

    private fun serializeItems(items: List<CartItem>): String {
        val array = JSONArray()
        items.forEach { item ->
            array.put(JSONObject().apply {
                put("productId", item.productId)
                put("name",      item.name)
                put("price",     item.price)
                put("imageRes",  item.imageRes)
                put("quantity",  item.quantity)
            })
        }
        return array.toString()
    }

    private fun deserializeItems(json: String): List<CartItem> {
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            CartItem(
                productId = obj.getInt("productId"),
                name      = obj.getString("name"),
                price     = obj.getDouble("price"),
                imageRes  = obj.getInt("imageRes"),
                quantity  = obj.getInt("quantity")
            )
        }
    }
}