package pl.wluczak.care_me.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.wluczak.care_me.data.local.entity.ReminderEntity

/**
 * DAO interface for Reminder table operations.
 */
@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE activityId = :activityId")
    fun getRemindersForActivity(activityId: Long): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Long)
}
