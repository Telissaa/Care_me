package pl.wluczak.care_me.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.wluczak.care_me.domain.model.Activity

interface ActivityRepository {
    fun getAllActivities(): Flow<List<Activity>>
    suspend fun insertActivity(activity: Activity)
}
