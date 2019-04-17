package com.chesire.malime.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chesire.malime.MalimeApplication
import com.chesire.malime.core.Resource
import com.chesire.malime.injection.components.DaggerAppComponent
import com.chesire.malime.repo.SeriesRepository
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

class RefreshSeriesWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @Inject
    lateinit var repo: SeriesRepository

    init {
        // For now setup in the init block
        // dagger currently doesn't support androidInjection for workers
        Timber.i("Initializing the RefreshSeriesWorker")
        if (appContext is MalimeApplication) {
            appContext.daggerComponent.inject(this)
        }
    }

    override suspend fun doWork(): Result {
        Timber.i("doWork RefreshSeriesWorker")

        return if (listOf(repo.refreshAnime(), repo.refreshManga()).any { it is Resource.Error }) {
            Result.retry()
        } else {
            Result.success()
        }
    }
}
