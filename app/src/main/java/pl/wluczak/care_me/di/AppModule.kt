package pl.wluczak.care_me.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module
import pl.wluczak.care_me.data.repository.ActivityRepositoryImpl
import pl.wluczak.care_me.domain.repository.ActivityRepository

val appModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    single<ActivityRepository> { ActivityRepositoryImpl(get()) }
}
