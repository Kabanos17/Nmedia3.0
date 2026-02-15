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

data class PostPayload(
    val likedByMe: Boolean? = null,
    val likes: Int? = null,
)

class PostAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val payload = payloads.last() as? PostPayload ?: return
            holder.bind(payload)
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    private var item: Post? = null

    init {
        binding.like.setOnClickListener {
            item?.let { onInteractionListener.onLike(it) }
        }
        binding.share.setOnClickListener {
            item?.let { onInteractionListener.onShare(it) }
        }
        binding.menu.setOnClickListener { view ->
            item?.let { post ->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
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
        binding.playButton.setOnClickListener {
            item?.let { onInteractionListener.onVideo(it) }
        }
        binding.videoPreview.setOnClickListener {
            item?.let { onInteractionListener.onVideo(it) }
        }
        binding.root.setOnClickListener {
            item?.let { onInteractionListener.onViewPost(it) }
        }
    }

    fun bind(post: Post) {
        this.item = post
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
        }
    }

    fun bind(payload: PostPayload) {
        payload.likedByMe?.let {
            binding.like.isChecked = it
        }
        payload.likes?.let {
            binding.like.text = formatCount(it.toLong())
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

    override fun getChangePayload(oldItem: Post, newItem: Post): Any? {
        if (newItem == oldItem) return null
        val payload = PostPayload(
            likedByMe = newItem.likedByMe.takeIf { it != oldItem.likedByMe },
            likes = newItem.likes.takeIf { it != oldItem.likes }
        )
        return if (payload.likedByMe == null && payload.likes == null) null else payload
    }
}