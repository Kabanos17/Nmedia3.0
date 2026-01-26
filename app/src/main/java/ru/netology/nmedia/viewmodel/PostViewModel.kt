package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryDbImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryDbImpl(application)
    val data = repository.getAll()
    val edited = MutableLiveData<Post?>(null)

    private val _postsRefreshing = MutableLiveData<Boolean>()
    val postsRefreshing: LiveData<Boolean> = _postsRefreshing

    fun loadPosts() = viewModelScope.launch {
        _postsRefreshing.value = true
        try {
            repository.getAll()
        } finally {
            _postsRefreshing.postValue(false)
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            val post = data.value?.find { it.id == id } ?: return@launch
            if (post.likedByMe) {
                repository.unlikeById(id)
            } else {
                repository.likeById(id)
            }
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
