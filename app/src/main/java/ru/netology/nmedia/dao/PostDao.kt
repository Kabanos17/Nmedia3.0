package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.dto.Post

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE id = 1")
    fun get(): LiveData<Post?>

    @Query("SELECT * FROM posts WHERE id = 1")
    suspend fun getCurrentPost(): Post?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post)

    @Query("UPDATE posts SET likedByMe = :likedByMe, likes = :likes WHERE id = 1")
    suspend fun updateLikeStatus(likedByMe: Boolean, likes: Int)

    @Query("UPDATE posts SET shares = :shares WHERE id = 1")
    suspend fun updateShares(shares: Int)
}