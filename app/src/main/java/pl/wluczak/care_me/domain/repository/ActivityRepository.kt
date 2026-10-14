package pl.wluczak.care_me.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.wluczak.care_me.domain.model.Activity
import pl.wluczak.care_me.domain.model.Reminder

/**
 * Repository interface for managing Activities and Reminders.
 */
interface ActivityRepository {
    // Activity CRUD
    fun getAllActivities(): Flow<List<Activity>>
    fun getActivityById(id: Long): Flow<Activity?>
    suspend fun insertActivity(activity: Activity): Long
    suspend fun updateActivity(activity: Activity)
    suspend fun deleteActivity(activity: Activity)
    suspend fun deleteActivityById(id: Long)

    // Reminder CRUD
    fun getRemindersForActivity(activityId: Long): Flow<List<Reminder>>
    fun getAllReminders(): Flow<List<Reminder>>
    suspend fun insertReminder(reminder: Reminder): Long
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
    suspend fun deleteReminderById(id: Long)
}
