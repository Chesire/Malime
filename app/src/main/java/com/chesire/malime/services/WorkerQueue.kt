package com.chesire.malime.services

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val SERIES_REFRESH_TAG = "SeriesRefresh"

/**
 * Allows starting up workers.
 */
class WorkerQueue @Inject constructor(private val workManager: WorkManager) {
    /**
     * Starts up the worker to perform series refreshing.
     */
    fun enqueueSeriesRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = PeriodicWorkRequestBuilder<RefreshSeriesWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag(SERIES_REFRESH_TAG)
            .build()

        workManager.enqueue(request)
    }

    /**
     * Cancels any queued workers.
     */
    fun cancelQueued() {
        workManager.cancelAllWorkByTag(SERIES_REFRESH_TAG)
    }
}
