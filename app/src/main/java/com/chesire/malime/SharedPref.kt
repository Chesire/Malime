package com.chesire.malime

import android.content.SharedPreferences
import androidx.core.content.edit
import com.chesire.malime.core.flags.UserSeriesStatus
import com.chesire.malime.flow.series.SortOption
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

private const val SORT_PREFERENCE = "preference.sort"
private const val FILTER_PREFERENCE = "preference.filter"

/**
 * Provides a wrapper around the [SharedPreferences] to aid with getting and setting values into it.
 */
class SharedPref @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private val filterAdapter by lazy {
        Moshi.Builder()
            .build()
            .adapter<Map<Int, Boolean>>(
                Types.newParameterizedType(
                    Map::class.java,
                    Int::class.javaObjectType,
                    Boolean::class.javaObjectType
                )
            )
    }

    private val defaultFilter by lazy {
        filterAdapter.toJson(
            UserSeriesStatus
                .values()
                .filterNot { it == UserSeriesStatus.Unknown }
                .associate {
                    it.index to (it.index == 0)
                }
        )
    }

    var sortPreference: SortOption
        get() = SortOption.forIndex(
            sharedPreferences.getInt(
                SORT_PREFERENCE,
                SortOption.Default.index
            )
        )
        set(value) = sharedPreferences.edit {
            putInt(SORT_PREFERENCE, value.index)
        }

    var filterPreference: Map<Int, Boolean>
        get() {
            val filterJson = sharedPreferences.getString(FILTER_PREFERENCE, defaultFilter)!!
            return filterAdapter.fromJson(filterJson) ?: emptyMap()
        }
        set(value) = sharedPreferences.edit {
            putString(FILTER_PREFERENCE, filterAdapter.toJson(value))
        }
}
