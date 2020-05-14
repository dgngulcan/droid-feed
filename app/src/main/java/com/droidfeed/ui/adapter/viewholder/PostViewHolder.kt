package com.droidfeed.ui.adapter.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.data.model.Post
import com.droidfeed.ui.module.feed.PostClickListener
import com.droidfeed.util.glide.GlideApp

abstract class PostViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {

    abstract fun bind(
        post: Post,
        postClickListener: PostClickListener
    )

    protected fun bindImage(
        imageView: ImageView,
        post: Post
    ) {
        GlideApp.with(root.context)
            .load(post.image)
            .into(imageView)
    }
}