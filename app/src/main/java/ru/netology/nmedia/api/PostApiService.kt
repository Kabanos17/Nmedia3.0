package ru.netology.nmedia.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.Post

interface PostApiService {
    @GET("posts")
    fun getAll(): Call<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun unlikeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>
}
