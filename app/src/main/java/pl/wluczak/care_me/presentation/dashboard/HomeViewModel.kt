package pl.wluczak.care_me.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
import pl.wluczak.care_me.domain.repository.ActivityRepository

class HomeViewModel(
    private val activityRepository: ActivityRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        combine(
            activityRepository.getAllActivities(),
            activityRepository.getAllReminders()
        ) { activities, reminders ->
            val notificationItems = reminders.map { reminder ->
                val activity = activities.find { it.id == reminder.activityId }
                val title = activity?.name ?: "Notification"
                val timeFormatted = formatTime(reminder.timeInMillis)
                NotificationItem(
                    id = reminder.id,
                    activityName = title,
                    timeRange = timeFormatted
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

    private fun formatTime(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = sdf.format(Date(timeInMillis))
        val end = sdf.format(Date(timeInMillis + 3600000L))
        return "$start - $end"
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
