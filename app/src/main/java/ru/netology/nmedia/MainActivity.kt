package ru.netology.nmedia

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodels.PostViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PostViewModel by viewModels()

    // Define a TAG for logging
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: View binding initialized.")

        viewModel.post.observe(this) { post ->
            Log.d(TAG, "LiveData observer: Received post data: $post")
            if (post == null) {
                Log.e(TAG, "LiveData observer: Post data is null!")
                return@observe
            }
            bindPost(post)
        }

        binding.like.setOnClickListener {
            Log.d(TAG, "Like button clicked")
            viewModel.toggleLike()
        }

        binding.share.setOnClickListener {
            Log.d(TAG, "Share button clicked")
            viewModel.share()
        }
    }

    private fun bindPost(post: Post) {
        Log.d(TAG, "bindPost: Binding post data: $post")
        with(binding) {
            // Check if views are null (they shouldn't be with view binding if IDs are correct)
            if (author == null) {
                Log.e(TAG, "bindPost: binding.author is null!")
            }
            if (published == null) {
                Log.e(TAG, "bindPost: binding.published is null!")
            }
            if (content == null) {
                Log.e(TAG, "bindPost: binding.content is null!")
            }
            if (avatar == null) {
                Log.e(TAG, "bindPost: binding.avatar is null!")
            }

            author.text = post.author
            published.text = post.published
            content.text = post.content
            likeCount.text = formatCount(post.likes)
            shareCount.text = formatCount(post.shares)
            shareEye.text = formatCount(post.views)

            Log.d(TAG, "bindPost: Author: '${post.author}', Published: '${post.published}'")
            Log.d(TAG, "bindPost: Content: '${post.content?.take(30)}...'")
            Log.d(
                TAG,
                "bindPost: Likes: ${post.likes}, Shares: ${post.shares}, Views: ${post.views}"
            )

            avatar.setImageResource(R.drawable.ic_netology_48dp)

            val likeIconRes = if (post.likedByMe) {
                R.drawable.ic_liked_24
            } else {
                R.drawable.ic_like_24
            }
            like.setImageResource(likeIconRes)
            Log.d(TAG, "bindPost: Images set. Liked: ${post.likedByMe}")
        }
    }
}