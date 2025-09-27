// PostViewModel.kt
package ru.netology.nmedia.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post

class PostViewModel : ViewModel() {
    private val _post = MutableLiveData(
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likes = 999,
            likedByMe = false,
            shares = 999,
            views = 1000
        )
    )
    val post: LiveData<Post> = _post

    fun toggleLike() {
        val currentPost = _post.value ?: return
        val liked = !currentPost.likedByMe
        val likesCount = if (liked) currentPost.likes + 1 else currentPost.likes - 1
        _post.value = currentPost.copy(likedByMe = liked, likes = likesCount)
    }

    fun share() {
        val currentPost = _post.value ?: return
        _post.value = currentPost.copy(shares = currentPost.shares + 1)
    }
}

