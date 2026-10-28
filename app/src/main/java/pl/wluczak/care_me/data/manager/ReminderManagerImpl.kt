package pl.wluczak.care_me.data.manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import pl.wluczak.care_me.core.receiver.AlarmReceiver
import pl.wluczak.care_me.domain.manager.ReminderManager
import pl.wluczak.care_me.domain.model.Reminder

class ReminderManagerImpl(
    private val context: Context
) : ReminderManager {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleReminder(reminder: Reminder) {
        if (reminder.id <= 0) return // Cannot schedule without ID

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(AlarmReceiver.EXTRA_ACTIVITY_ID, reminder.activityId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = reminder.calculateNextAlarmTime()
        Log.d("ReminderManager", "Scheduling reminder ${reminder.id} for time: $triggerTime")

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    // Fallback if permission is missing, but should be requested in UI
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: SecurityException) {
            Log.e("ReminderManager", "Failed to schedule alarm: ${e.message}")
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    override fun cancelReminder(reminderId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("ReminderManager", "Cancelled reminder $reminderId")
    }
}
