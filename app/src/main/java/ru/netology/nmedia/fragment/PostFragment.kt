package ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import ru.netology.nmedia.activity.EditPostActivity
import ru.netology.nmedia.activity.MainActivity
import ru.netology.nmedia.formatCount


class PostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = CardPostBinding.inflate(inflater, container, false)
        val post = arguments?.getParcelable<Post>(ARG_POST)
        if (post == null) return binding.root

        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.text = formatCount(post.likes.toLong())
            share.text = formatCount(post.shares.toLong())
            views.text = formatCount(post.views.toLong())
            avatar.setImageResource(R.drawable.ic_netology_48dp)
            like.isChecked = post.likedByMe

            if (post.video.isNullOrBlank()) {
                videoPreview.visibility = View.GONE
                playButton.visibility = View.GONE
            } else {
                videoPreview.visibility = View.VISIBLE
                playButton.visibility = View.VISIBLE
            }

            like.setOnClickListener {
                viewModel.likeById(post.id)
            }

            share.setOnClickListener {
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

            menu.setOnClickListener { view ->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                viewModel.removeById(post.id)
                                // Return to list after deletion
                                requireActivity().onBackPressed()
                                true
                            }
                            R.id.edit -> {
                                val intent = Intent(view.context, EditPostActivity::class.java)
                                intent.putExtra(EditPostActivity.Companion.Contract.EXTRA_TEXT, post.content)
                                startActivity(intent)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }

        return binding.root
    }

    companion object {
        private const val ARG_POST = "post"

        fun newInstance(post: Post): PostFragment {
            val args = Bundle().apply {
                putParcelable(ARG_POST, post)
            }
            val fragment = PostFragment()
            fragment.arguments = args
            return fragment
        }
    }
}