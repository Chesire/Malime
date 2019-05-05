package com.chesire.malime.flow.series.list.anime

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chesire.malime.R
import com.chesire.malime.SharedPref
import com.chesire.malime.core.models.SeriesModel
import com.chesire.malime.flow.series.SortOption

class AnimeAdapter(
    private val listener: AnimeInteractionListener,
    private val sharedPref: SharedPref
) : RecyclerView.Adapter<AnimeViewHolder>() {

    private var allItems = emptyList<SeriesModel>()
    private var displayedItems = emptyList<SeriesModel>()

    fun loadItems(items: List<SeriesModel>) {
        allItems = items
        performFilter()
        performSort()

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        return AnimeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_item_anime, parent, false)
        )
    }

    override fun getItemCount() = displayedItems.size

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        holder.bind(displayedItems[position])
        holder.bindListener(listener)
    }

    fun performFilter() {
        val filterOptions = sharedPref.filterPreference
        displayedItems = allItems.filter {
            filterOptions[it.userSeriesStatus.index] ?: false
        }

        notifyDataSetChanged()
    }

    fun performSort() {
        displayedItems = displayedItems
            .sortedWith(
                when (sharedPref.sortPreference) {
                    SortOption.Default -> compareBy { it.userId }
                    SortOption.Title -> compareBy { it.title }
                    SortOption.StartDate -> compareBy { it.startDate }
                    SortOption.EndDate -> compareBy { it.endDate }
                }
            )

        notifyDataSetChanged()
    }
}
