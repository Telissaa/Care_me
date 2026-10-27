package pl.wluczak.care_me.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for Reminder, linked to ActivityEntity with CASCADE deletion.
 */
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["activityId"])]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityId: Long,
    val timeInMillis: Long,
    val isRepeating: Boolean,
    val title: String = "",
    val descriptionHtml: String = "",
    val startTimeHour: Int = 8,
    val startTimeMinute: Int = 0,
    val endTimeHour: Int = 9,
    val endTimeMinute: Int = 0,
    val repeatType: String = "DAYS_OF_WEEK",
    val repeatDaysOfWeek: String = "",
    val repeatInterval: Int = 1,
    val colorHex: String = ""
)
