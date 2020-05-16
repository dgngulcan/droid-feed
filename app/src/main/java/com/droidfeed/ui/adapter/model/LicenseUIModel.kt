package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.License
import com.droidfeed.databinding.ListItemLicenseBinding
import com.droidfeed.ui.adapter.BaseUIModel
import com.droidfeed.ui.adapter.UIModelType
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.viewholder.LicenseViewHolder

class LicenseUIModel(
    private val license: License,
    private val listener: (String) -> Unit
) : BaseUIModel<LicenseViewHolder> {

    override fun getViewHolder(parent: ViewGroup): LicenseViewHolder {
        return LicenseViewHolder(
            ListItemLicenseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindViewHolder(viewHolder: LicenseViewHolder) {
        viewHolder.bind(license, listener)
    }

    override fun getViewType(): Int = UIModelType.LICENSE.ordinal

    override fun getData(): Diffable = license
}