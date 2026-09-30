package pl.wluczak.care_me.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getDefaultCity(): Flow<String?>
    suspend fun setDefaultCity(city: String)
    
    fun getIsDarkModeEnabled(): Flow<Boolean?>
    suspend fun setIsDarkModeEnabled(isEnabled: Boolean)
}
