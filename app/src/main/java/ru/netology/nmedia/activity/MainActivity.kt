package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.fragment.PostFragment
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity(), OnInteractionListener {

    override fun onViewPost(post: Post) {
        val fragment = PostFragment.newInstance(post)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    val viewModel: PostViewModel by viewModels()
    private val adapter = PostAdapter(this, showPost = true)

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

        viewModel.loadPosts()

        viewModel.postsRefreshing.observe(this) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }

        binding.fab.setOnClickListener {
            newPostLauncher.launch(null)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadPosts()
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
    }

    override fun onRemove(post: Post) {
        viewModel.removeById(post.id)
    }

    override fun onVideo(post: Post) {
        val intent = Intent(Intent.ACTION_VIEW, post.video?.toUri())
        startActivity(intent)
    }
}