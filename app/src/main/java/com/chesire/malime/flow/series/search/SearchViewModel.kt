package com.chesire.malime.flow.series.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chesire.malime.AsyncState
import com.chesire.malime.IOContext
import com.chesire.malime.core.Resource
import com.chesire.malime.core.api.SearchApi
import com.chesire.malime.core.flags.SeriesType
import com.chesire.malime.core.models.SeriesModel
import com.chesire.malime.extensions.postError
import com.chesire.malime.extensions.postLoading
import com.chesire.malime.extensions.postSuccess
import com.chesire.malime.repo.SeriesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SearchViewModel @Inject constructor(
    private val repo: SeriesRepository,
    private val search: SearchApi,
    @IOContext private val ioContext: CoroutineContext
) : ViewModel() {

    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(job + ioContext)
    private val _searchResults = MutableLiveData<AsyncState<List<SeriesModel>, SearchError>>()

    val searchTitle = MutableLiveData<String>()
    val series: LiveData<List<SeriesModel>>
        get() = repo.series
    val searchResults: LiveData<AsyncState<List<SeriesModel>, SearchError>>
        get() = _searchResults

    var seriesType: SeriesType = SeriesType.Anime

    fun performSearch() = ioScope.launch {
        if (searchTitle.value.isNullOrEmpty()) {
            _searchResults.postError(SearchError.MissingTitle)
            return@launch
        }

        _searchResults.postLoading()

        val result = when (seriesType) {
            SeriesType.Anime -> search.searchForAnime(searchTitle.value!!)
            SeriesType.Manga -> search.searchForManga(searchTitle.value!!)
            else -> error("Unexpected series type provided")
        }

        when (result) {
            is Resource.Success -> _searchResults.postSuccess(result.data)
            is Resource.Error -> _searchResults.postError(SearchError.Error)
        }
    }
}
