package pl.wluczak.care_me.presentation.dashboard

import pl.wluczak.care_me.domain.model.Activity

data class HomeState(
    val activities: List<Activity> = emptyList(),
    val isLoading: Boolean = true
)
