package com.chesire.malime.flow.series

import androidx.annotation.StringRes
import com.chesire.malime.R

/**
 * Options available for when sorting the series list is performed.
 */
enum class SortOption(val index: Int, @StringRes val stringId: Int) {
    Default(0, R.string.sort_by_default),
    Title(1, R.string.sort_by_title),
    StartDate(2, R.string.sort_by_start_date),
    EndDate(3, R.string.sort_by_end_date),
}
