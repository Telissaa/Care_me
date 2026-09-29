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
        isRepeating = isRepeating
    )
}

fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        activityId = activityId,
        timeInMillis = timeInMillis,
        isRepeating = isRepeating
    )
}
