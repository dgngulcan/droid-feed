package com.droidfeed.ui.adapter.viewholder

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.droidfeed.R
import com.droidfeed.data.model.Article
import com.droidfeed.databinding.ListItemNewsBinding
import com.droidfeed.ui.module.news.NewsItemClickListener
import com.droidfeed.util.ObservableColorMatrix

/**
 * Created by Dogan Gulcan on 11/2/17.
 */
class RssViewHolder(private val binding: ListItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(article: Article, onRssClickListener: NewsItemClickListener) {
        binding.clickListener = onRssClickListener
        binding.rssItem = article

        Glide.with(binding.root.context)
                .load(article.image)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        if (!article.hasFadedIn) {
                            binding.imgArticle.setHasTransientState(true)
                            val cm = ObservableColorMatrix()
                            val saturation = ObjectAnimator.ofFloat(
                                    cm, ObservableColorMatrix.SATURATION, 0f, 1f)
                            saturation.addUpdateListener(ValueAnimator.AnimatorUpdateListener {
                                binding.imgArticle.setColorFilter(ColorMatrixColorFilter(cm))
                            })
                            saturation.duration = 1000L
                            saturation.addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    binding.imgArticle.clearColorFilter()
                                    binding.imgArticle.setHasTransientState(false)
                                }
                            })
                            saturation.start()
                            article.hasFadedIn = true
                        }
                        return true
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                })
                .apply(RequestOptions()
                        .error(binding.root.context.getDrawable(R.drawable.ic_broken_image_black_24dp)))
                .into(binding.imgArticle)
    }

}