package com.chesire.malime.flow.series.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chesire.malime.core.models.SeriesModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adapter_item_search.adapterItemSearchTitle

class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {
    private lateinit var seriesModel: SeriesModel
    override val containerView: View?
        get() = itemView

    fun bind(model: SeriesModel) {
        seriesModel = model
        adapterItemSearchTitle.text = model.title
    }
}
