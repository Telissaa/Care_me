package pl.wluczak.care_me.data.mapper

import pl.wluczak.care_me.data.local.entity.ReminderEntity
import pl.wluczak.care_me.domain.model.Reminder

/**
 * Mappers for converting between ReminderEntity and Reminder domain model.
 */
fun ReminderEntity.toDomain(): Reminder {
    return Reminder(
        id = id,
        activityId = activityId,
        timeInMillis = timeInMillis,
        isRepeating = isRepeating,
        title = title,
        descriptionHtml = descriptionHtml,
        startTimeHour = startTimeHour,
        startTimeMinute = startTimeMinute,
        endTimeHour = endTimeHour,
        endTimeMinute = endTimeMinute,
        repeatType = repeatType,
        repeatDaysOfWeek = repeatDaysOfWeek,
        repeatInterval = repeatInterval,
        colorHex = colorHex,
    )
}

fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        activityId = activityId,
        timeInMillis = timeInMillis,
        isRepeating = isRepeating,
        title = title,
        descriptionHtml = descriptionHtml,
        startTimeHour = startTimeHour,
        startTimeMinute = startTimeMinute,
        endTimeHour = endTimeHour,
        endTimeMinute = endTimeMinute,
        repeatType = repeatType,
        repeatDaysOfWeek = repeatDaysOfWeek,
        repeatInterval = repeatInterval,
        colorHex = colorHex,
    )
}
