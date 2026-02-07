package ru.netology.nmedia.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.db.AppDatabase
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.io.IOException

class PostRepositoryDbImpl(
    application: Application
) : PostRepository {

    private val dao: PostDao = AppDatabase.getInstance(application).postDao()

    override suspend fun save(post: Post) {
        try {
            val response = PostApi.service.save(post)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            val body = response.body() ?: throw RuntimeException("body is null")
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getAll(): LiveData<List<Post>> = dao.getAll()
        .map { list -> list.map(PostEntity::toDto) }

    override suspend fun likeById(id: Long): Post {
        dao.likeById(id)
        try {
            val response = PostApi.service.likeById(id)
            if (!response.isSuccessful) {
                throw RuntimeException("api error")
            }
            val body = response.body() ?: throw RuntimeException("body is null")
            dao.insert(PostEntity.fromDto(body))
            return body
        } catch (e: Exception) {
            dao.unlikeById(id)
            throw e
        }
    }

    override suspend fun unlikeById(id: Long): Post {
        dao.unlikeById(id)
        try {
            val response = PostApi.service.unlikeById(id)
            if (!response.isSuccessful) {
                throw RuntimeException("api error")
            }
            val body = response.body() ?: throw RuntimeException("body is null")
            dao.insert(PostEntity.fromDto(body))
            return body
        } catch (e: Exception) {
            dao.likeById(id)
            throw e
        }
    }

    override suspend fun shareById(id: Long) {
        dao.shareById(id)
    }

    override suspend fun seed() {
        // No-op for DB implementation
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
    }
}
