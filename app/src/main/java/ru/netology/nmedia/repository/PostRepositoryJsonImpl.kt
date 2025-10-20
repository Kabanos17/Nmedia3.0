package ru.netology.nmedia.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.netology.nmedia.dto.Post
import kotlin.coroutines.cancellation.CancellationException

class PostRepositoryJsonImpl(
    private val application: Application
) : PostRepository {

    private val gson = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    private val filename = "posts.json"

    private val data = MutableLiveData<List<Post>>()

    init {
        application.applicationContext.filesDir.resolve(filename).let { file ->
            if (file.exists()) {
                application.applicationContext.openFileInput(filename).bufferedReader().use {
                    data.value = gson.decodeFromString<List<Post>>(it.readText())
                }
            } else {
                data.value = emptyList()
            }
        }
    }


    private fun sync() {
        application.applicationContext.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.encodeToString(data.value))
        }
    }

    override fun getAll(): LiveData<List<Post>> = data
    override suspend fun likeById(id: Long) {
        withContext(Dispatchers.IO) {
            data.postValue(
                data.value?.map {
                    if (it.id == id) {
                        it.copy(
                            likedByMe = !it.likedByMe,
                            likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
                        )
                    } else {
                        it
                    }
                }
            )
            sync()
        }
    }

    override suspend fun shareById(id: Long) {
        withContext(Dispatchers.IO) {
            data.postValue(
                data.value?.map {
                    if (it.id == id) {
                        it.copy(shares = it.shares + 1)
                    } else {
                        it
                    }
                }
            )
            sync()
        }
    }

    override suspend fun seed() {
        if (data.value.isNullOrEmpty()) {
            withContext(Dispatchers.IO) {
                data.postValue(
                    listOf(
                        Post(
                            id = 0,
                            author = "Нетология. Университет интернет-профессий будущего",
                            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
                            published = "22 мая в 18:36",
                            likes = 1999,
                            likedByMe = false,
                            shares = 10999,
                            views = 1_500_000,
                            video = "https://rutube.ru/video/530e4b5e88ff2cd438bae9d46286b641/?r=wd"
                        ),
                        Post(
                            id = 0,
                            author = "Нетология. Университет интернет-профессий будущего",
                            content = "Знаний хватит на всех: на следующей неделе разбираемся с разработкой мобильных приложений, учимся рассказывать истории и составлять PR-стратегию прямо на бесплатных занятиях \uD83D\uDC47",
                            published = "21 мая в 18:36",
                            likes = 999,
                            likedByMe = false,
                            shares = 999998,
                            views = 1_200_000
                        )
                    ).mapIndexed { index, post -> post.copy(id = index + 1L) }
                )
                sync()
            }
        }
    }

    override suspend fun save(post: Post) {
        withContext(Dispatchers.IO) {
            if (post.id == 0L) {
                data.postValue(
                    listOf(
                        post.copy(
                            id = (data.value?.firstOrNull()?.id ?: 0L) + 1,
                            author = "Me",
                            published = "now"
                        )
                    ) + (data.value ?: emptyList())
                )
                sync()
                return@withContext
            }

            data.postValue(
                data.value?.map {
                    if (it.id == post.id) {
                        it.copy(content = post.content)
                    } else {
                        it
                    }
                }
            )
            sync()
        }
    }

    override suspend fun removeById(id: Long) {
        withContext(Dispatchers.IO) {
            data.postValue(
                data.value?.filter { it.id != id }
            )
            sync()
        }
    }
}