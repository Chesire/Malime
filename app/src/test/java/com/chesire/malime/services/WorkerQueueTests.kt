package com.chesire.malime.services

import androidx.work.WorkManager
import androidx.work.WorkRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class WorkerQueueTests {
    @Test
    fun `enqueueSeriesRefresh enqueues the request on the manager`() {
        val mockWorkManager = mockk<WorkManager> {
            every { enqueue(any<WorkRequest>()) } returns mockk()
        }

        WorkerQueue(mockWorkManager).run {
            enqueueSeriesRefresh()
            verify { mockWorkManager.enqueue(any<WorkRequest>()) }
        }
    }

    @Test
    fun `cancelEnqueued cancels the SeriesRefresh worker`() {
        val mockWorkManager = mockk<WorkManager> {
            every { cancelAllWorkByTag("SeriesRefresh") } returns mockk()
        }

        WorkerQueue(mockWorkManager).run {
            cancelQueued()
            verify { mockWorkManager.cancelAllWorkByTag("SeriesRefresh") }
        }
    }
}
