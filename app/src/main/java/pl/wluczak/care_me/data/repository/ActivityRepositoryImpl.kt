package pl.wluczak.care_me.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.wluczak.care_me.data.local.ActivityDao
import pl.wluczak.care_me.data.mapper.toDomain
import pl.wluczak.care_me.data.mapper.toEntity
import pl.wluczak.care_me.domain.model.Activity
import pl.wluczak.care_me.domain.repository.ActivityRepository

class ActivityRepositoryImpl(
    private val activityDao: ActivityDao
) : ActivityRepository {

    override fun getAllActivities(): Flow<List<Activity>> {
        return activityDao.getAllActivities().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertActivity(activity: Activity) {
        activityDao.insertActivity(activity.toEntity())
    }
}
