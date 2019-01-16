package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.Post
import com.droidfeed.databinding.ListItemPostLargeBinding
import com.droidfeed.databinding.ListItemPostSmallBinding
import com.droidfeed.ui.adapter.BaseUIModel
import com.droidfeed.ui.adapter.UIModelType
import com.droidfeed.ui.adapter.viewholder.PostLargeViewHolder
import com.droidfeed.ui.adapter.viewholder.PostSmallViewHolder
import com.droidfeed.ui.adapter.viewholder.PostViewHolder
import com.droidfeed.ui.module.feed.PostClickListener

data class PostUIModel(
    private val post: Post,
    private val onPostClickListener: PostClickListener
) : BaseUIModel<PostViewHolder> {

    override fun getViewHolder(parent: ViewGroup) =
        when (post.layoutType) {
            UIModelType.POST_SMALL -> PostSmallViewHolder(
                ListItemPostSmallBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> PostLargeViewHolder(
                ListItemPostLargeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    override fun bindViewHolder(viewHolder: PostViewHolder) {
        viewHolder.bind(post, onPostClickListener)
    }

    override fun getViewType(): Int = post.layoutType.ordinal

    override fun getData() = post

}