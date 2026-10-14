package pl.wluczak.care_me.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.wluczak.care_me.core.util.DispatcherProvider
import pl.wluczak.care_me.data.local.ActivityDao
import pl.wluczak.care_me.data.local.ReminderDao
import pl.wluczak.care_me.data.mapper.toDomain
import pl.wluczak.care_me.data.mapper.toEntity
import pl.wluczak.care_me.domain.model.Activity
import pl.wluczak.care_me.domain.model.Reminder
import pl.wluczak.care_me.domain.repository.ActivityRepository

/**
 * Implementation of ActivityRepository performing Room database operations using provided dispatchers.
 */
class ActivityRepositoryImpl(
    private val activityDao: ActivityDao,
    private val reminderDao: ReminderDao,
    private val dispatchers: DispatcherProvider,
) : ActivityRepository {

    override fun getAllActivities(): Flow<List<Activity>> {
        return activityDao.getAllActivities()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(dispatchers.io)
    }

    override fun getActivityById(id: Long): Flow<Activity?> {
        return activityDao.getActivityById(id)
            .map { it?.toDomain() }
            .flowOn(dispatchers.io)
    }

    override suspend fun insertActivity(activity: Activity): Long {
        return withContext(dispatchers.io) {
            activityDao.insertActivity(activity.toEntity())
        }
    }

    override suspend fun updateActivity(activity: Activity) {
        withContext(dispatchers.io) {
            activityDao.updateActivity(activity.toEntity())
        }
    }

    override suspend fun deleteActivity(activity: Activity) {
        withContext(dispatchers.io) {
            activityDao.deleteActivity(activity.toEntity())
        }
    }

    override suspend fun deleteActivityById(id: Long) {
        withContext(dispatchers.io) {
            activityDao.deleteActivityById(id)
        }
    }

    override fun getRemindersForActivity(activityId: Long): Flow<List<Reminder>> {
        return reminderDao.getRemindersForActivity(activityId)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(dispatchers.io)
    }

    override fun getAllReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(dispatchers.io)
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
        return withContext(dispatchers.io) {
            reminderDao.insertReminder(reminder.toEntity())
        }
    }

    override suspend fun updateReminder(reminder: Reminder) {
        withContext(dispatchers.io) {
            reminderDao.updateReminder(reminder.toEntity())
        }
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        withContext(dispatchers.io) {
            reminderDao.deleteReminder(reminder.toEntity())
        }
    }

    override suspend fun deleteReminderById(id: Long) {
        withContext(dispatchers.io) {
            reminderDao.deleteReminderById(id)
        }
    }
}
