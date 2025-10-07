package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryDbImpl

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likes = 0,
    likedByMe = false,
    shares = 0,
    views = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryDbImpl(application)
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    init {
        viewModelScope.launch {
            repository.seed()
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            repository.likeById(id)
        }
    }

    fun shareById(id: Long) {
        viewModelScope.launch {
            repository.shareById(id)
        }
    }

    fun changeContentAndSave(content: String) {
        edited.value?.let {
            val post = it.copy(content = content)
            viewModelScope.launch {
                repository.save(post)
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            repository.removeById(id)
        }
    }

    fun cancelEdit() {
        edited.value = empty
    }
}
