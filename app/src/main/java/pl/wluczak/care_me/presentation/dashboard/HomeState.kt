package pl.wluczak.care_me.presentation.dashboard

import pl.wluczak.care_me.domain.model.Activity

data class NotificationItem(
    val id: Long,
    val activityName: String,
    val description: String,
    val timeRange: String
)

data class HomeState(
    val activities: List<Activity> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = true
)
