package ru.netology.nmedia.repository

import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

class PostRepositoryDbImpl(
    private val dao: PostDao
) : PostRepository {

    override suspend fun save(post: Post) {
        dao.insert(PostEntity.fromDto(post))
    }

    override suspend fun getAll(): List<Post> {
        return dao.getAllPosts().map { it.toDto() }
    }

    override suspend fun likeById(id: Long): Post {
        dao.likeById(id)
        return dao.getById(id)!!.toDto()
    }

    override suspend fun unlikeById(id: Long): Post {
        dao.unlikeById(id)
        return dao.getById(id)!!.toDto()
    }

    override suspend fun shareById(id: Long) {
        dao.shareById(id)
    }

    override suspend fun seed() {
        // This repository shouldn't be responsible for seeding
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
    }
}