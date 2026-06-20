package com.example.familypizza.data.repository

import com.example.familypizza.data.local.OrderDao
import com.example.familypizza.data.local.OrderEntity
import com.example.familypizza.data.firebase.FirebaseDataSource
import com.example.familypizza.data.local.OrderItemDao
import com.example.familypizza.data.local.OrderItemEntity
import com.example.familypizza.data.remote.FamilyPizzaApi
import com.example.familypizza.data.remote.dto.OrderItemDto
import com.example.familypizza.data.remote.dto.OrderRequestDto
import com.example.familypizza.data.remote.safeApiCall
import com.example.familypizza.domain.model.CartItem
import com.example.familypizza.domain.model.Order
import com.example.familypizza.domain.model.OrderStatus
import com.example.familypizza.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class OrderRepositoryImpl(
    private val dao: OrderDao,
    private val itemDao: OrderItemDao,
    private val api: FamilyPizzaApi,
    private val firebase: FirebaseDataSource
) : OrderRepository {

    override fun observeAllOrders(): Flow<List<Order>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeOrders(userId: Int): Flow<List<Order>> =
        dao.observeByUser(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun refreshAllOrders(): Result<Unit> =
        runCatching {
            firebase.getAllOrders().forEach { order ->
                dao.upsert(order.toEntity())
                itemDao.deleteByOrder(order.id)
                itemDao.insertAll(order.items.map { it.toOrderItemEntity(order.id) })
            }
        }

    override suspend fun placeOrder(order: Order): Order {
        val entity = order.toEntity()
        val newId  = dao.insert(entity).toInt()
        val savedOrder = order.copy(id = newId)
        itemDao.insertAll(savedOrder.items.map { it.toOrderItemEntity(newId) })
        runCatching { firebase.saveOrder(savedOrder) }
        safeApiCall { api.createOrder(savedOrder.toRequestDto()) }
        return savedOrder
    }

    override suspend fun updateStatus(orderId: Int, status: OrderStatus) {
        dao.updateStatus(orderId, status.name)
        runCatching { firebase.updateOrderStatus(orderId, status) }
    }

    override suspend fun deleteOrder(orderId: Int) {
        itemDao.deleteByOrder(orderId)
        dao.deleteById(orderId)
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

    private fun CartItem.toOrderItemEntity(orderId: Int) = OrderItemEntity(
        orderId = orderId,
        productId = productId,
        name = name,
        price = price,
        imageRes = imageRes,
        quantity = quantity
    )

    private fun Order.toRequestDto() = OrderRequestDto(
        localId = id,
        userId = userId,
        items = items.map { item ->
            OrderItemDto(
                productId = item.productId,
                name = item.name,
                price = item.price,
                quantity = item.quantity
            )
        },
        total = total,
        status = status.name,
        createdAt = createdAt
    )
}
