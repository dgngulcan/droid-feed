package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.Licence
import com.droidfeed.databinding.ListItemLicenceBinding
import com.droidfeed.ui.adapter.BaseUiModel
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.viewholder.LicenceViewHolder

class LicenceUiModel(
    private val licence: Licence,
    private val listener: (String) -> Unit
) : BaseUiModel<LicenceViewHolder>() {

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

    override fun getViewType(): Int = UiModelType.LICENCE.ordinal

    override fun getData(): Diffable = licence
}