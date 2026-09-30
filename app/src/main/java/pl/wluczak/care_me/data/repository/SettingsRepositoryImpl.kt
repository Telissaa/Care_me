package pl.wluczak.care_me.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.wluczak.care_me.domain.repository.SettingsRepository

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override fun getDefaultCity(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[DEFAULT_CITY_KEY]
        }
    }

    override suspend fun setDefaultCity(city: String) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_CITY_KEY] = city
        }
    }

    override fun getIsDarkModeEnabled(): Flow<Boolean?> {
        return dataStore.data.map { preferences ->
            preferences[DARK_MODE_ENABLED_KEY]
        }
    }

    override suspend fun setIsDarkModeEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_ENABLED_KEY] = isEnabled
        }
    }

    companion object {
        val DEFAULT_CITY_KEY = stringPreferencesKey("default_city")
        val DARK_MODE_ENABLED_KEY = booleanPreferencesKey("dark_mode_enabled")
    }
}
