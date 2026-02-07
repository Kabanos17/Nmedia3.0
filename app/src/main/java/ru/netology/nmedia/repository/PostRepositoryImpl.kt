package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.api.ApiModule
import ru.netology.nmedia.dto.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostRepositoryImpl : PostRepository {

    override fun getAll(): LiveData<List<Post>> {
        val data = MutableLiveData<List<Post>>()
        ApiModule.service.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    data.value = response.body()
                } else {
                    data.value = emptyList()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                data.value = emptyList()
            }
        })
        return data
    }

    override suspend fun likeById(id: Long): Post {
        val response = ApiModule.service.likeById(id)
        return response.body() ?: throw RuntimeException("body is null")
    }

    override suspend fun unlikeById(id: Long): Post {
        val response = ApiModule.service.unlikeById(id)
        return response.body() ?: throw RuntimeException("body is null")
    }

    override suspend fun shareById(id: Long) {
        // No-op
    }

    override suspend fun save(post: Post) {
        ApiModule.service.save(post)
    }

    override suspend fun removeById(id: Long) {
        ApiModule.service.removeById(id)
    }

    override suspend fun seed() {
        // No-op
    }
}