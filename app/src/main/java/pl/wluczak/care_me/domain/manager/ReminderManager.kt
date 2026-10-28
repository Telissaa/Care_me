package pl.wluczak.care_me.domain.manager

import pl.wluczak.care_me.domain.model.Reminder

interface ReminderManager {
    fun scheduleReminder(reminder: Reminder)
    fun cancelReminder(reminderId: Long)
}
