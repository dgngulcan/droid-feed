package com.droidfeed.ui.adapter.viewholder

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.droidfeed.R
import com.droidfeed.data.model.Post
import com.droidfeed.ui.module.feed.ArticleClickListener
import com.droidfeed.util.ObservableColorMatrix
import com.droidfeed.util.glide.GlideApp
import com.droidfeed.util.glide.roundCorners

/**
 * ViewHolder to display article cards.
 */
abstract class ArticleViewHolder(private val root: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(root) {

    companion object {
        private const val SATURATION_DURATION = 2000L
    }

    /**
     * Binds the post to the ViewHolder.
     *
     * @param post
     * @param articleClickListener
     */
    abstract fun bind(post: Post, articleClickListener: ArticleClickListener)

    protected fun bindImage(
        imageView: ImageView,
        post: Post
    ) {
        GlideApp.with(root.context)
            .load(post.image)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (!post.hasFadedIn) {

                        imageView.setHasTransientState(true)
                        val cm = ObservableColorMatrix()
                        val saturation = ObjectAnimator.ofFloat(
                            cm, ObservableColorMatrix.SATURATION, 0f, 1f
                        )
                        saturation.addUpdateListener {
                            imageView.colorFilter = ColorMatrixColorFilter(cm)
                        }
                        saturation.duration = SATURATION_DURATION
                        saturation.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                imageView.clearColorFilter()
                                imageView.setHasTransientState(false)
                            }
                        })
                        saturation.start()
                        post.hasFadedIn = true
                    }
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean = false
            })
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .centerCrop()
            .roundCorners(imageView.resources.getDimension(R.dimen.card_corner_radius).toInt())
            .into(imageView)
    }
}