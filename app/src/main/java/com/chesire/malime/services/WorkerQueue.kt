package com.chesire.malime.services

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Allows starting up workers.
 */
class WorkerQueue {
    /**
     * Starts up the worker to perform series refreshing.
     */
    fun enqueueSeriesRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = PeriodicWorkRequestBuilder<RefreshSeriesWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag("SeriesRefresh")
            .build()

        WorkManager.getInstance().enqueue(request)
    }
}
