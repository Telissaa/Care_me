package pl.wluczak.care_me.di

import android.content.Context
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import pl.wluczak.care_me.data.repository.ActivityRepositoryImpl
import pl.wluczak.care_me.data.repository.SettingsRepositoryImpl
import pl.wluczak.care_me.data.repository.UvRepositoryImpl
import pl.wluczak.care_me.data.repository.dataStore
import pl.wluczak.care_me.domain.repository.ActivityRepository
import pl.wluczak.care_me.domain.repository.SettingsRepository
import pl.wluczak.care_me.domain.repository.UvRepository
import pl.wluczak.care_me.core.util.DispatcherProvider
import pl.wluczak.care_me.core.util.StandardDispatcherProvider
import pl.wluczak.care_me.presentation.dashboard.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {
    single<DispatcherProvider> { StandardDispatcherProvider() }

    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    single<ActivityRepository> { ActivityRepositoryImpl(get(), get(), get(), get()) }
    single<UvRepository> { UvRepositoryImpl(get(), get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get<Context>().dataStore) }

    viewModel { HomeViewModel(get(), get()) }
}
