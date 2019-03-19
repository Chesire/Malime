package com.chesire.malime.flow.series.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chesire.malime.IOContext
import com.chesire.malime.core.Resource
import com.chesire.malime.core.api.SearchApi
import com.chesire.malime.core.flags.SeriesType
import com.chesire.malime.core.models.SeriesModel
import com.chesire.malime.repo.SeriesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SearchViewModel @Inject constructor(
    private val repo: SeriesRepository,
    private val search: SearchApi,
    @IOContext private val ioContext: CoroutineContext
) : ViewModel() {

    private val job = Job()
    private val ioScope = CoroutineScope(job + ioContext)
    private val _searchResults = MutableLiveData<List<SeriesModel>>()
    val searchTitle = MutableLiveData<String>()

    val series: LiveData<List<SeriesModel>>
        get() = repo.series
    val searchResults: LiveData<List<SeriesModel>>
        get() = _searchResults

    var seriesType: SeriesType = SeriesType.Anime

    fun performSearch() = ioScope.launch {
        if (searchTitle.value.isNullOrEmpty()) {
            return@launch
        }

        val result = when (seriesType) {
            SeriesType.Anime -> search.searchForAnime(searchTitle.value!!)
            SeriesType.Manga -> search.searchForManga(searchTitle.value!!)
            else -> error("Unexpected series type provided")
        }

        when (result) {
            is Resource.Success -> _searchResults.postValue(result.data)
            is Resource.Error -> Timber.e("Failure to perform search")
        }
    }
}
