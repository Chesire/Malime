package com.chesire.malime.view.search

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chesire.malime.R
import com.chesire.malime.core.api.SearchApi
import com.chesire.malime.core.flags.ItemType
import com.chesire.malime.core.models.MalimeModel
import com.chesire.malime.core.repositories.Library
import com.chesire.malime.util.IOScheduler
import com.chesire.malime.util.UIScheduler
import io.reactivex.BackpressureStrategy
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchApi: SearchApi,
    private val library: Library
) : ViewModel() {
    private val disposables = CompositeDisposable()

    @Inject
    @field:IOScheduler
    lateinit var subscribeScheduler: Scheduler

    @Inject
    @field:UIScheduler
    lateinit var observeScheduler: Scheduler

    val series: LiveData<List<MalimeModel>> = LiveDataReactiveStreams.fromPublisher(
        library.observeLibrary().toFlowable(BackpressureStrategy.ERROR)
    )
    val searchItems = MutableLiveData<List<MalimeModel>>()
    val params = SearchParams()

    fun searchForSeries(type: ItemType, @StringRes errorCallback: (Int) -> Unit) {
        if (params.searchText.isBlank() || type == ItemType.Unknown) {
            Timber.w("No text entered or type was unknown")
            return
        }

        disposables.add(
            searchApi.searchForSeriesWith(params.searchText, type)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .doOnSubscribe { params.searching = true }
                .subscribeBy(
                    onError = {
                        Timber.e(it, "Error performing the search")
                        params.searching = false
                        errorCallback(R.string.search_failed_general_error)
                    },
                    onSuccess = {
                        Timber.i("Found ${it.count()} items")
                        params.searching = false
                        searchItems.value = it
                        if (it.isEmpty()) {
                            errorCallback(R.string.search_failed_no_items)
                        }
                    }
                )
        )
    }

    fun addNewSeries(selectedSeries: MalimeModel, callback: (Boolean) -> Unit) {
        disposables.add(
            library.sendNewToApi(selectedSeries)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribeBy(
                    onError = { callback(false) },
                    onSuccess = {
                        library.insertIntoLocalLibrary(it)
                        callback(true)
                    }
                )
        )
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
