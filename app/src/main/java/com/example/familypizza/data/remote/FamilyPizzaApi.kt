package com.example.familypizza.data.remote

import com.example.familypizza.data.remote.dto.OrderRequestDto
import com.example.familypizza.data.remote.dto.OrderResponseDto
import com.example.familypizza.data.remote.dto.ProductDto
import com.example.familypizza.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FamilyPizzaApi {
    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @POST("users")
    suspend fun createUser(@Body user: UserDto): UserDto

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UserDto): UserDto

    @POST("orders")
    suspend fun createOrder(@Body order: OrderRequestDto): OrderResponseDto
}
