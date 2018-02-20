package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.Licence
import com.droidfeed.databinding.ListItemLicenceBinding
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.viewholder.LicenceViewHolder
import com.droidfeed.ui.common.BaseUiModel
import com.droidfeed.ui.adapter.UiModelClickListener

/**
 * Created by Dogan Gulcan on 11/9/17.
 */
class LicenceUiModel(
    private val licence: Licence,
    private val licenceClickListener: UiModelClickListener<Licence>
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
        viewHolder.bind(licence, licenceClickListener)
    }

    override fun getViewType(): Int = UiModelType.LICENCE.ordinal

    override fun getData(): Diffable = licence

}