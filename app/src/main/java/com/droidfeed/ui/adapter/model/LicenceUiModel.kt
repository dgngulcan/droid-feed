package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.data.model.Licence
import com.droidfeed.databinding.ListItemLicenceBinding
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.viewholder.LicenceViewHolder
import com.droidfeed.ui.common.BaseUiModel

/**
 * Created by Dogan Gulcan on 11/9/17.
 */
class LicenceUiModel(
        private val licence: Licence,
        private val onClickListener: View.OnClickListener
) : BaseUiModel<LicenceViewHolder, Licence>() {

    override fun getComparable(): Comparable<Licence> {
        return licence
    }

    override fun getViewHolder(parent: ViewGroup): LicenceViewHolder {
        return LicenceViewHolder(
                ListItemLicenceBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false))
    }

    override fun bindViewHolder(viewHolder: LicenceViewHolder) {
        viewHolder.bind(licence, onClickListener)
    }

    override fun getViewType(): Int {
        return UiModelType.Licence.ordinal
    }

    override fun getData(): Diffable {
        return licence
    }

}