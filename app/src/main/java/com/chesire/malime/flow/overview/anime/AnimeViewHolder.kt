package com.chesire.malime.flow.overview.anime

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chesire.malime.core.models.SeriesModel
import com.chesire.malime.extensions.visibleIf
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.adapter_item_anime.adapterItemAnimeImage
import kotlinx.android.synthetic.main.adapter_item_anime.adapterItemAnimePlusOne
import kotlinx.android.synthetic.main.adapter_item_anime.adapterItemAnimeTitle

class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {
    override val containerView: View?
        get() = itemView

    fun bind(model: SeriesModel) {
        Glide.with(itemView)
            .load(model.coverImage.largest)
            .into(adapterItemAnimeImage)
        adapterItemAnimeTitle.text = model.title
        adapterItemAnimePlusOne.visibleIf(invisible = true) { model.progress == model.totalLength }
    }
}
