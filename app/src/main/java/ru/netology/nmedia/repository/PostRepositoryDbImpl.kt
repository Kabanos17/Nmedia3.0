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
                listOf(
                    PostEntity(
                        id = 0,
                        author = "Нетология. Университет интернет-профессий будущего",
                        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
                        published = "22 мая в 18:36",
                        likes = 1999,
                        likedByMe = false,
                        shares = 10999,
                        views = 1_500_000
                    ),
                    PostEntity(
                        id = 0,
                        author = "Нетология. Университет интернет-профессий будущего",
                        content = "Знаний хватит на всех: на следующей неделе разбираемся с разработкой мобильных приложений, учимся рассказывать истории и составлять PR-стратегию прямо на бесплатных занятиях \uD83D\uDC47",
                        published = "21 мая в 18:36",
                        likes = 999,
                        likedByMe = false,
                        shares = 999998,
                        views = 1_200_000
                    ),
                    PostEntity(
                        id = 0,
                        author = "Нетология. Университет интернет-профессий будущего",
                        content = "Языков программирования много, и выбрать какой-то один бывает нелегко. Собрали подборку статей, которая поможет вам начать, если вы остановили свой выбор на JavaScript.",
                        published = "20 мая в 18:36",
                        likes = 999,
                        likedByMe = false,
                        shares = 999998,
                        views = 1_200_000
                    ),
                    PostEntity(
                        id = 0,
                        author = "Нетология. Университет интернет-профессий будущего",
                        content = "Диджитал давно стал частью нашей жизни: мы общаемся в социальных сетях и мессенджерах, заказываем еду, такси и оплачиваем счета через приложения.",
                        published = "19 мая в 18:36",
                        likes = 999,
                        likedByMe = false,
                        shares = 999998,
                        views = 1_200_000
                    )
                ).map { it.copy(id = 0) }
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
