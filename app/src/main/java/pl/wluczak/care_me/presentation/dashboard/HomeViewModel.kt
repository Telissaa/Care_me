package pl.wluczak.care_me.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.Calendar
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.wluczak.care_me.core.util.DispatcherProvider
import pl.wluczak.care_me.domain.model.Activity
import pl.wluczak.care_me.domain.model.Reminder
import pl.wluczak.care_me.domain.repository.ActivityRepository

class HomeViewModel(
    private val activityRepository: ActivityRepository,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        combine(
            activityRepository.getAllActivities(),
            activityRepository.getAllReminders()
        ) { activities, reminders ->
            val todaysReminders = reminders.filter { isReminderForToday(it) }

            val notificationItems = todaysReminders.map { reminder ->
                val activity = activities.find { it.id == reminder.activityId }
                val title = activity?.name ?: "Notification"
                NotificationItem(
                    id = reminder.id,
                    activityName = title,
                    timeRange = reminder.formattedTimeRange,
                    notificationTitle = reminder.title.ifEmpty { null },
                    description = reminder.descriptionHtml,
                    repeatDays = reminder.formattedDays
                )
            }
            HomeState(
                activities = activities,
                notifications = notificationItems,
                isLoading = false
            )
        }
            .onEach { newState ->
                _state.value = newState
            }
            .flowOn(dispatcherProvider.io)
            .launchIn(viewModelScope)
    }

    private fun isReminderForToday(reminder: Reminder): Boolean {
        val todayCalendar = Calendar.getInstance()
        val todayDayOfWeek = todayCalendar[Calendar.DAY_OF_WEEK]
        // Map Calendar DAY_OF_WEEK to domain model (1 = Mon, 2 = Tue, ..., 7 = Sun)
        val todayMapped = when (todayDayOfWeek) {
            Calendar.MONDAY -> "1"
            Calendar.TUESDAY -> "2"
            Calendar.WEDNESDAY -> "3"
            Calendar.THURSDAY -> "4"
            Calendar.FRIDAY -> "5"
            Calendar.SATURDAY -> "6"
            Calendar.SUNDAY -> "7"
            else -> "1"
        }

        if (reminder.repeatType == "DAYS_OF_WEEK") {
            if (reminder.repeatDaysOfWeek.isEmpty()) {
                // No repeating days set - show only on creation day
                val reminderCalendar = Calendar.getInstance().apply { timeInMillis = reminder.timeInMillis }
                return reminderCalendar[Calendar.YEAR] == todayCalendar[Calendar.YEAR] &&
                        reminderCalendar[Calendar.DAY_OF_YEAR] == todayCalendar[Calendar.DAY_OF_YEAR]
            } else {
                return reminder.repeatDaysOfWeek.split(",").contains(todayMapped)
            }
        } else if (reminder.repeatType == "INTERVAL") {
            val todayMidnight = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val reminderMidnight = Calendar.getInstance().apply {
                timeInMillis = reminder.timeInMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val diffMillis = todayMidnight - reminderMidnight
            val diffDays = (diffMillis.toDouble() / (1000 * 60 * 60 * 24)).roundToInt()

            return diffDays >= 0 && diffDays % reminder.repeatInterval == 0
        }

        return false
    }

    fun addActivity(
        name: String,
        colorHex: String = "#D4F5FF",
        iconName: String = "face",
        onCreated: (Long) -> Unit
    ) {
        viewModelScope.launch(dispatcherProvider.io) {
            val newActivity = Activity(
                name = name,
                colorHex = colorHex,
                iconName = iconName,
                iconResId = 0
            )
            val newId = activityRepository.insertActivity(newActivity)
            withContext(dispatcherProvider.main) {
                onCreated(newId)
            }
        }
    }
}
