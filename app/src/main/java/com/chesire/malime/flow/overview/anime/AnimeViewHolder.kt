package com.chesire.malime.flow.overview.anime

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chesire.malime.core.models.SeriesModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adapter_item_anime.adapterItemAnimeTitle

class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {
    override val containerView: View?
        get() = itemView

    fun bind(model: SeriesModel) {
        adapterItemAnimeTitle.text = model.title
    }
}
