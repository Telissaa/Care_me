package pl.wluczak.care_me.core.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.Html
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pl.wluczak.care_me.MainActivity
import pl.wluczak.care_me.R
import pl.wluczak.care_me.domain.manager.ReminderManager
import pl.wluczak.care_me.domain.repository.ActivityRepository

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val activityRepository: ActivityRepository by inject()
    private val reminderManager: ReminderManager by inject()

    companion object {
        const val EXTRA_REMINDER_ID = "EXTRA_REMINDER_ID"
        const val EXTRA_ACTIVITY_ID = "EXTRA_ACTIVITY_ID"
        private const val CHANNEL_ID = "reminder_channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
        if (reminderId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminder = activityRepository.getReminderById(reminderId)
                if (reminder != null) {
                    showNotification(context, reminder.title, reminder.descriptionHtml, reminder.id.toInt())
                    
                    if (reminder.isRepeating) {
                        reminderManager.scheduleReminder(reminder)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, title: String, descriptionHtml: String, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val strippedDescription = Html.fromHtml(descriptionHtml, Html.FROM_HTML_MODE_COMPACT).toString().trim()
        val displayTitle = if (title.isEmpty()) "Care me" else title

        android.util.Log.d("AlarmReceiver", "Showing notification for reminder $notificationId, title: $displayTitle")

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // Use app's launcher icon instead of android system drawable
            .setContentTitle(displayTitle)
            .setContentText(strippedDescription)
            .setStyle(NotificationCompat.BigTextStyle().bigText(strippedDescription))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, builder.build())
        android.util.Log.d("AlarmReceiver", "Notification $notificationId sent to NotificationManager")
    }
}
