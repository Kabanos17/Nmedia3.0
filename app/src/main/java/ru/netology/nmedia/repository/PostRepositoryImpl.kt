package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        })
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}.type

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override suspend fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("$BASE_URL/api/posts")
            .build()

        val response = client.newCall(request).await()
        val body = response.body?.string() ?: throw RuntimeException("body is null")
        return gson.fromJson(body, typeToken)
    }

    override suspend fun likeById(id: Long): Post {
        val request: Request = Request.Builder()
            .post("".toRequestBody(jsonType))
            .url("$BASE_URL/api/posts/$id/likes")
            .build()

        val response = client.newCall(request).await()
        val body = response.body?.string() ?: throw RuntimeException("body is null")
        return gson.fromJson(body, Post::class.java)
    }

    override suspend fun unlikeById(id: Long): Post {
        val request: Request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/posts/$id/likes")
            .build()

        val response = client.newCall(request).await()
        val body = response.body?.string() ?: throw RuntimeException("body is null")
        return gson.fromJson(body, Post::class.java)
    }

    override suspend fun shareById(id: Long) {
    }

    override suspend fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("$BASE_URL/api/posts")
            .build()

        client.newCall(request).await()
    }

    override suspend fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/posts/$id")
            .build()

        client.newCall(request).await()
    }

    override suspend fun seed() {
    }
}

suspend fun Call.await(): Response {
    return suspendCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }
        })
    }
}