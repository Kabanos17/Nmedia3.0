package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity(), OnInteractionListener {

    private val viewModel: PostViewModel by viewModels()
    private val adapter = PostAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                return@observe
            }
            binding.editGroup.visibility = View.VISIBLE
            binding.fab.visibility = View.GONE
            binding.editContent.text = post.content
            binding.content.setText(post.content)
            binding.content.requestFocus()
            AndroidUtils.showKeyboard(binding.content)
        }

        binding.save.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isNotBlank()) {
                viewModel.changeContentAndSave(text)
            }
            binding.content.setText("")
            binding.content.clearFocus()
            AndroidUtils.hideKeyboard(it)
            binding.editGroup.visibility = View.GONE
            binding.fab.visibility = View.VISIBLE
        }

        binding.cancelEdit.setOnClickListener {
            viewModel.cancelEdit()
            binding.content.setText("")
            binding.content.clearFocus()
            AndroidUtils.hideKeyboard(it)
            binding.editGroup.visibility = View.GONE
            binding.fab.visibility = View.VISIBLE
        }

        binding.fab.setOnClickListener {
            binding.editGroup.visibility = View.VISIBLE
            binding.fab.visibility = View.GONE
            binding.content.requestFocus()
            AndroidUtils.showKeyboard(binding.content)
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
        viewModel.edit(post)
    }

    override fun onRemove(post: Post) {
        viewModel.removeById(post.id)
    }
}
