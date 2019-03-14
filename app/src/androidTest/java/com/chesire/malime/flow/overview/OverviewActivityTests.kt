package com.chesire.malime.flow.overview

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.chesire.malime.R
import com.chesire.malime.TestApplication
import com.chesire.malime.core.api.AuthApi
import com.chesire.malime.injection.components.DaggerTestComponent
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
class OverviewActivityTests {
    @Inject
    lateinit var authApi: AuthApi

    @get:Rule
    val overviewActivity = BaristaRule.create(OverviewActivity::class.java)

    @Before
    fun setUp() {
        val app = getInstrumentation().targetContext.applicationContext as TestApplication
        app.component = DaggerTestComponent
            .builder()
            .applicationContext(app)
            .build()
        app.component.inject(this)
    }

    @Test
    fun overviewStartsInAnimeView() {
        val auth = authApi

        overviewActivity.launchActivity()
        assertDisplayed(R.string.nav_anime)
    }

    @Test
    @Ignore("Will come back to UI testing")
    fun overviewCanNavigateToAnimeView() {
        overviewActivity.launchActivity()
        assertDisplayed(R.string.nav_anime)
        clickOn(R.id.overviewNavManga)
        assertDisplayed(R.string.nav_manga)
        clickOn(R.id.overviewNavAnime)
        assertDisplayed(R.string.nav_anime)
    }

    @Test
    @Ignore("Will come back to UI testing")
    fun overviewCanNavigateToMangaView() {
        overviewActivity.launchActivity()
        clickOn(R.id.overviewNavManga)
        assertDisplayed(R.string.nav_manga)
    }

    @Test
    @Ignore("Will come back to UI testing")
    fun overviewCanNavigateToProfileView() {
        overviewActivity.launchActivity()
        clickOn(R.id.overviewNavProfile)
        assertDisplayed(R.string.nav_profile)
    }

    @Test
    @Ignore("Will come back to UI testing")
    fun overviewCanNavigateToActivityView() {
        overviewActivity.launchActivity()
        clickOn(R.id.overviewNavActivity)
        assertDisplayed(R.string.nav_activity)
    }

    @Test
    @Ignore("Will come back to UI testing")
    fun overviewCanNavigateToSettingsView() {
        overviewActivity.launchActivity()
        clickOn(R.id.overviewNavSettings)
        assertDisplayed(R.string.nav_settings)
    }
}
