package com.chesire.malime.injection.androidmodules

import com.chesire.malime.flow.LaunchActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeLaunchActivity(): LaunchActivity
}