package ru.netology.nmedia.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.db.AppDatabase
import ru.netology.nmedia.dto.Post

class PostRepositoryDbImpl(
    application: Application
) : PostRepository {

    private val dao: PostDao = AppDatabase.getInstance(application).postDao()

    private val initialPost = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        published = "21 мая в 18:36",
        likes = 999,
        likedByMe = false,
        shares = 999998,
        views = 1_200_000
    )

    init {
        GlobalScope.launch(Dispatchers.IO) {
            if (dao.getCurrentPost() == null) {
                dao.insert(initialPost)
            }
        }
    }

    override fun get(): LiveData<Post> {
        return dao.get().map { it ?: initialPost }
    }

    override fun like() {
        GlobalScope.launch(Dispatchers.IO) {
            val currentPost = dao.getCurrentPost() ?: initialPost
            
            val newLikedByMe = !currentPost.likedByMe
            val newLikes = if (newLikedByMe) currentPost.likes + 1 else currentPost.likes - 1
            dao.updateLikeStatus(newLikedByMe, newLikes)
        }
    }

    override fun share() {
        GlobalScope.launch(Dispatchers.IO) {
            val currentPost = dao.getCurrentPost() ?: initialPost
            val newShares = currentPost.shares + 1
            dao.updateShares(newShares)
        }
    }
}