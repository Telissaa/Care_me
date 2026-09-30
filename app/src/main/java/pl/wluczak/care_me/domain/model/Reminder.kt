package pl.wluczak.care_me.domain.model

/**
 * Represents a scheduled reminder for an activity.
 */
data class Reminder(
    val id: Long = 0,
    val activityId: Long,
    val timeInMillis: Long,
    val isRepeating: Boolean
)
