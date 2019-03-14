package com.chesire.malime

import android.app.Application
import com.chesire.malime.injection.components.DaggerTestComponent

/**
 * Overridden application object that provides a dagger component.
 */
class TestApplication : Application() {
    var component = DaggerTestComponent
        .builder()
        .applicationContext(this)
        .build()
}
