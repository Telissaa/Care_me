package pl.wluczak.care_me.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pl.wluczak.care_me.domain.manager.ReminderManager
import pl.wluczak.care_me.domain.repository.ActivityRepository

class BootReceiver : BroadcastReceiver(), KoinComponent {
    
    private val activityRepository: ActivityRepository by inject()
    private val reminderManager: ReminderManager by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val reminders = activityRepository.getAllReminders().first()
                    for (reminder in reminders) {
                        reminderManager.scheduleReminder(reminder)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
