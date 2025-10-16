package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity(), OnInteractionListener {

    private val viewModel: PostViewModel by viewModels()
    private val adapter = PostAdapter(this)

    private val newPostLauncher = registerForActivityResult(EditPostActivity.Contract) {
        it?.let {
            viewModel.changeContentAndSave(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        binding.fab.setOnClickListener {
            newPostLauncher.launch(null)
        }
    }

    override fun onLike(post: Post) {
        viewModel.likeById(post.id)
    }

    override fun onShare(post: Post) {
        viewModel.shareById(post.id)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.content)
            type = "text/plain"
        }
        val shareIntent =
            Intent.createChooser(intent, getString(R.string.chooser_share_post))
        startActivity(shareIntent)
    }

    override fun onEdit(post: Post) {
        newPostLauncher.launch(post.content)
        viewModel.edit(post)
    }

    override fun onRemove(post: Post) {
        viewModel.removeById(post.id)
    }

    override fun onVideo(post: Post) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
        startActivity(intent)
    }
}