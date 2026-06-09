package com.example.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LostFoundApi {
    @GET("items")
    suspend fun getItems(
        @Query("category") category: String? = null,
        @Query("status") status: String? = null,
        @Query("location") location: String? = null,
        @Query("q") query: String? = null
    ): Response<List<ItemDto>>

    @GET("items/{id}")
    suspend fun getItemById(@Path("id") id: String): Response<ItemDto>

    @POST("items")
    suspend fun addItem(@Body item: ItemDto): Response<ItemDto>

    @PUT("items/{id}")
    suspend fun updateItem(@Path("id") id: String, @Body item: ItemDto): Response<Unit>

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: String): Response<Unit>
}