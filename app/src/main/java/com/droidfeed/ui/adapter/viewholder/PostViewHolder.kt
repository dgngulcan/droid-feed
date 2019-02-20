package com.droidfeed.ui.adapter.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.droidfeed.R
import com.droidfeed.data.model.Post
import com.droidfeed.ui.module.feed.PostClickListener
import com.droidfeed.util.glide.GlideApp
import com.droidfeed.util.glide.roundCorners

abstract class PostViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {

    private val cornerSize = root.resources.getDimension(R.dimen.card_corner_radius).toInt()

    /**
     * Binds the post to the ViewHolder.
     *
     * @param post
     * @param postClickListener
     */
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
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .roundCorners(cornerSize)
            .into(imageView)
    }
}