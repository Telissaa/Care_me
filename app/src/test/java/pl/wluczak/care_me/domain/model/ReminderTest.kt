package pl.wluczak.care_me.domain.model

import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ReminderTest {

    @Test
    fun `calculateNextAlarmTime schedules for tomorrow if time passed and not repeating`() {
        val now = Calendar.getInstance()
        val reminder = Reminder(
            activityId = 1,
            timeInMillis = System.currentTimeMillis(),
            isRepeating = false,
            startTimeHour = now.get(Calendar.HOUR_OF_DAY) - 1, // 1 hour ago
            startTimeMinute = now.get(Calendar.MINUTE)
        )
        
        val targetTime = reminder.calculateNextAlarmTime()
        
        val targetCal = Calendar.getInstance().apply { timeInMillis = targetTime }
        
        // Target should be tomorrow
        assertTrue("Alarm should be scheduled for tomorrow", targetCal.get(Calendar.DAY_OF_YEAR) > now.get(Calendar.DAY_OF_YEAR) || targetCal.get(Calendar.YEAR) > now.get(Calendar.YEAR))
    }

    @Test
    fun `calculateNextAlarmTime schedules for today if time is in future`() {
        val now = Calendar.getInstance()
        val reminder = Reminder(
            activityId = 1,
            timeInMillis = System.currentTimeMillis(),
            isRepeating = false,
            startTimeHour = now.get(Calendar.HOUR_OF_DAY) + 1, // 1 hour in future
            startTimeMinute = now.get(Calendar.MINUTE)
        )
        
        val targetTime = reminder.calculateNextAlarmTime()
        val targetCal = Calendar.getInstance().apply { timeInMillis = targetTime }
        
        // Target should be today
        assertTrue("Alarm should be scheduled for today", targetCal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR))
    }
}
