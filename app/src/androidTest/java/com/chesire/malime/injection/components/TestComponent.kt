package com.chesire.malime.injection.components

import android.content.Context
import com.chesire.malime.TestApplication
import com.chesire.malime.flow.overview.OverviewActivityTests
import com.chesire.malime.injection.androidmodules.ActivityModule
import com.chesire.malime.injection.androidmodules.FragmentModule
import com.chesire.malime.injection.androidmodules.ViewModelModule
import com.chesire.malime.injection.modules.AppModule
import com.chesire.malime.injection.modules.CoroutinesModule
import com.chesire.malime.injection.modules.DatabaseModule
import com.chesire.malime.injection.modules.KitsuModule
import com.chesire.malime.injection.modules.ServerModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ActivityModule::class,
        AndroidSupportInjectionModule::class,
        AppModule::class,
        CoroutinesModule::class,
        DatabaseModule::class,
        FragmentModule::class,
        KitsuModule::class,
        ServerModule::class,
        ViewModelModule::class
    ]
)
interface TestComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(applicationContext: Context): Builder

        fun build(): TestComponent
    }

    fun inject(application: TestApplication)
    fun inject(overviewActivityTests: OverviewActivityTests)
}
