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

    fun calculateNextAlarmTime(): Long {
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, startTimeHour)
            set(java.util.Calendar.MINUTE, startTimeMinute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        val now = System.currentTimeMillis()
        if (calendar.timeInMillis <= now) {
            // Alarm time today has passed, shift to next possible day
            if (!isRepeating) {
                // If it's a one-time alarm but the time has passed today, assume it's for tomorrow.
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                return calendar.timeInMillis
            }

            if (repeatType == "INTERVAL") {
                calendar.add(java.util.Calendar.DAY_OF_YEAR, repeatInterval)
            } else if (repeatType == "DAYS_OF_WEEK" && repeatDaysOfWeek.isNotEmpty()) {
                val selectedDays = repeatDaysOfWeek.split(",").mapNotNull { it.toIntOrNull() }
                if (selectedDays.isNotEmpty()) {
                    var addedDays = 1
                    calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    // Calendar.DAY_OF_WEEK is 1 (Sun) to 7 (Sat)
                    // Our map: 1 to 7 is Monday to Sunday?
                    // In UI map: 1(Pn) to 7(Nd). Let's convert Calendar day to our format:
                    // Monday = 1, Sunday = 7
                    while (addedDays <= 7) {
                        val calDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
                        val ourDay = if (calDayOfWeek == java.util.Calendar.SUNDAY) 7 else calDayOfWeek - 1
                        if (selectedDays.contains(ourDay)) {
                            break
                        }
                        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                        addedDays++
                    }
                } else {
                    calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                }
            } else {
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
        } else {
            // If it's today but in the future, check if today is a valid day for DAYS_OF_WEEK
            if (repeatType == "DAYS_OF_WEEK" && repeatDaysOfWeek.isNotEmpty()) {
                val selectedDays = repeatDaysOfWeek.split(",").mapNotNull { it.toIntOrNull() }
                if (selectedDays.isNotEmpty()) {
                    val calDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
                    val ourDay = if (calDayOfWeek == java.util.Calendar.SUNDAY) 7 else calDayOfWeek - 1
                    if (!selectedDays.contains(ourDay)) {
                        var addedDays = 1
                        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                        while (addedDays <= 7) {
                            val nextCalDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
                            val nextOurDay = if (nextCalDayOfWeek == java.util.Calendar.SUNDAY) 7 else nextCalDayOfWeek - 1
                            if (selectedDays.contains(nextOurDay)) {
                                break
                            }
                            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                            addedDays++
                        }
                    }
                }
            }
        }
        return calendar.timeInMillis
    }
}
