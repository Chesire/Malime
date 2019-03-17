package com.chesire.malime.flow.series.list.anime

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.chesire.malime.IOContext
import com.chesire.malime.core.flags.UserSeriesStatus
import com.chesire.malime.core.models.SeriesModel
import com.chesire.malime.repo.SeriesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AnimeViewModel @Inject constructor(
    private val repo: SeriesRepository,
    @IOContext private val ioContext: CoroutineContext
) : ViewModel() {

    private val job = Job()
    private val ioScope = CoroutineScope(job + ioContext)
    val animeSeries: LiveData<List<SeriesModel>>
        get() = repo.anime

    fun updateSeries(userSeriesId: Int, newProgress: Int, newUserSeriesStatus: UserSeriesStatus) =
        ioScope.launch {
            repo.updateSeries(userSeriesId, newProgress, newUserSeriesStatus)
        }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}