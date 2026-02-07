package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.formatCount

interface OnInteractionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onVideo(post: Post)
    fun onViewPost(post: Post)
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val showPost: Boolean = false
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener, showPost)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
    private val showPost: Boolean = false
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        with(binding) {
            if (showPost) {
                root.setOnClickListener {
                    onInteractionListener.onViewPost(post)
                }
            } else {
                root.setOnClickListener(null)
            }
        }
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.text = formatCount(post.likes)
            share.text = formatCount(post.shares)
            views.text = formatCount(post.views)
            avatar.setImageResource(R.drawable.ic_netology_48dp)
            like.isChecked = post.likedByMe

            if (post.video.isNullOrBlank()) {
                videoPreview.visibility = View.GONE
            } else {
                videoPreview.visibility = View.VISIBLE
                playButton.visibility = View.VISIBLE
            }

            playButton.setOnClickListener {
                onInteractionListener.onVideo(post)
            }

            videoPreview.setOnClickListener {
                onInteractionListener.onVideo(post)
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}