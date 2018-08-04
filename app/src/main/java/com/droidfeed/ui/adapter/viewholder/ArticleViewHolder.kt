package com.droidfeed.ui.adapter.viewholder

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.droidfeed.data.model.Article
import com.droidfeed.ui.module.feed.ArticleClickListener
import com.droidfeed.util.ObservableColorMatrix
import com.droidfeed.util.glide.GlideApp

abstract class ArticleViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {

    companion object {
        private const val SATURATION_DURATION = 2000L
    }

    /**
     * Binds the article to the ViewHolder.
     *
     * @param article
     * @param articleClickListener
     */
    abstract fun bind(article: Article, articleClickListener: ArticleClickListener)

    protected fun bindImage(
        imageView: ImageView,
        article: Article
    ) {
        GlideApp.with(root.context)
            .load(article.image)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (!article.hasFadedIn) {

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
                        article.hasFadedIn = true
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
            .into(imageView)
    }
}