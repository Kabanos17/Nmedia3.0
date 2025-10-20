package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryJsonImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryJsonImpl(application)
    val data = repository.getAll()
    val edited = MutableLiveData<Post?>(null)

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
        val post = edited.value?.copy(content = content) ?: Post.emptyPost.copy(content = content, author = "Me", published = "now")
        viewModelScope.launch {
            repository.save(post)
        }
        edited.value = null
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            repository.removeById(id)
        }
    }
}
