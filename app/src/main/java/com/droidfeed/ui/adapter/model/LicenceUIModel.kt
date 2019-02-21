package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.Licence
import com.droidfeed.databinding.ListItemLicenceBinding
import com.droidfeed.ui.adapter.BaseUIModel
import com.droidfeed.ui.adapter.UIModelType
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.viewholder.LicenceViewHolder

class LicenceUIModel(
    private val licence: Licence,
    private val listener: (String) -> Unit
) : BaseUIModel<LicenceViewHolder> {

    override fun getViewHolder(parent: ViewGroup): LicenceViewHolder {
        return LicenceViewHolder(
            ListItemLicenceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindViewHolder(viewHolder: LicenceViewHolder) {
        viewHolder.bind(licence, listener)
    }

    override fun getViewType(): Int = UIModelType.LICENCE.ordinal

    override fun getData(): Diffable = licence
}