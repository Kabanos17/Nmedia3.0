package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

data class FeedModel(val posts: List<Post> = emptyList(), val empty: Boolean = false)

sealed interface FeedModelState {
    object Loading : FeedModelState
    data class Success(val feed: FeedModel) : FeedModelState
    data class Error(val e: Exception) : FeedModelState
}

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData<FeedModel>()
    val data: LiveData<FeedModel>
        get() = _data

    val edited = MutableLiveData<Post?>(null)

    private val _state = MutableLiveData<FeedModelState>()
    val state: LiveData<FeedModelState>
        get() = _state

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        _state.value = FeedModelState.Loading
        try {
            val posts = withContext(Dispatchers.IO) {
                repository.getAll()
            }
            _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
            _state.value = FeedModelState.Success(_data.value!!)
        } catch (e: Exception) {
            _state.value = FeedModelState.Error(e)
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                val updatedPost = withContext(Dispatchers.IO) {
                    val post = _data.value?.posts?.find { it.id == id } ?: throw Exception("Post not found")
                    if (post.likedByMe) {
                        repository.unlikeById(id)
                    } else {
                        repository.likeById(id)
                    }
                }
                val posts = _data.value?.posts?.map { if (it.id == id) updatedPost else it }
                _data.postValue(FeedModel(posts = posts!!))
            } catch (e: Exception) {
                _state.value = FeedModelState.Error(e)
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
            try {
                withContext(Dispatchers.IO) {
                    repository.save(post)
                }
                loadPosts()
            } catch (e: Exception) {
                _state.value = FeedModelState.Error(e)
            }
        }
        edited.value = null
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.removeById(id)
                }
                val posts = _data.value?.posts?.filter { it.id != id }
                _data.postValue(FeedModel(posts = posts!!))
            } catch (e: Exception) {
                _state.value = FeedModelState.Error(e)
            }
        }
    }
}