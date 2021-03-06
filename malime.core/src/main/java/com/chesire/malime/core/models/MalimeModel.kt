package com.chesire.malime.core.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chesire.malime.core.flags.ItemType
import com.chesire.malime.core.flags.SeriesStatus
import com.chesire.malime.core.flags.Subtype
import com.chesire.malime.core.flags.UserSeriesStatus
import kotlin.math.roundToInt

private const val UNKNOWN = "??"

@Entity
data class MalimeModel(
    @PrimaryKey
    var seriesId: Int,
    var userSeriesId: Int,
    var type: ItemType,
    var subtype: Subtype,
    var slug: String,
    var title: String,
    var seriesStatus: SeriesStatus,
    var userSeriesStatus: UserSeriesStatus,
    var progress: Int,
    var totalLength: Int,
    var posterImage: String,
    var coverImage: String,
    var nsfw: Boolean,
    val startDate: String,
    val endDate: String
) {
    fun getTotalSeriesLength(): String =
        if (totalLength > 0) {
            totalLength.toString()
        } else {
            UNKNOWN
        }

    fun getSeriesProgressPercent(): Int =
        if (getTotalSeriesLength() == UNKNOWN) {
            if (progress <= 0) {
                0
            } else {
                50
            }
        } else {
            val percent = (progress.toDouble().div(totalLength.toDouble()) * 100).roundToInt()
            if (percent > 0) {
                percent
            } else {
                0
            }
        }

    fun canDecreaseProgress(): Boolean = progress > 0
    fun canIncreaseProgress(): Boolean = totalLength == 0 || progress < totalLength
}
