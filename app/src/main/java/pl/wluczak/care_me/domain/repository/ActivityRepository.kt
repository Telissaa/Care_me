package pl.wluczak.care_me.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.wluczak.care_me.domain.model.Activity
import pl.wluczak.care_me.domain.model.Reminder
import pl.wluczak.care_me.domain.model.Step

/**
 * Repository interface for managing Activities, Steps, and Reminders.
 */
interface ActivityRepository {
    // Activity CRUD
    fun getAllActivities(): Flow<List<Activity>>
    fun getActivityById(id: Long): Flow<Activity?>
    suspend fun insertActivity(activity: Activity): Long
    suspend fun updateActivity(activity: Activity)
    suspend fun deleteActivity(activity: Activity)

    // Step CRUD
    fun getStepsForActivity(activityId: Long): Flow<List<Step>>
    suspend fun insertStep(step: Step): Long
    suspend fun insertSteps(steps: List<Step>): List<Long>
    suspend fun updateStep(step: Step)
    suspend fun updateSteps(steps: List<Step>)
    suspend fun deleteStep(step: Step)
    suspend fun deleteStepById(id: Long)

    // Reminder CRUD
    fun getRemindersForActivity(activityId: Long): Flow<List<Reminder>>
    suspend fun insertReminder(reminder: Reminder): Long
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
    suspend fun deleteReminderById(id: Long)
}
