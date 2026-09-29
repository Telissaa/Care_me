package pl.wluczak.care_me.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.wluczak.care_me.data.local.ActivityDao
import pl.wluczak.care_me.data.local.ReminderDao
import pl.wluczak.care_me.data.local.StepDao
import pl.wluczak.care_me.data.mapper.toDomain
import pl.wluczak.care_me.data.mapper.toEntity
import pl.wluczak.care_me.domain.model.Activity
import pl.wluczak.care_me.domain.model.Reminder
import pl.wluczak.care_me.domain.model.Step
import pl.wluczak.care_me.domain.repository.ActivityRepository

/**
 * Implementation of ActivityRepository performing Room database operations on Dispatchers.IO.
 */
class ActivityRepositoryImpl(
    private val activityDao: ActivityDao,
    private val stepDao: StepDao,
    private val reminderDao: ReminderDao
) : ActivityRepository {

    override fun getAllActivities(): Flow<List<Activity>> {
        return activityDao.getAllActivities()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getActivityById(id: Long): Flow<Activity?> {
        return activityDao.getActivityById(id)
            .map { it?.toDomain() }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun insertActivity(activity: Activity): Long {
        return withContext(Dispatchers.IO) {
            activityDao.insertActivity(activity.toEntity())
        }
    }

    override suspend fun updateActivity(activity: Activity) {
        withContext(Dispatchers.IO) {
            activityDao.updateActivity(activity.toEntity())
        }
    }

    override suspend fun deleteActivity(activity: Activity) {
        withContext(Dispatchers.IO) {
            activityDao.deleteActivity(activity.toEntity())
        }
    }

    override fun getStepsForActivity(activityId: Long): Flow<List<Step>> {
        return stepDao.getStepsForActivity(activityId)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun insertStep(step: Step): Long {
        return withContext(Dispatchers.IO) {
            stepDao.insertStep(step.toEntity())
        }
    }

    override suspend fun updateStep(step: Step) {
        withContext(Dispatchers.IO) {
            stepDao.updateStep(step.toEntity())
        }
    }

    override suspend fun deleteStep(step: Step) {
        withContext(Dispatchers.IO) {
            stepDao.deleteStep(step.toEntity())
        }
    }

    override fun getRemindersForActivity(activityId: Long): Flow<List<Reminder>> {
        return reminderDao.getRemindersForActivity(activityId)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
        return withContext(Dispatchers.IO) {
            reminderDao.insertReminder(reminder.toEntity())
        }
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderDao.deleteReminder(reminder.toEntity())
        }
    }
}
