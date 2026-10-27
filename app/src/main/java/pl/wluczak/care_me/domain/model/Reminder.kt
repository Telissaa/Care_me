package pl.wluczak.care_me.domain.model

import java.util.Locale

/**
 * Represents a scheduled reminder for an activity.
 */
data class Reminder(
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
) {
    val formattedTimeRange: String
        get() = String.format(Locale.getDefault(), "%02d:%02d - %02d:%02d", startTimeHour, startTimeMinute, endTimeHour, endTimeMinute)

    val formattedDays: String
        get() = if (repeatType == "INTERVAL") {
            "co $repeatInterval dni"
        } else {
            if (repeatDaysOfWeek.isEmpty()) "brak"
            else {
                val map = mapOf(1 to "pon.", 2 to "wt.", 3 to "śr.", 4 to "czw.", 5 to "pt.", 6 to "sob.", 7 to "niedz.")
                repeatDaysOfWeek.split(",").mapNotNull { map[it.toIntOrNull()] }.joinToString(", ")
            }
        }
}
