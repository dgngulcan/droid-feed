package com.droidfeed.ui.adapter.viewholder

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.droidfeed.R
import com.droidfeed.data.model.Post
import com.droidfeed.ui.module.feed.PostClickListener
import com.droidfeed.util.glide.GlideApp
import com.droidfeed.util.glide.roundCorners

/**
 * ViewHolder to display article cards.
 */
abstract class PostViewHolder(private val root: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(root) {

    companion object {
        private const val SATURATION_DURATION = 2000L
    }

    /**
     * Binds the post to the ViewHolder.
     *
     * @param post
     * @param postClickListener
     */
    abstract fun bind(post: Post, postClickListener: PostClickListener)

    protected fun bindImage(
        imageView: ImageView,
        post: Post
    ) {
        GlideApp.with(root.context)
            .load(post.image)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .centerCrop()
            .roundCorners(imageView.resources.getDimension(R.dimen.card_corner_radius).toInt())
            .into(imageView)
    }
}