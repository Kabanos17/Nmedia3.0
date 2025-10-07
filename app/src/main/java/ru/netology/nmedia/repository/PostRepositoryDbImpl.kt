package ru.netology.nmedia.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.db.AppDatabase
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

class PostRepositoryDbImpl(
    application: Application
) : PostRepository {

    private val dao: PostDao = AppDatabase.getInstance(application).postDao()

    override fun getAll(): LiveData<List<Post>> = dao.getAll()
        .map { list -> list.map(PostEntity::toDto) }

    override suspend fun likeById(id: Long) {
        dao.likeById(id)
    }

    override suspend fun shareById(id: Long) {
        dao.shareById(id)
    }

    override suspend fun seed() {
        if (dao.isEmpty()) {
            dao.insert(
                PostEntity(
                    id = 0,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Привет, это новая Нетология!",
                    published = "22 мая в 18:36",
                    likes = 1999,
                    likedByMe = false,
                    shares = 10999,
                    views = 1_500_000
                )
            )
            dao.insert(
                PostEntity(
                    id = 0,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Знаний хватит на всех: на следующей неделе разбираемся с разработкой мобильных приложений",
                    published = "21 мая в 18:36",
                    likes = 999,
                    likedByMe = false,
                    shares = 999998,
                    views = 1_200_000
                )
            )
        }
    }

    override suspend fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
    }
}